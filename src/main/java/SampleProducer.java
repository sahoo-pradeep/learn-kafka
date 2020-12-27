import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

public class SampleProducer {

  private static final KafkaProducer<String, String> kafkaProducer;
  private static final String TOPIC_PRODUCTS = "products";

  static {
    Properties properties = new Properties();
    //The Kafka broker's address
    properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

    //Id of the producer so that the broker can determine the source of the request
    properties.put(ProducerConfig.CLIENT_ID_CONFIG, "client1");

    //The class that will be used to serialize the key object
    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

    //The class that will be used to serialize the value object
    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

    kafkaProducer = new KafkaProducer<>(properties);
  }

  public static void main(String[] args) {
    sendProduct("name", "IPhone 11");
  }

  public static void sendProduct(String key, String value) {
    System.out.println("Sending message: [key = " + key + ", value = " + value + "]");
    ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_PRODUCTS, key, value);
    Future<RecordMetadata> futureRecord = kafkaProducer.send(record);

    try {
      RecordMetadata recordMetadata = futureRecord.get();
      System.out.println("Message sent successfully: " + recordMetadata);
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }

    kafkaProducer.close();
  }
}
