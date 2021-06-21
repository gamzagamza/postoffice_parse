package parser.cj;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import parser.DeliveryParser;
import parser.DeliveryTracking;
import parser.Progress;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CJParser implements DeliveryParser {

    private Map<String, String> setHeader() throws Exception {
        URL url = new URL("https://www.cjlogistics.com/ko/tool/parcel/tracking");
        HttpURLConnection http = (HttpURLConnection) url.openConnection();

        http.setDefaultUseCaches(false);
        http.setDoInput(true);
        http.setDoOutput(true);
        http.setRequestMethod("GET");

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

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("cookie", strCookie);
        headerMap.put("csrf", csrf);

        return headerMap;
    }

    private String lookupHtml(String sid, Map<String, String> headerMap) throws Exception {
        URL url = new URL("https://www.cjlogistics.com/ko/tool/parcel/tracking-detail");
        HttpURLConnection http = (HttpURLConnection) url.openConnection();

        http.setDefaultUseCaches(false);
        http.setDoInput(true);
        http.setDoOutput(true);
        http.setRequestMethod("POST");
        http.setRequestProperty("content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        http.setRequestProperty("Cookie", headerMap.get("cookie"));

        // -------------------------------
        //          REQUEST CODE
        // -------------------------------
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(http.getOutputStream(), "UTF-8");
        PrintWriter writer = new PrintWriter(outputStreamWriter);

        writer.write("_csrf=" + headerMap.get("csrf") + "&paramInvcNo="+sid);
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
        //System.out.println(builder.toString());
        return builder.toString();
    }

    @Override
    public DeliveryTracking progressParse(String sid) throws Exception {
        DeliveryTracking deliveryTracking = new DeliveryTracking();
        deliveryTracking.setProgressList(new ArrayList<>());

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(lookupHtml(sid, setHeader()));

        JsonObject parcelDetailResultMap = (JsonObject) jsonObject.getAsJsonObject("parcelDetailResultMap");
        JsonArray resultList = (JsonArray) parcelDetailResultMap.getAsJsonArray("resultList");

        for(int i = 0; i < resultList.size(); i++) {
            JsonObject result = (JsonObject) resultList.get(i);

            Progress progress = new Progress();
            progress.setDate(result.get("dTime").getAsString());
            progress.setTime(result.get("dTime").getAsString());
            progress.setLocation(result.get("regBranNm").getAsString());
            progress.setStatus(result.get("scanNm").getAsString());

            if(i == (resultList.size() - 1) && result.get("scanNm").getAsString().equals("배달완료")) {
                deliveryTracking.setComplete(true);
            }

            deliveryTracking.getProgressList().add(progress);
        }

        return deliveryTracking;
    }
}
