package org.thivernale.springawspractice.domain;

import java.util.Optional;

public interface OrderRepository {
    Optional<Order> findById(String orderId);

    void save(Order order);
}
