package org.thivernale.springawspractice.service;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.logging.LoggersEndpoint;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.thivernale.springawspractice.ApplicationProperties;
import org.thivernale.springawspractice.domain.InvoiceRepository;
import org.thivernale.springawspractice.domain.Order;
import org.thivernale.springawspractice.domain.OrderCreated;
import org.thivernale.springawspractice.domain.OrderRepository;

import java.net.URL;

@Service
@RequiredArgsConstructor
public class OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final InvoiceRepository invoiceRepository;
    private final SqsTemplate sqsTemplate;
    private final ApplicationProperties applicationProperties;
    private final LoggersEndpoint loggersEndpoint;

    public void createOrder(Order order) {
        orderRepository.save(order);
        // publish OrderCreated to "order-queue" queue in SQS
        sqsTemplate.send(getQueueName(), new OrderCreated(order.getOrderId()));
        LOGGER.info("Created order: {}", order.getOrderId());
    }

    public String getQueueName() {
        return applicationProperties.getQueueName();
    }

    public Order findOrder(String orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow();
    }

    public Resource getInvoice(String orderId) {
        return invoiceRepository.findByOrderId(orderId);
    }

    public URL getInvoiceUrl(String orderId) {
        return invoiceRepository.findGetUrlByOrderId(orderId);
    }
}
