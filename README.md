```java
@Controller
public class TrackingController {

    private final DeliveryParser deliveryParser = new PostOfficeParser();

    @GetMapping("/tracking")
    public ResponseEntity<DeliveryTracking> tracking(String trackingNumber) throws Excecption {
        return ResponseEntity.ok().body(deliveryParser.progressParse(trackingNumber));
    }
}
```
