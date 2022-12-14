package avro;

import io.apicurio.registry.serde.SerdeConfig;
import io.apicurio.registry.serde.avro.AvroKafkaSerializer;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class Producer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Producer.class);

    public static void main(String[] args) {
        // Create configuration options for our producer and initialize a new producer
        Properties props = new Properties();

        props.putIfAbsent(SerdeConfig.REGISTRY_URL, Constants.REGISTRY_URL);

        props.putIfAbsent(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, Constants.BOOTSTRAP_SERVERS);
        props.putIfAbsent(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.putIfAbsent(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AvroKafkaSerializer.class.getName());
//        props.putIfAbsent(SerdeConfig.FIND_LATEST_ARTIFACT, Boolean.FALSE);     // if true it will start sending messages with the new schema before restarting!

        // Since we need to close our producer, we can use the try-with-resources statement to
        // create a new producer
        try (org.apache.kafka.clients.producer.Producer<String, GenericRecord> producer = new KafkaProducer<>(props)) {
            // here, we run an infinite loop to send a message to the cluster every second

            String schemaPath = "src/main/java/avro/thing.asvc";
            String valueSchemaString = "";

            try {
                valueSchemaString = new String(Files.readAllBytes(Paths.get(schemaPath)));
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
            Schema avroValueSchema = new Schema.Parser().parse(valueSchemaString);
            GenericRecord thisValueRecord = new GenericData.Record(avroValueSchema);

            for (int i = 0; ; i++) {
                thisValueRecord.put("code", "some code (" + i + ")");
                thisValueRecord.put("title", "some title (" + i + ")");

                // Send/produce the message on the Kafka Producer
                ProducerRecord<String, GenericRecord> producedRecord = new ProducerRecord<>(Constants.TOPIC, Constants.SUBJECT, thisValueRecord);
                producer.send(producedRecord);

                // log a confirmation once the message is written
                System.out.printf("sent message: %s, global id: %s\n", i, producedRecord.headers().toArray()[0].toString());

                try {
                    // Sleep for a second
                    Thread.sleep(1000);
                } catch (Exception e) {
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Could not start producer: " + e);
            e.printStackTrace();
        }
    }
}