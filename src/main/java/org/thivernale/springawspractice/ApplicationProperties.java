package org.thivernale.springawspractice;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app")
public class ApplicationProperties {
    private final String bucketName;
    private final String queueName;
    private final String topicQueueName;
    private final String topicName;
}
