package com.fleet.monitor.consumer;

import com.fleet.monitor.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor de RabbitMQ dedicado exclusivamente a procesar la telemetría GPS
 * de los vehículos en tiempo real.
 */
@Component
public class GpsConsumer {

    private static final Logger log = LoggerFactory.getLogger(GpsConsumer.class);

    /**
     * Escucha mensajes en la cola de telemetría GPS.
     * @param message Mensaje JSON con los datos de latitud, longitud y velocidad.
     */
    @RabbitListener(queues = RabbitMQConfig.GPS_QUEUE)
    public void consumeGps(String message) {
        try {
            // Retardo de 300ms para visualizar la ráfaga de mensajes en la cola sin crear un cuello de botella permanente
            Thread.sleep(300);
            log.info("GPS procesado: {}", message);
            // Aquí se podría guardar el historial de posiciones en una base de datos
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("Error procesando GPS: {}", e.getMessage());
        }
    }
}
