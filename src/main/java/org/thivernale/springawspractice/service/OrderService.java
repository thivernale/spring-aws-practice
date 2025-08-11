package org.thivernale.springawspractice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thivernale.springawspractice.domain.Order;
import org.thivernale.springawspractice.domain.OrderRepository;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public void createOrder(Order order) {
        orderRepository.save(order);
    }

    public Order findOrder(String orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow();
    }
}
