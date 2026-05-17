package com.fleet.monitor.controller;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Controlador REST que expone los endpoints para interactuar con el frontend (Dashboard).
 * Permite consultar el estado general del sistema y estadísticas de las colas de RabbitMQ.
 */
@RestController
@RequestMapping("/api/fleet")
@CrossOrigin(origins = "*") // Permite peticiones desde el dashboard HTML
public class FleetController {

    @Autowired
    private AmqpAdmin amqpAdmin; // Utilizado para inspeccionar el estado de las colas de RabbitMQ

    /**
     * Endpoint para comprobar el estado de salud y resumen de la flota.
     * @return Objeto JSON con el estado de los vehículos y alertas activas.
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getFleetStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("totalVehicles", 3);
        status.put("activeAlerts", 2);
        status.put("timestamp", new Date());
        status.put("online", true);
        return ResponseEntity.ok(status);
    }

    /**
     * Endpoint para obtener la telemetría histórica de un vehículo en particular.
     * @param id Identificador del vehículo.
     * @return Lista con los datos de telemetría.
     */
    @GetMapping("/vehicle/{id}/telemetria")
    public ResponseEntity<List<String>> getTelemetria(@PathVariable String id) {
        return ResponseEntity.ok(
            Collections.singletonList("Datos de telemetria para " + id));
    }

    /**
     * Endpoint para obtener información en tiempo real sobre las colas de RabbitMQ.
     * @return Lista de mapas con el nombre de la cola, cantidad de mensajes encolados y consumidores.
     */
    @GetMapping("/queues")
    public ResponseEntity<List<Map<String, Object>>> getQueues() {
        List<String> names = List.of(
            "cola.gps.telemetria",
            "cola.alertas.temperatura",
            "cola.combustible.nivel",
            "cola.notificaciones"
        );
        List<Map<String, Object>> result = new ArrayList<>();
        for (String name : names) {
            Map<String, Object> q = new HashMap<>();
            q.put("name", name);
            try {
                // Interroga a RabbitMQ sobre la información de la cola específica
                QueueInformation info = amqpAdmin.getQueueInfo(name);
                q.put("messages",  info != null ? info.getMessageCount()  : 0);
                q.put("consumers", info != null ? info.getConsumerCount() : 0);
            } catch (Exception e) {
                // Manejo de errores por si RabbitMQ no está disponible o la cola no existe
                q.put("messages",  0);
                q.put("consumers", 0);
            }
            result.add(q);
        }
        return ResponseEntity.ok(result);
    }
}