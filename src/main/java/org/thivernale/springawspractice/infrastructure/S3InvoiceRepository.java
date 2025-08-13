package org.thivernale.springawspractice.infrastructure;

import io.awspring.cloud.s3.S3Operations;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.thivernale.springawspractice.domain.Invoice;
import org.thivernale.springawspractice.domain.InvoiceRepository;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class S3InvoiceRepository implements InvoiceRepository {
    private final S3Operations s3Operations;

    @Value("${app.bucketName:test-bucket}")
    private String bucketName;

    @Override
    public void store(Invoice invoice) {
        s3Operations.upload(bucketName, invoice.filename(), new ByteArrayInputStream(invoice.content()));
    }

    @Override
    public Resource findByOrderId(String orderId) {
        return s3Operations.download(bucketName, Invoice.fileNameFor(orderId));
    }

    @Override
    public URL findGetUrlByOrderId(String orderId) {
        return s3Operations.createSignedGetURL(bucketName, Invoice.fileNameFor(orderId), Duration.ofMinutes(10));
    }
}
