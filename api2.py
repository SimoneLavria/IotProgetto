
#Il server che gestisce tutte le richieste Api
#In teoria dovrebbe stare su un dispositivo diverso dallo smarthbox
#E la smarthbox invia le richieste tramite http

import uvicorn
from fastapi import FastAPI,Request,HTTPException
import random
from pymongo.mongo_client import MongoClient
from pymongo.server_api import ServerApi
from pydantic import BaseModel
import json
import base64

#How allow FastAPI to handle multiple requests at the same time?
"""
You need to set the number of workers as shown in the uvicorn settings
--workers <int>
"""
#Aprire il terminale ed eseguire $ uvicorn api2:my_awesome_api --reload
#Questa è la parte di codice che mi serve per permettermi di connetermi al database in mongodb
#Per creare un oggetto usando il terminale dopo aver fatto l'api
#Oppure aprire postman(programma)
#Step 3: Define the data model Create a Pydantic model to define the schema of the data you want to store in MongoDB.
"""file_data = {
    "posizione_sensore" : posizione_sensore,
    "distanza" : distanza,
    "angolo" : angolo,
    "data_ora" : data_ora,
    "data_minuto" : data_minuto,
    "data_giorno" : data_giorno,
    "data_mese" : data_mese,
    "id_trattore" : id_trattore
}"""
class Item(BaseModel):
    posizione_sensore: str
    distanza: int
    angolo: int
    data_ora: int
    data_minuto: int
    data_giorno: int
    data_mese: int
    id_trattore: int
    evento_accaduto:str

#Fine Step3

from pymongo.mongo_client import MongoClient
from pymongo.server_api import ServerApi

uri = "mongodb+srv://SimoneLavria:T4CtkD1h8LI6gSW3@cluster0.6b9wngj.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0"

# Create a new client and connect to the server
client = MongoClient(uri, server_api=ServerApi('1'))
# Send a ping to confirm a successful connection
try:
    client.admin.command('ping')
    print("Pinged your deployment. You successfully connected to MongoDB!")
except Exception as e:
    print("You failed to connected to MongoDb!")
    exit(-1)

#Fine della parte di codice che mi serve per permettermi di connetermi al database in mongodb

database=client["DatabaseCentrale"]
collection = database["DatiSensori"]
my_awesome_api = FastAPI()

"""
Ci sono varie richieste che posso fare
post per mettere un file nel database
get per prendere un file dal database
put per aggiornare un file dal database
delete per eliminare un file dal database

"""

@my_awesome_api.post('/items',response_model=Item)
def create_item(request: Request,item: Item):

    #Qui vado a verificare l'username e la password
    #Lo schema di autenticazione su cui lavoro è l'http basic auth    
    b = base64.b64encode(bytes('simonelavria:password', 'utf-8')) # bytes
    base64_str = b.decode('utf-8') # convert bytes to string
    stringa_da_cercare="Basic "+base64_str
    if(request.headers['authorization']==stringa_da_cercare): #Se username e password concidono allora aggiorna il database in cloud
        result = collection.insert_one(item.dict())
        print(str(result.inserted_id))
    else:
        raise HTTPException(status_code=401, detail="Wrong") #Altrimenti rifiuto la richiesta post e alzo un errore con codice 401
    return item


#Aprire il terminale ed eseguire $ uvicorn api2:my_awesome_api --reload



