package org.thivernale.springawspractice;

import io.awspring.cloud.dynamodb.DynamoDbOperations;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.thivernale.springawspractice.domain.Order;
import org.thivernale.springawspractice.domain.OrderRepository;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.math.BigDecimal;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class SpringAwsPracticeApplicationTests {

    @Autowired
    private OrderRepository repository;
    @Autowired
    private DynamoDbOperations operations;

    @Test
    void contextLoads() {
        Order order = new Order("orderId", "productName", "userId", BigDecimal.valueOf(100L));
        repository.save(order);

        Order actual = operations.load(Key.builder()
            .partitionValue("orderId")
            .build(), Order.class);

        Assertions.assertThat(actual)
            .usingRecursiveAssertion()
            .isEqualTo(order);
    }

}
