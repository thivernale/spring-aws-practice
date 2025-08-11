package org.thivernale.springawspractice.domain;

import lombok.*;

import java.math.BigDecimal;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private String orderId;
    private String productName;
    private String userId;
    private BigDecimal amount;
}
