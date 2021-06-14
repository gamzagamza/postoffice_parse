import java.util.List;

public class main {
    public static void main(String[] args) throws Exception {
        PostOfficeParser postOfficeParser = new PostOfficeParser();

        String html = postOfficeParser.htmlLookup("1234567890123");

        List<ProgressVO> progressList = postOfficeParser.progressParse(html);

        for(ProgressVO progressVO : progressList) {
            System.out.println(progressVO.getDate() + " " + progressVO.getTime() + " " + progressVO.getLocation() + " " + progressVO.getStatus());
        }

        ProgressVO lastProgressVO = progressList.get(progressList.size() - 1);
        if(lastProgressVO.getStatus().indexOf("배달완료") != -1) {
            System.out.println("배달완료!");
        }
    }
}
