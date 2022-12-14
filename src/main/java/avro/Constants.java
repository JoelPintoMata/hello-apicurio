package avro;

public class Constants {

    public static final String BOOTSTRAP_SERVERS = "localhost:9092";

    public static final String REGISTRY_URL = "http://localhost:8080/apis/registry/v2";

    public static final String TOPIC = "my-kafka-topic";

    public static final String SUBJECT = "my-kafka-subject";

    public static final String SCHEMA = "{\n" +
            "   \"type\":\"record\",\n" +
            "   \"name\":\"Thing\",\n" +
            "   \"namespace\":\"com.example.avro\",\n" +
            "   \"fields\":[\n" +
            "      {\n" +
            "         \"name\":\"code\",\n" +
            "         \"type\":\"string\"\n" +
            "      },\n" +
            "      {\n" +
            "         \"name\":\"title\",\n" +
            "         \"type\":\"string\"\n" +
            "      }\n" +
            "   ]\n" +
            "}";
    public static final String SCHEMA_v2 = "{\n" +
            "  \"type\": \"record\",\n" +
            "  \"name\": \"thing\",\n" +
            "  \"namespace\": \"com.example\",\n" +
            "  \"fields\": [\n" +
            "    {\n" +
            "      \"name\": \"code\",\n" +
            "      \"type\": \"string\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"title\",\n" +
            "      \"type\": \"string\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"description\",\n" +
            "      \"type\": \"string\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";
}
