package ProducerConsumer;

import io.apicurio.registry.serde.SerdeConfig;
import io.apicurio.registry.serde.avro.AvroKafkaDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Consumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);

    public static void main(String[] args) {

        /* Sadly, we need to do some string parsing to deal with 'poison pill' records (i.e. any message that cannot be
        de-serialized by KafkaAvroDeserializer, most likely because they weren't produced using Schema Registry) so we
        need to set up some regex things
         */
        final Pattern offsetPattern = Pattern.compile("\\w*offset*\\w[ ]\\d+");
        final Pattern partitionPattern = Pattern.compile("\\w*" + Constants.TOPIC + "*\\w[-]\\d+");

        // Create configuration options for our consumer
        Properties props = new Properties();

        // Configure the client with the URL of Apicurio Registry
        props.setProperty(SerdeConfig.REGISTRY_URL, Constants.REGISTRY_URL);

        // Configure Kafka settings
        props.putIfAbsent(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Constants.BOOTSTRAP_SERVERS);
        props.putIfAbsent(ConsumerConfig.GROUP_ID_CONFIG, "Consumer-" + Constants.TOPIC);

        // Every time we consume a message from kafka, we need to "commit" - that is, acknowledge
        // receipts of the messages. We can set up an auto-commit at regular intervals, so that
        // this is taken care of in the background
        props.putIfAbsent(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.putIfAbsent(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.putIfAbsent(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // Configure deserializer settings
        props.putIfAbsent(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        props.putIfAbsent(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                AvroKafkaDeserializer.class.getName());

        // Since we need to close our consumer, we can use the try-with-resources statement to
        // create it
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            // Subscribe this consumer to the same topic that we wrote messages to earlier
            consumer.subscribe(Arrays.asList(Constants.TOPIC));
            // run an infinite loop where we consume and print new messages to the topic
            while (true) {
                // The consumer.poll method checks and waits for any new messages to arrive for the subscribed topic
                // in case there are no messages for the duration specified in the argument (1000 ms
                // in this case), it returns an empty list
                try {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(10));
                    for (ConsumerRecord<String, String> record : records) {
                        System.out.printf("received message: %s, global id: %s\n", record.value(), record.headers().toArray()[0].toString());
                    }
                } catch (Exception e) {
                    String text = e.getMessage();

                    // Parse the error message to get the partition number and offset, in order to `seek` past the poison pill.
                    Matcher mPart = partitionPattern.matcher(text);
                    Matcher mOff = offsetPattern.matcher(text);

                    mPart.find();
                    Integer partition = Integer.parseInt(mPart.group().replace(Constants.TOPIC + "-", ""));
                    mOff.find();
                    Long offset = Long.parseLong(mOff.group().replace("offset ", ""));

                    System.out.println(String.format(
                            "'Poison pill' found at partition {0}, offset {1} .. skipping", partition, offset));

                    consumer.seek(new TopicPartition(Constants.TOPIC, partition), offset + 1);
                    // Continue on
                }
            }
        }
    }
}