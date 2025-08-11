package org.thivernale.springawspractice.infrastructure;

import io.awspring.cloud.dynamodb.DynamoDbOperations;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.thivernale.springawspractice.domain.Order;
import org.thivernale.springawspractice.domain.OrderRepository;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DynamoDbOrderRepository implements OrderRepository {

    private final DynamoDbOperations operations;

    @Override
    public Optional<Order> findById(String orderId) {
        return Optional.ofNullable(operations.load(Key.builder()
            .partitionValue(orderId)
            .build(), Order.class));
    }

    @Override
    public void save(Order order) {
        operations.save(order);
    }
}
