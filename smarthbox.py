#Lo script python prende due argomenti
#il primo argomento è l'id del trattore 
#il secondo argomento è il link http a cui mandare le richieste request


import json
import random
import asyncio
from paho.mqtt import client as mqtt_client
import requests
import asyncio
from bleak import BleakClient
from bleak import BleakScanner
import uuid;
import json;
import sys
from cryptography.fernet import Fernet

#La funzione che si occupa della connessione con il dispositivo ble è asincrona
async def run(msg) :
    mac_addr = "48:A9:36:6B:5C:EF"
    tx_charac = "0b89d2d4-0ea6-4141-86bb-0c5fb91ab14a"
    my_uuid = uuid.UUID(tx_charac)
    device_name_android = "BleApplication"
    devices = await BleakScanner.discover(timeout=5)
    for device in devices :
        if device.name == device_name_android :
            mac_addr = device.address
            async with BleakClient(mac_addr, timeout=5) as client :
                await client.write_gatt_char(my_uuid, bytearray(msg, 'utf-8'), response=False)
                await client.disconnect()


#tale funzione aggiorna il valore della caratteristica
#Lo smarthbox è un client che aggiorna il valore della caratteristica del server


def update_database(json_message) :
    collisione = 0
    inserire_nel_database = 0
    link=str(sys.argv[2])+'/items'
    f = Fernet('qHuVIhGOciC4PFRAxJ-mud3dtjMt7P_XOJoTT1j6BWA=')
    json_message_payload=json_message.payload
    json_message_payload_originale=f.decrypt(json_message_payload.decode('utf-8'))
    json_message=json.loads(json_message_payload_originale)
    angolo_oggetto = json_message['angolo']
    distanza_oggetto = json_message['distanza']
    if angolo_oggetto == 0 or angolo_oggetto == 180 :
        if distanza_oggetto <= 10 :
            collisione = 1
            json_message['evento_accaduto'] = 'Avvenuta Collisione'
            inserire_nel_database = 1
    if distanza_oggetto <= 10 and collisione == 0 :
        json_message['evento_accaduto'] = 'Pericolo Collisione'
        inserire_nel_database = 1
    if inserire_nel_database == 1 :
        try:
            requests.post(link, data=json.dumps(json_message))
            #Il link tramite ngrok
            #Per gli https endpoint ngrok si occuperà dei certificati TLS in modo automatico
        except:
            print("Errore nell'invio della richiesta rest")
        try:
            #Il Bluetooth Low Energy è una tecnologia di comunicazione che non utilizza internet
            #Il raggio d'azione massimo è minore di 100 metri
            #Quindi non sono stati implementati meccanismi di sicurezza
            asyncio.run(run(json.dumps(json_message)))
        except:
            print("Errore nella funzione che gestisce il Bluetooth Low Energy")

    return json_message


broker = 'broker.emqx.io'
port = 1883
topic = "valori_sensori"
client_id = f'subscribe-{random.randint(0, 100)}'
base=str(sys.argv[1])
topic=topic+base



def connect_mqtt() -> mqtt_client :
    def on_connect(client, userdata, flags, reason_code, properties) :
        if reason_code == 0 :
            print("Connected to MQTT Broker!")
        else :
            print("Failed to connect, return code %d\n", reason_code)

    client = mqtt_client.Client(mqtt_client.CallbackAPIVersion.VERSION2, client_id)
    client.on_connect = on_connect
    client.connect(broker, port)
    return client


def subscribe(client: mqtt_client) :
    def on_message(client, userdata, msg) :
        update_database(msg)
    client.subscribe(topic)
    client.on_message = on_message


def main() :
    client = connect_mqtt()
    subscribe(client)
    client.loop_forever()


if __name__ == '__main__' :
    main()
