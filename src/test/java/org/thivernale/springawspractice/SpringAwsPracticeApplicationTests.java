package org.thivernale.springawspractice;

import io.awspring.cloud.dynamodb.DynamoDbOperations;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.thivernale.springawspractice.domain.InvoiceFactory;
import org.thivernale.springawspractice.domain.Order;
import org.thivernale.springawspractice.service.OrderService;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class SpringAwsPracticeApplicationTests {

    @Autowired
    private OrderService orderService;
    @Autowired
    private DynamoDbOperations operations;
    @MockitoSpyBean
    private InvoiceFactory invoiceFactory;

    @Test
    void contextLoads() {
        Order order = new Order("orderId", "productName", "userId", BigDecimal.valueOf(100L));
        orderService.createOrder(order);

        Order actual = operations.load(Key.builder()
            .partitionValue("orderId")
            .build(), Order.class);

        assertThat(actual)
            .usingRecursiveAssertion()
            .isEqualTo(order);

        verify(invoiceFactory, timeout(5000))
            .invoiceFor(assertArg(it -> assertThat(it.getOrderId())
                .isEqualTo(order.getOrderId())));
    }

}
