package org.thivernale.springawspractice;

import io.awspring.cloud.sqs.listener.MessageListenerContainer;
import io.awspring.cloud.sqs.listener.MessageListenerContainerRegistry;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.thivernale.springawspractice.domain.InvoiceRepository;
import org.thivernale.springawspractice.domain.Order;
import org.thivernale.springawspractice.service.OrderService;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Objects;

import static org.awaitility.Awaitility.await;

public class SpringCloudAwsSqsIntegrationTest extends BaseSqsIntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringCloudAwsSqsIntegrationTest.class);

    @Autowired
    protected OrderService orderService;
    @Autowired
    protected InvoiceRepository invoiceRepository;
    @Autowired
    protected SqsTemplate sqsTemplate;
    @Autowired
    protected ApplicationProperties applicationProperties;
    @Autowired
    private MessageListenerContainerRegistry registry;

    @Test
    public void testCreateOrder() {
        Order order = new Order("new-order-id", "productName", "userId", BigDecimal.valueOf(100L));
        orderService.createOrder(order);

        await()
            .atMost(Duration.ofSeconds(30))
            .untilAsserted(() -> {
                Assertions.assertThat(invoiceRepository.findByOrderId("new-order-id"))
                    .isNotNull();
            });
        assertQueueIsEmpty(applicationProperties.getQueueName(), "order-created-listener");
    }

    private void assertQueueIsEmpty(String queueName, String containerId) {
        MessageListenerContainer<?> container = Objects.requireNonNull(registry.getContainerById(containerId),
            () -> "Could not find container: %s".formatted(containerId));
        container.stop();

        // ...
        LOGGER.info("Checking for messages in queue {}", queueName);
        var message = sqsTemplate.receive(from -> from.queue(queueName)
            .pollTimeout(Duration.ofSeconds(5)));
        Assertions.assertThat(message)
            .isEmpty();
        LOGGER.info("No messages found in queue {}", queueName);

    }
}
