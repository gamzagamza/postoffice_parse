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
    public DeliveryTrackingVO progressParse(String sid) throws Exception {
        DeliveryTrackingVO deliveryTrackingVO = new DeliveryTrackingVO();
        deliveryTrackingVO.setProgressList(new ArrayList<>());

        Document doc = Jsoup.parseBodyFragment(htmlLookup(sid));

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
            deliveryTrackingVO.getProgressList().add(progressVO);
        }

        if(deliveryTrackingVO.getProgressList() != null && !deliveryTrackingVO.getProgressList().isEmpty()) {
            ProgressVO lastProgressVO = deliveryTrackingVO.getProgressList().get(deliveryTrackingVO.getProgressList().size() - 1);

            if (lastProgressVO.getStatus().indexOf("배달완료") != -1) {
                deliveryTrackingVO.setComplete(true);
            }
        }

        return deliveryTrackingVO;
    }
}
