package br.com.dev.btgpactual.orderms.controller;

import br.com.dev.btgpactual.orderms.controller.dto.ApiResponse;
import br.com.dev.btgpactual.orderms.controller.dto.OrderResponse;
import br.com.dev.btgpactual.orderms.controller.dto.PaginationResponse;
import br.com.dev.btgpactual.orderms.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/order")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/customers/{customerId}")
    public ResponseEntity<ApiResponse<OrderResponse>> listOrders(@PathVariable("customerId") Long customerId,
                                                                 @RequestParam(name = "page", defaultValue = "0") Integer page,
                                                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<OrderResponse> pageResponse = orderService.findAllByCustomerId(customerId, PageRequest.of(page, pageSize));

        BigDecimal totalOnOrders = orderService.findTotalOnOrdersByCustomerId(customerId);

        return ResponseEntity.ok(new ApiResponse<>(
                Map.of("totalOnOrders", totalOnOrders),
                pageResponse.getContent(),
                PaginationResponse.fromPage(pageResponse)
        ));
    }

}
