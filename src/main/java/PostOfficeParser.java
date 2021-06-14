import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PostOfficeParser {

    public String htmlLookup(String sid) throws Exception {
        URL url = new URL("https://service.epost.go.kr/trace.RetrieveDomRigiTraceList.comm");
        HttpURLConnection http = (HttpURLConnection) url.openConnection();

        http.setDefaultUseCaches(false);
        http.setDoInput(true);
        http.setDoOutput(true);
        http.setRequestMethod("POST");
        http.setRequestProperty("content-type", "application/x-www-form-urlencoded");

        // -------------------------------
        //          REQUEST CODE
        // -------------------------------
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(http.getOutputStream(), "UTF-8");
        PrintWriter writer = new PrintWriter(outputStreamWriter);

        writer.write("sid1=" + sid);
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

        return builder.toString();
    }

    public List<ProgressVO> progressParse(String html) {
        List<ProgressVO> progressList = new ArrayList<>();

        Document doc = Jsoup.parseBodyFragment(html);

        // ---------------------------
        //    progress table parse
        // ---------------------------
        Elements rows = doc.select("table.detail_off tbody tr");
        for(Element row : rows) {
            Iterator<Element> iterator = row.getElementsByTag("td").iterator();

            ProgressVO progressVO = new ProgressVO();
            progressVO.setDate(iterator.next().text());
            progressVO.setTime(iterator.next().text());
            progressVO.setLocation(iterator.next().text());
            progressVO.setStatus(iterator.next().text());
            progressList.add(progressVO);
        }

        return progressList;
    }
}
