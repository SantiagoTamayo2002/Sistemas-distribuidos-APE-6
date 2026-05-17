"""
Bridge (Puente) entre MQTT y RabbitMQ.
Actúa como suscriptor (Subscriber) en MQTT para recibir los mensajes de telemetría de los vehículos,
y luego actúa como publicador (Publisher) hacia RabbitMQ utilizando el exchange 'exchange.fleet'.
Esto implementa un patrón de integración de mensajes.
"""
import paho.mqtt.client as mqtt
import pika, json

# Conexión a RabbitMQ usando credenciales por defecto (admin:admin123)
rabbit = pika.BlockingConnection(
    pika.ConnectionParameters("localhost", 5672,
        credentials=pika.PlainCredentials("admin", "admin123")))
channel = rabbit.channel()

# Declarar el exchange 'exchange.fleet' de tipo direct para enrutar mensajes específicamente
channel.exchange_declare(exchange="exchange.fleet", exchange_type="direct", durable=True)

def on_message(client, userdata, msg):
    """
    Callback que se ejecuta cada vez que se recibe un mensaje desde el broker MQTT.
    Determina la clave de enrutamiento (routing_key) basándose en el tópico MQTT
    y reenvía el mensaje a RabbitMQ.
    """
    topic = msg.topic
    payload = msg.payload.decode()
    
    # Asignación de routing_key según el tópico recibido
    if "/gps" in topic:
        routing_key = "gps.routing"
    elif "/temperatura" in topic:
        routing_key = "temp.alert"
    elif "/combustible" in topic:
        routing_key = "fuel.routing"
    else:
        return
        
    # Publicar el mensaje en el exchange de RabbitMQ
    channel.basic_publish(
        exchange="exchange.fleet",
        routing_key=routing_key,
        body=payload,
        properties=pika.BasicProperties(delivery_mode=2) # Mensajes persistentes
    )
    print(f"[Bridge] {topic} → {routing_key}")

# Configuración del cliente MQTT que escuchará todos los mensajes bajo el prefijo 'flota/#'
mqtt_client = mqtt.Client()
mqtt_client.on_message = on_message
mqtt_client.connect("localhost", 1883)
mqtt_client.subscribe("flota/#") # Suscripción con comodín (wildcard) a toda la flota

# Iniciar el bucle de escucha indefinidamente
mqtt_client.loop_forever()