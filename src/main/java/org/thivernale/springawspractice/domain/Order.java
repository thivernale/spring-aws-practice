package org.thivernale.springawspractice.domain;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
public class Order {
    private String orderId;
    private String productName;
    private String userId;
    private BigDecimal amount;

    @DynamoDbPartitionKey
    public String getOrderId() {
        return orderId;
    }
}
