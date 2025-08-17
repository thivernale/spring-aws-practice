package org.thivernale.springawspractice.service;

import io.awspring.cloud.sns.sms.SnsSmsOperations;
import io.awspring.cloud.sqs.annotation.SnsNotificationMessage;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.annotation.SqsListenerAcknowledgementMode;
import io.awspring.cloud.sqs.listener.acknowledgement.Acknowledgement;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.thivernale.springawspractice.domain.InvoiceFactory;
import org.thivernale.springawspractice.domain.InvoiceRepository;
import org.thivernale.springawspractice.domain.OrderCreated;
import org.thivernale.springawspractice.domain.OrderRepository;

import java.net.URL;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class OrderListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderListener.class);

    private final OrderRepository orderRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceFactory invoiceFactory;
    private final SnsSmsOperations smsOperations;

    @SqsListener(
        queueNames = "#{@orderService.getQueueName()}",
        acknowledgementMode = SqsListenerAcknowledgementMode.MANUAL,
        messageVisibilitySeconds = "20",
        maxMessagesPerPoll = "10",
        maxConcurrentMessages = "20",
        id = "order-created-listener"
    )
    void handleAsync(OrderCreated event, Acknowledgement acknowledgement) {
        LOGGER.info("Received event from SQS: {}", event);

        CompletableFuture.supplyAsync(() -> orderRepository.findById(event.orderId()))
            .thenApply(orderOptional -> {
                orderOptional.ifPresentOrElse(
                    order -> invoiceRepository.store(invoiceFactory.invoiceFor(order)),
                    () -> {
                        throw new RuntimeException("Order not found: %s".formatted(event.orderId()));
                    });
                return orderOptional;
            })
            .thenCompose(orderOptional -> {
                if (orderOptional.isPresent()) {
                    return acknowledgement.acknowledgeAsync();
                }
                return null;
            });

    }

    @SqsListener(
        queueNames = "#{@orderService.getTopicQueueName()}",
        messageVisibilitySeconds = "20",
        maxMessagesPerPoll = "10",
        maxConcurrentMessages = "20",
        id = "order-created-topic-listener"
    )
    void handle(@SnsNotificationMessage OrderCreated event) {
        LOGGER.info("Received event from SNS: {}", event);

        orderRepository.findById(event.orderId())
            .ifPresent(order -> {
                /*if ("throw".equals(order.getProductName())) {
                    order.setProductName("product");
                    orderRepository.save(order);
                    throw new RuntimeException("Invalid product name changed to: %s".formatted(order.getProductName()));
                }*/
                invoiceRepository.store(invoiceFactory.invoiceFor(order));
                URL signedUrl = invoiceRepository.findGetUrlByOrderId(order.getOrderId());
//            smsOperations.send("+00000", "Invoice created: %s".formatted(signedUrl));
            });
    }
}
