package br.com.dev.btgpactual.orderms.service;

import br.com.dev.btgpactual.orderms.controller.dto.OrderResponse;
import br.com.dev.btgpactual.orderms.entity.OrderEntity;
import br.com.dev.btgpactual.orderms.listener.dto.OrderCreatedEvent;
import br.com.dev.btgpactual.orderms.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MongoTemplate mongoTemplate;


    public void save(OrderCreatedEvent event) {
        OrderEntity orderEntity = new OrderEntity(event);

        // Persiste o pedido
        orderRepository.save(orderEntity);
    }

    public Page<OrderResponse> findAllByCustomerId(Long customerId, PageRequest pageRequest) {

        Page<OrderEntity> orders = orderRepository.findAllByCustomerId(customerId, pageRequest);

        return orders.map(OrderResponse::fromEntity);
    }

    public BigDecimal findTotalOnOrdersByCustomerId(Long customerId) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("customerId").is(customerId)),
                group().sum("total").as("total"));

        AggregationResults<Document> response = mongoTemplate.aggregate(aggregation, "tb_orders", Document.class);

        String total = response.getUniqueMappedResult().get("total").toString();
        return !total.isEmpty() ? new BigDecimal(total) : BigDecimal.ZERO;
    }
}
