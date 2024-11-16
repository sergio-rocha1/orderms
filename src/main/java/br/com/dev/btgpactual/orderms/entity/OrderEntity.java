package br.com.dev.btgpactual.orderms.entity;

import br.com.dev.btgpactual.orderms.listener.dto.OrderCreatedEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.math.BigDecimal;
import java.util.List;

@Document(collection = "tb_orders")
@Getter
@Setter
@NoArgsConstructor
public class OrderEntity {

    @MongoId
    private Long orderId;

    @Indexed(name = "customer_id_index")
    private Long customerId;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal total;

    private List<OrderItem> items;

    public OrderEntity(OrderCreatedEvent event) {
        this.orderId = event.codigoPedido();
        this.customerId = event.codigoCliente();

        // Converte os itens do pedido em objetos OrderItem
        this.items = event.itens()
                .stream()
                .map(i -> new OrderItem(i.produto(), i.quantidade(), i.preco()))
                .toList();

        // calcula o total do pedido com base nos itens
        this.setTotal(this.items.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }
}
