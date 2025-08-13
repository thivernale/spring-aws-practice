package org.thivernale.springawspractice.infrastructure;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;
import org.thivernale.springawspractice.domain.Invoice;
import org.thivernale.springawspractice.domain.InvoiceFactory;
import org.thivernale.springawspractice.domain.Order;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PdfboxInvoiceFactory implements InvoiceFactory {
    private static byte[] generateInvoicePdf(Order order) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
                stream.setFont(PDType1Font.HELVETICA, 14);

                stream.beginText();
                stream.newLineAtOffset(50, 750); // Top-left position
                stream.showText("Invoice");
                stream.endText();

                stream.beginText();
                stream.newLineAtOffset(50, 700);
                stream.showText("Order ID: " + order.getOrderId());
                stream.newLineAtOffset(0, -20);
                stream.showText("Product: " + order.getProductName());
                stream.newLineAtOffset(0, -20);
                stream.showText("User ID: " + order.getUserId());
                stream.newLineAtOffset(0, -20);
                stream.showText("Amount: $" + String.format("%.2f", order.getAmount()));
                stream.newLineAtOffset(0, -20);
                stream.showText("Date: " + LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                stream.endText();

                stream.beginText();
                stream.newLineAtOffset(50, 600);
                stream.showText("Thank you for your purchase!");
                stream.endText();
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            // after stream is closed
            document.save(outputStream);

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Invoice invoiceFor(Order order) {
        return new Invoice(order, generateInvoicePdf(order));
    }
}
