import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DeliveryTrackingVO {
    private boolean complete = false;
    private List<ProgressVO> progressList;
}
