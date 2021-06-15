

public class main {
    public static void main(String[] args) throws Exception {
        /*DeliveryParser deliveryParser = new PostOfficeParser();
        DeliveryTrackingVO deliveryTrackingVO = deliveryParser.progressParse("1234567890123");

        for(ProgressVO progressVO : deliveryTrackingVO.getProgressList()) {
            System.out.println(progressVO.getDate() + " " + progressVO.getTime() + " " + progressVO.getLocation() + " " + progressVO.getStatus());
        }

        System.out.println("STATUS : " + deliveryTrackingVO.isComplete());*/


        DeliveryParser cjParser = new CJParser();
        cjParser.progressParse("354826742933");
    }
}
