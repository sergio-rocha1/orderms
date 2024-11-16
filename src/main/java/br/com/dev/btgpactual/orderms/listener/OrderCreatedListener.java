package br.com.dev.btgpactual.orderms.listener;

import br.com.dev.btgpactual.orderms.listener.dto.OrderCreatedEvent;
import br.com.dev.btgpactual.orderms.service.OrderService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.messaging.Message;

import static br.com.dev.btgpactual.orderms.config.RabbitMqConfig.ORDER_CREATED_QUEUE;

@Component
@AllArgsConstructor
public class OrderCreatedListener {

    private final Logger logger = LoggerFactory.getLogger(OrderCreatedListener.class);

    private final OrderService orderService;

    @RabbitListener(queues = ORDER_CREATED_QUEUE)
    public void listen(Message<OrderCreatedEvent> message) {
        logger.info("Message consumed: {}", message);

        // Persiste o pedido
        orderService.save(message.getPayload());
    }
}
