
#In questo script verrà effettuata pure la crittografia del payload tramite un algoritmo di cifratura
#la password di cifratura/decifratura sarà  la stessa per i publisher ed i subscriver
#Si suppone che tale password rimanga segreta
#È stato ritenuto che un algoritmo di crittografia simmetrico sia in grado di fornire un adeguato livello di sicurezza
# https://cryptography.io/en/latest/fernet/



#Lo script prende da terminale due argomenti
#il primo argomento è l'id del trattore di cui vogliamo pubblicare i valori
#il secondo argomento è il ritardo costante che aggiungo tra due pubblicazione di messaggi successivi
import random
import time
import json
from paho.mqtt import client as mqtt_client
import sys
from cryptography.fernet import Fernet
import time
#key = Fernet.generate_key()
f = Fernet('qHuVIhGOciC4PFRAxJ-mud3dtjMt7P_XOJoTT1j6BWA=')
#Definisco il broker,la porta del broker,il client id 
#Costruisco il topic del client
broker = 'broker.emqx.io'
port = 1883
topic = "valori_sensori"
base=str(sys.argv[1])
topic=topic+base
client_id = f'publish-{random.randint(0, 1000)}'

def connect_mqtt():
    def on_connect(client, userdata, flags, reason_code, properties):
        if reason_code == 0:
            print("Connected to MQTT Broker!")
        else:
            print("Failed to connect, return code %d\n", reason_code)

    #client = mqtt_client.Client(client_id)
    client = mqtt_client.Client(mqtt_client.CallbackAPIVersion.VERSION2, client_id)
    # client.username_pw_set(username, password)
    client.on_connect = on_connect
    client.connect(broker, port)
    return client


def publish(client):
    msg_count = 0
    lista_posizione=["Anteriore","Posteriore","LateraleDestro","LateraleSinistro"]
    second_inziale=time.time()
    while True:
        posizione_sensore=random.choice(lista_posizione)
        distanza = random.randint(0, 10) #La distanza viene misurata in metri
        angolo =random.randint(0,180) # L'angolo tra il sensore e l'oggetto
        #Le varie informazioni su quando la misura è stata effettuata
        #Stiamo ipotizzando che il lidar fa una misurazione ogni minuto
        data_ora=random.randint(0,23)
        data_minuto=random.randint(0,60)
        data_giorno=random.randint(0,31)
        id_trattore=int(sys.argv[1]) 
        data_mese=3 #Ipotizziamo che le misurazioni per ora vengono fatte solo nel mese di marzo
        file_data = {
            "posizione_sensore":posizione_sensore,
            "distanza": distanza,
            "angolo":angolo,
            "data_ora":data_ora,
            "data_minuto":data_minuto,
            "data_giorno":data_giorno,
            "data_mese":data_mese,
            "id_trattore":id_trattore
        }
        msg = json.dumps(file_data)
        msg=f.encrypt(msg.encode('utf-8'))
        delay=int(sys.argv[2])
        time.sleep(delay)
        result = client.publish(topic, msg)
        # result: [0, 1]
        status = result[0]
        print("\n")
        if status == 0:
            print("inviato correttamente il messaggio to topic "+str(topic))
        else:
            print(f"Failed to send message to topic "+str(topic))
        msg_count += 1
        seconds_final = time.time() #in questo
        #average_time indica quanto ci si impiega a pubblicare un messaggio 
        average_time = (-second_inziale+seconds_final)/msg_count-delay #dobbiamo togliare il ritardo costante di time.sleep
        print("Tempo medio per mandare un messaggio (senza il ritardo costante di time.sleep) "+str(average_time))
        print("second_inziale "+str(second_inziale))
        print("seconds_finale "+str(seconds_final))
        print("msg_count"+str(msg_count))
        print(" \n ")
        if msg_count > 5000:
            break


def run():
    client = connect_mqtt()
    client.loop_start()
    publish(client)
    client.loop_stop()
    print("Fine")


if __name__ == '__main__':
    run()
