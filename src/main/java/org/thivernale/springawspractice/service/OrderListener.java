package org.thivernale.springawspractice.service;

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

    void handle(OrderCreated event) {
        LOGGER.info("Received event: {}", event);
        Optional<Order> orderOptional = orderRepository.findById(event.orderId());
        orderOptional.ifPresent(order -> {
            invoiceRepository.store(invoiceFactory.invoiceFor(order));
        });
    }
}
