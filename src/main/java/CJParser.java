import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class CJParser implements DeliveryParser {
    private String setHeader(String sid) throws Exception {
        URL url = new URL("https://www.cjlogistics.com/ko/tool/parcel/tracking");
        HttpURLConnection http = (HttpURLConnection) url.openConnection();

        http.setDefaultUseCaches(false);
        http.setDoInput(true);
        http.setDoOutput(true);
        http.setRequestMethod("GET");

        // -------------------------------
        //         RESPONSE CODE
        // -------------------------------
        InputStreamReader inputStreamReader = new InputStreamReader(http.getInputStream(), "UTF-8");
        BufferedReader reader = new BufferedReader(inputStreamReader);
        String str = "";
        String csrf = "";
        while((str = reader.readLine()) != null) {
            if(str.indexOf("name=\"_csrf\"") != -1) {
               csrf = str.substring(str.indexOf("value=\"") + 7, str.lastIndexOf("\""));
            }
        }

        Map<String, List<String>> header =  http.getHeaderFields();

        String strCookie = "";
        if(header.containsKey("Set-Cookie")) {
            strCookie = String.join("; ", header.get("Set-Cookie"));
        }

        // ----------------------------------------------------------------------------------

        return lookupHtml(strCookie, csrf, sid);
    }

    private String lookupHtml(String cookie, String csrf, String sid) throws Exception {
        System.out.println(cookie);
        System.out.println(csrf);
        System.out.println(sid);

        URL url = new URL("https://www.cjlogistics.com/ko/tool/parcel/tracking-detail");
        HttpURLConnection http = (HttpURLConnection) url.openConnection();

        http.setDefaultUseCaches(false);
        http.setDoInput(true);
        http.setDoOutput(true);
        http.setRequestMethod("POST");
        http.setRequestProperty("content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        http.setRequestProperty("Cookie", cookie);

        // -------------------------------
        //          REQUEST CODE
        // -------------------------------
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(http.getOutputStream(), "UTF-8");
        PrintWriter writer = new PrintWriter(outputStreamWriter);

        writer.write("_csrf=" + csrf + "&paramInvcNo="+sid);
        writer.flush();

        // -------------------------------
        //         RESPONSE CODE
        // -------------------------------
        InputStreamReader inputStreamReader = new InputStreamReader(http.getInputStream(), "UTF-8");
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuilder builder = new StringBuilder();
        String str = "";
        while((str = reader.readLine()) != null) {
            builder.append(str + "\n");
        }
        System.out.println(builder.toString());
        return builder.toString();
    }

    @Override
    public DeliveryTrackingVO progressParse(String sid) throws Exception {
        setHeader(sid);
        return null;
    }
}
