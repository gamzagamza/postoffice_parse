package parser.po;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
import java.util.Iterator;

/**
 * 우체국 배송조회
 */
public class PostOfficeParser implements DeliveryParser {

    private String htmlLookup(String sid) throws Exception {
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

    @Override
    public DeliveryTracking progressParse(String sid) throws Exception {
        DeliveryTracking deliveryTracking = new DeliveryTracking();
        deliveryTracking.setProgressList(new ArrayList<>());

        Document doc = Jsoup.parseBodyFragment(htmlLookup(sid));

        // ---------------------------
        //    progress table parse
        // ---------------------------
        Elements rows = doc.select("table.detail_off tbody tr");
        for(Element row : rows) {
            Iterator<Element> iterator = row.getElementsByTag("td").iterator();

            Progress progress = new Progress();
            progress.setDate(iterator.next().text());
            progress.setTime(iterator.next().text());
            progress.setLocation(iterator.next().text());
            progress.setStatus(iterator.next().text());
            deliveryTracking.getProgressList().add(progress);
        }

        if(deliveryTracking.getProgressList() != null && !deliveryTracking.getProgressList().isEmpty()) {
            Progress lastProgress = deliveryTracking.getProgressList().get(deliveryTracking.getProgressList().size() - 1);

            if (lastProgress.getStatus().indexOf("배달완료") != -1) {
                deliveryTracking.setComplete(true);
            }
        }

        return deliveryTracking;
    }
}
