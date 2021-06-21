import parser.DeliveryParser;
import parser.DeliveryTracking;
import parser.Progress;
import parser.cj.CJParser;

public class main {
    public static void main(String[] args) throws Exception {
        DeliveryParser deliveryParser = new CJParser();
        DeliveryTracking deliveryTracking = deliveryParser.progressParse("354826742933");
    }
}
