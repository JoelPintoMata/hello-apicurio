package avro;

import java.io.Serializable;

public class Price implements Serializable {
    private final String code;
    private final String title;

    public Price(String code, String title) {
        this.code = code;
        this.title = title;
    }
}
