"""
Simulador de Sensores IoT para la Flota de Vehículos.
Genera datos aleatorios de telemetría (GPS, Temperatura, Combustible) para cada vehículo
y los publica en el broker MQTT de forma periódica utilizando el patrón Publisher.
"""
import paho.mqtt.client as mqtt
import json, time, random

# Inicialización del cliente MQTT
client = mqtt.Client()

# Conexión al broker MQTT local (Mosquitto) por el puerto por defecto (1883)
client.connect("localhost", 1883)

# Identificadores de los vehículos simulados
vehicles = ["VH-001", "VH-002", "VH-003"]

# Bucle infinito para publicar telemetría continuamente
while True:
    for vid in vehicles:
        # Publicar datos simulados de GPS
        client.publish(f"flota/{vid}/gps", json.dumps({
            "vehicleId": vid,
            "timestamp": time.strftime("%Y-%m-%dT%H:%M:%S"),
            "lat": round(-0.2295 + random.uniform(-0.01, 0.01), 6),
            "lng": round(-78.5243 + random.uniform(-0.01, 0.01), 6),
            "speed": round(random.uniform(0, 90), 1)
        }))
        # Publicar datos simulados de Temperatura
        client.publish(f"flota/{vid}/temperatura", json.dumps({
            "vehicleId": vid, "temp": round(random.uniform(-5, 30), 1)
        }))
        # Publicar datos simulados de nivel de Combustible
        client.publish(f"flota/{vid}/combustible", json.dumps({
            "vehicleId": vid, "nivel": round(random.uniform(10, 100), 1)
        }))
    
    # Espera 5 segundos antes de enviar el siguiente lote de datos
    time.sleep(5)