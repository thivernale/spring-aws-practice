package org.thivernale.springawspractice.api;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import org.thivernale.springawspractice.domain.Order;
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

    @GetMapping("{orderId}/invoice")
    public ResponseEntity<Resource> invoice(@PathVariable("orderId") String orderId) {
        Resource resource = orderService.getInvoice(orderId);
        return ResponseEntity.status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                .filename(resource.getFilename())
                .build()
                .toString())
            .body(resource);
    }

    @GetMapping("{orderId}/invoice-redirect")
    public RedirectView invoiceRedirect(@PathVariable("orderId") String orderId) {
        return new RedirectView(orderService.getInvoiceUrl(orderId)
            .toString());
    }
}
