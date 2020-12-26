import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;

public class TopicCreator {

  private static final AdminClient adminClient;
  private static final String TOPIC_PRODUCTS = "products";

  static {
    Properties properties = new Properties();

    //The Kafka broker's address
    properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

    adminClient = AdminClient.create(properties);
  }

  public static void main(String[] args) {
    createTopic(TOPIC_PRODUCTS, 2, (short) 1);
  }

  public static void createTopic(String topicName, int partitions, short replicationFactor) {
    NewTopic topic = new NewTopic(topicName, partitions, replicationFactor);

    List<NewTopic> topics = new ArrayList<>();
    topics.add(topic);

    adminClient.createTopics(topics);
    adminClient.close();
  }

}
