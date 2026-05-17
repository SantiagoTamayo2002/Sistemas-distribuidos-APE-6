# Sistema de Monitoreo de Flota Distribuido (Agro-Smart / APE 006)

Este proyecto implementa un sistema distribuido para el monitoreo en tiempo real de una flota de vehículos utilizando tecnologías IoT y una arquitectura basada en microservicios.

## Arquitectura del Sistema
La solución está construida utilizando las siguientes capas tecnológicas:
1. **Capa de Dispositivos (Simulada):** Sensores IoT de la flota, que envían telemetría (GPS, Temperatura, Combustible) utilizando MQTT.
2. **Capa de Ingesta:** Broker MQTT (Mosquitto) y un **Bridge en Python** que actúa como puente para enrutar los mensajes hacia el sistema central.
3. **Capa de Enrutamiento:** RabbitMQ recibe los mensajes clasificados en distintas colas utilizando el patrón de intercambio asíncrono.
4. **Capa de Lógica de Negocio (Backend):** Aplicación Spring Boot (Fleet Monitor) que expone endpoints REST, consume alertas críticas mediante AMQP y gestiona el estado general del sistema.
5. **Capa de Presentación (Frontend):** Dashboard en HTML estático/JS que visualiza el estado, muestra un log de eventos y monitorea el tamaño de las colas y la disponibilidad de los microservicios.

## Patrones de Diseño Implementados
- **Publisher-Subscriber (Pub/Sub):** Utilizado extensivamente. Los vehículos actúan como _Publishers_ hacia el broker MQTT, mientras que el Bridge funciona como un _Subscriber_. Luego, el Bridge actúa como un _Publisher_ hacia RabbitMQ y las clases Java como `AlertConsumer` y `GpsConsumer` son los _Subscribers_ finales.
- **Event-Driven Architecture (EDA):** El sistema reacciona asíncronamente a los mensajes, como niveles bajos de combustible o altas temperaturas, encolando eventos que se notifican a través del backend sin bloquear procesos de otros vehículos.
- **Model-View-Controller (MVC):** Utilizado en el backend de Spring Boot, donde los controladores REST (`FleetController`) orquestan las peticiones.

## Endpoints del Backend (Spring Boot)
La API REST del backend se sirve en el puerto `8080`:

| Método | Endpoint | Descripción |
| ------ | -------- | ----------- |
| `GET` | `/api/fleet/status` | Devuelve el estado de salud y un resumen general de los vehículos y alertas activas. |
| `GET` | `/api/fleet/queues` | Retorna información en tiempo real de las colas de RabbitMQ (mensajes encolados y número de consumidores). |
| `GET` | `/api/fleet/vehicle/{id}/telemetria` | Endpoint para solicitar el historial de telemetría de un vehículo específico. |

## Instrucciones de Ejecución
1. Levantar los brokers de mensajería usando Docker:
   ```bash
   cd Docker
   docker-compose -f docker-compose-mosquitto.yml up -d
   docker-compose -f docker-compose-rabbitmq.yml up -d
   ```
2. Ejecutar los scripts de Python:
   ```bash
   cd mqtt-python
   # Instalar requerimientos si no lo has hecho: pip install -r requirements.txt
   python bridge.py &
   python simulator.py &
   ```
3. Levantar el Backend (Spring Boot):
   ```bash
   cd Fleet-monitor
   ./mvnw spring-boot:run
   ```
4. Abrir el Dashboard:
   Simplemente abre en tu navegador el archivo `Dahsboard/dashboard_flota.html` para ver la interfaz en funcionamiento.