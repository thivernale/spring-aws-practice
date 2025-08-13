package org.thivernale.springawspractice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.thivernale.springawspractice.domain.InvoiceRepository;
import org.thivernale.springawspractice.domain.Order;
import org.thivernale.springawspractice.domain.OrderRepository;

import java.net.URL;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final InvoiceRepository invoiceRepository;

    public void createOrder(Order order) {
        orderRepository.save(order);
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
