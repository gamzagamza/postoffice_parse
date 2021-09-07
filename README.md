## jitpack (with maven)
```
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>
    
    <dependency>
	    <groupId>com.github.gamzagamza</groupId>
	    <artifactId>postoffice_parse</artifactId>
	    <version>Tag</version>
	</dependency>
```

## Java Code
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
