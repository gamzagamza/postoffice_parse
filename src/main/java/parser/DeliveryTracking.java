package parser;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DeliveryTracking {
    private boolean complete = false;
    private List<Progress> progressList;
}
