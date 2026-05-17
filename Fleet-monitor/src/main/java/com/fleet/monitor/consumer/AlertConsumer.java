package com.fleet.monitor.consumer;

import com.fleet.monitor.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Consumidor de RabbitMQ que escucha las colas de alertas críticas
 * (Temperatura y Combustible) para procesarlas de manera asíncrona.
 * Implementa el patrón Event-Driven.
 */
@Component
public class AlertConsumer {

    private static final Logger log = LoggerFactory.getLogger(AlertConsumer.class);
    private final RabbitTemplate rabbitTemplate;

    public AlertConsumer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Escucha mensajes en la cola de alertas de temperatura.
     * Al recibir una alerta, la registra y la reenvía a la cola de notificaciones.
     */
    @RabbitListener(queues = RabbitMQConfig.TEMP_ALERT_QUEUE)
    public void consumeTempAlert(String message) {
        try {
            Thread.sleep(300); // Retardo de 300ms para visualizar colas sin saturarlas
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.warn("ALERTA TEMPERATURA PROCESADA: {}", message);
        rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATION_QUEUE, message);
    }

    /**
     * Escucha mensajes en la cola de alertas de combustible.
     * Al recibir una alerta, la registra y la reenvía a la cola de notificaciones.
     */
    @RabbitListener(queues = RabbitMQConfig.FUEL_QUEUE)
    public void consumeFuelAlert(String message) {
        try {
            Thread.sleep(300); // Retardo de 300ms para visualizar colas sin saturarlas
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.warn("ALERTA COMBUSTIBLE PROCESADA: {}", message);
        rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATION_QUEUE, message);
    }
}
