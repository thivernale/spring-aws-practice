package org.thivernale.springawspractice.api;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import org.thivernale.springawspractice.domain.Order;
import org.thivernale.springawspractice.domain.OrderCreated;
import org.thivernale.springawspractice.service.OrderService;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody Order order) {
        orderService.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(order.getOrderId());
    }

    @GetMapping("{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable("orderId") String orderId) {
        return ResponseEntity.ok(orderService.findOrder(orderId));
    }

    @PostMapping("{orderId}/notify")
    public ResponseEntity<Void> sendNotification(@PathVariable("orderId") String orderId) {
        orderService.sendNotification(new OrderCreated(orderId + " notify"));
        return ResponseEntity.noContent()
            .build();
    }

    @GetMapping("{orderId}/invoice")
    public ResponseEntity<Resource> invoice(@PathVariable("orderId") String orderId) {
        Resource resource = orderService.getInvoice(orderId);
        if (resource.exists()) {
            return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                    .filename(resource.getFilename())
                    .build()
                    .toString())
                .body(resource);
        }
        return ResponseEntity.notFound()
            .build();
    }

    @GetMapping("{orderId}/invoice-redirect")
    public RedirectView invoiceRedirect(@PathVariable("orderId") String orderId) {
        return new RedirectView(orderService.getInvoiceUrl(orderId)
            .toString());
    }
}
