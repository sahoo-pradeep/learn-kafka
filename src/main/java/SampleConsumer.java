import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class SampleConsumer {

  private static final Consumer<String, String> consumer;
  private static final String TOPIC_PRODUCTS = "products";

  static {
    Properties properties = new Properties();

    //The Kafka broker's address
    properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

    //The consumer group id used to identify to which group this consumer belongs
    properties.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());

    //The class that will be used to serialize the key object
    properties
        .put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

    //The class that will be used to serialize the value object
    properties
        .put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

    // What to do when there is no initial offset in Kafka:
    // earliest: automatically reset the offset to the earliest offset
    // latest: automatically reset the offset to the latest offset
    properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    consumer = new KafkaConsumer<>(properties);
  }

  public static void main(String[] args) {
    runConsumer();
  }

  public static void runConsumer() {
    consumer.subscribe(Collections.singletonList(TOPIC_PRODUCTS));

    while (true) {
      Duration timeout = Duration.of(30, ChronoUnit.SECONDS);

      ConsumerRecords<String, String> records = consumer.poll(timeout);

      if (records.count() == 0) {
        System.out.println("No message found for " + timeout);
        break;
      }

      records.forEach(record -> {
        System.out.println("Received Message...");
        System.out.println("Key: " + record.key());
        System.out.println("Value: " + record.value());
        System.out.println("Partition: " + record.partition());
        System.out.println("Offset: " + record.offset());
        System.out.println("Timestamp: " + getDateTime(record.timestamp()));
      });

      //Commit the offset of record to broker
      consumer.commitAsync();
    }

    consumer.close();
  }

  private static LocalDateTime getDateTime(long millis) {
    return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime();
  }

}
