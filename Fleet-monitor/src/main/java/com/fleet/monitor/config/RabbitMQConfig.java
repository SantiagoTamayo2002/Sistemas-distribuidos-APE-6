package com.fleet.monitor.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para el sistema de monitoreo.
 * Define las colas (Queues), el Intercambio (Exchange) y los Enlaces (Bindings)
 * necesarios para implementar el patrón Pub/Sub y enrutar los mensajes asíncronos.
 */
@Configuration
public class RabbitMQConfig {

    // Nombres de las colas y del exchange
    public static final String GPS_QUEUE = "cola.gps.telemetria";
    public static final String TEMP_ALERT_QUEUE = "cola.alertas.temperatura";
    public static final String FUEL_QUEUE = "cola.combustible.nivel";
    public static final String NOTIFICATION_QUEUE = "cola.notificaciones";
    public static final String FLEET_EXCHANGE = "exchange.fleet";

    /**
     * Declara un exchange de tipo Direct para enrutar los mensajes a colas específicas
     * basándose en un 'routing key' (clave de enrutamiento).
     */
    @Bean
    public DirectExchange fleetExchange() {
        return new DirectExchange(FLEET_EXCHANGE);
    }

    // --- Declaración de Colas (Durables para evitar pérdida de mensajes en reinicios) ---

    @Bean
    public Queue gpsQueue() {
        return new Queue(GPS_QUEUE, true);
    }

    @Bean
    public Queue tempAlertQueue() {
        return new Queue(TEMP_ALERT_QUEUE, true);
    }

    @Bean
    public Queue fuelQueue() {
        return new Queue(FUEL_QUEUE, true);
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue(NOTIFICATION_QUEUE, true);
    }

    // --- Enlaces (Bindings) entre el Exchange y las Colas usando Routing Keys ---

    @Bean
    public Binding gpsBinding(Queue gpsQueue, DirectExchange fleetExchange) {
        return BindingBuilder.bind(gpsQueue).to(fleetExchange).with("gps.routing");
    }

    @Bean
    public Binding tempBinding(Queue tempAlertQueue, DirectExchange fleetExchange) {
        return BindingBuilder.bind(tempAlertQueue).to(fleetExchange).with("temp.alert");
    }

    @Bean
    public Binding fuelBinding(Queue fuelQueue, DirectExchange fleetExchange) {
        return BindingBuilder.bind(fuelQueue).to(fleetExchange).with("fuel.routing");
    }
}