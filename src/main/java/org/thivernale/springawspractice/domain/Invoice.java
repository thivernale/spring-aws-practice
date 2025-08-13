package org.thivernale.springawspractice.domain;

public record Invoice(String filename, byte[] content) {
    public Invoice(Order order, byte[] content) {
        this(fileNameFor(order.getOrderId()), content);
    }

    public static String fileNameFor(String orderId) {
        return "invoice-%s.pdf".formatted(orderId);
    }
}
