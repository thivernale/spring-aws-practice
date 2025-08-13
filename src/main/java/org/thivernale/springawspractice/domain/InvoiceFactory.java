package org.thivernale.springawspractice.domain;

public interface InvoiceFactory {
    Invoice invoiceFor(Order order);
}
