package org.thivernale.springawspractice.service;

import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.thivernale.springawspractice.domain.*;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderListener.class);

    private final OrderRepository orderRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceFactory invoiceFactory;

    @SqsListener(
        queueNames = "#{@orderService.getQueueName()}",
        //acknowledgementMode = SqsListenerAcknowledgementMode.MANUAL,
        messageVisibilitySeconds = "200",
        maxMessagesPerPoll = "10",
        maxConcurrentMessages = "20"
    )
    void handle(OrderCreated event/*, Acknowledgement acknowledgement*/) {
        LOGGER.info("Received event: {}", event);
        Optional<Order> orderOptional = orderRepository.findById(event.orderId());
        orderOptional.ifPresent(order -> {
            invoiceRepository.store(invoiceFactory.invoiceFor(order));
            //acknowledgement.acknowledge();
        });
    }
}
