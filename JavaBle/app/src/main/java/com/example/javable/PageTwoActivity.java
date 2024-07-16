package com.example.javable;

import static android.bluetooth.BluetoothGattCharacteristic.PERMISSION_READ;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_NOTIFY;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_READ;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE;
import static android.bluetooth.BluetoothGattDescriptor.PERMISSION_WRITE;
import static android.bluetooth.BluetoothGattService.SERVICE_TYPE_PRIMARY;
import static android.content.ContentValues.TAG;
import static android.os.SystemClock.sleep;

import static java.sql.Types.NULL;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelUuid;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class PageTwoActivity extends AppCompatActivity {
    public static int id_trattore=-1;
    public int EXTERNAL_STORAGE_PERMISSION_CODE = 23;
    //Nessun trattore ha un id pari a -1
    //Spiegazione della variabile id_trattore
    /*La variabile id_trattore è pubblica quindi visibile da qualsiasi parte del programma */
    /*La variabile id_trattore è statica quindi */
    /*Le variabili statiche o le variabili di classe sono condivise tra tutte le istanze di una classe ed è possibile accedervi e modificarle senza creare un'istanza della classe. Facciamo un tuffo nel profondo per comprendere gli usi e l'implementazione delle variabili statiche in Java.
    */
    //Fine spiegazione della variabile id_trattore
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messaggi_visualizer);
        Button Ritorna_Menu = findViewById(R.id.buttonmenu);
        Ritorna_Menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PageTwoActivity.this.onDestroy();
            }
        });



        //Vado a definire una edit text view
        EditText testo_id_trattore = findViewById(R.id.editTextNumber);
        //vado a definire la text view che mi serve per visualizzare i
        //messaggi ricevuti
        TextView messaggio = (TextView)findViewById(R.id.TextViewMessaggio);
        //fine definizione edit text view
        //Inserisco un bottone per inserire l'id del trattore che mi interessa
        //ed associo a tale bottone un evento da fare nel caso in cui
        //viene cliccato
        Button aggiorna_id = findViewById(R.id.aggiorna_id);
        aggiorna_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cosa voglio che succeda quando clicco il bottone?
                //voglio che la variabile id_trattore venga aggiornata
                //prendendo come nuovo valore quello contenuto
                //nell Edit_text
                String valore_letto = testo_id_trattore.getText().toString();
                id_trattore=Integer.parseInt(valore_letto);
                //Alcune righe che non servono a nulla giusto per test
                //TextView messaggio = (TextView)findViewById(R.id.TextViewMessaggio);
                // messaggio.setText(valore_letto);
            }
        });



        //Fine inserisco un bottone per inserire l'id del trattore che mi interessa
        //fine associazione tra bottone ed evento
        //Dobbiamo andare a creare un gatt service sul dispostivo android
        //che permette di modificare le sue caratteristiche ad altri dispositivi bluetooth
        //quindi il servizio gatt è write
        //dopo di che aggiorniamo la view in base ai valore che man mano riceviamo

        //Andiamo a defininire l'UUID dei servizi e delle caratteristiche che vogliamo
        UUID SERVICE_UUID = UUID.fromString("795090c7-420d-4048-a24e-18e60180e23c");
        UUID CHARACTERISTIC_COUNTER_UUID = UUID.fromString("31517c58-66bf-470c-b662-e352a6c80cba");
        UUID CHARACTERISTIC_INTERACTOR_UUID = UUID.fromString("0b89d2d4-0ea6-4141-86bb-0c5fb91ab14a");
        UUID DESCRIPTOR_CONFIG_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
        //Fine definizione UUID
        // The BluetoothAdapter is required for any and all Bluetooth activity.

        //// The BluetoothAdapter is required for any and all Bluetooth activity.
        BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = mBluetoothManager.getAdapter();
        boolean isNameChanged =bluetoothAdapter.setName("BleApplication");

        //// The BluetoothAdapter is required for any and all Bluetooth activity.

        //We define an AdvertiseCallback
         AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                Log.i(TAG, "LE Advertise Started.");
            }

            @Override
            public void onStartFailure(int errorCode) {
                Log.w(TAG, "LE Advertise Failed: " + errorCode);
                //TextView messaggio = (TextView)findViewById(R.id.TextViewMessaggio);
                //messaggio.setText("Advertising Fallito");
                CharSequence text = "C'è un nell'advertasing!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(PageTwoActivity.this, text, duration);
                toast.show();

            }
        };
         //End Define AdvertiseCallback

// Some advertising settings. We don't set an advertising timeout
// since our device is always connected to AC power.
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setConnectable(true)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .build();

// Defines which service to advertise.
        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .build();

// Starts advertising.
        BluetoothLeAdvertiser mBluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        mBluetoothLeAdvertiser.startAdvertising(settings, data, mAdvertiseCallback);
        //Create Bluetooth GattService
        BluetoothGattService service = new BluetoothGattService(SERVICE_UUID, SERVICE_TYPE_PRIMARY);
        BluetoothGattCharacteristic counter = new BluetoothGattCharacteristic(CHARACTERISTIC_COUNTER_UUID, PROPERTY_READ | PROPERTY_NOTIFY, PERMISSION_READ);
        BluetoothGattDescriptor counterConfig = new BluetoothGattDescriptor(DESCRIPTOR_CONFIG_UUID, PERMISSION_READ | PERMISSION_WRITE);
        counter.addDescriptor(counterConfig);
        BluetoothGattCharacteristic interactor = new BluetoothGattCharacteristic(CHARACTERISTIC_INTERACTOR_UUID, PROPERTY_WRITE_NO_RESPONSE, PERMISSION_WRITE);
        service.addCharacteristic(counter);
        service.addCharacteristic(interactor);
        //EndBluetoothGattService

        //starting the server

        //GetApplicationContext

        //Sperando che vada bene

        BluetoothGattServerCallback mGattServerCallback = new BluetoothGattServerCallback() {
            @Override
            public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
                super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
                //Andiamo a vedere se entra almeno nella charatteristica write request
                Log.d(TAG,"Sei entrato nella funzione OnCharacteristic Write Request");
                if (CHARACTERISTIC_INTERACTOR_UUID.equals(characteristic.getUuid())) {
                    //Qui bisognerebe aggiungera una if per selezionare in base all'id del trattore
                    //per fare ciò bisogna cercare in value
                    //ma value come è stata fatta?
                    
                    TextView messaggio = (TextView)findViewById(R.id.TextViewMessaggio);
                    messaggio.setMovementMethod(new ScrollingMovementMethod());
                    String s = new String(value, StandardCharsets.UTF_8);
                    //Controllo se la stringa "id_trattore":$id_trattore è presente in s
                    String sotto_stringa="\"id_trattore\": "+Integer.toString(id_trattore);
                    boolean contiene = s.contains(sotto_stringa);
                    if(contiene)
                    {
                        messaggio.setText(s);
                        /*Salvo i dati in un file di testo che si trova nella cartella download*/
                        /*Questo file di testo terrà tutti i messaggi ricevuti dall'app tutte le volte che l'abbiamo utilizzata*/
                        /*Può essere facilmente visualizzato con un editor di testo*/
                        String s_primo=s+'\n';
                        savePublicly(s_primo);
                    }
                    String stringa_valore=messaggio.getText().toString();
                    if(stringa_valore.equals("-1"))
                    {
                        messaggio.setText(s);
                        String s_primo=s+'\n';
                        savePublicly(s_primo);
                    }
                    //fine if che bisogna aggiungere
                }
            }
        };
        Context mContext = getApplicationContext();
        BluetoothGattServer mGattServer = mBluetoothManager.openGattServer(mContext, mGattServerCallback);
        boolean controllo = mGattServer.addService(service);
        //Vado a controllare che il servizio fatto dal gatt server funzioni correttamente
        if(controllo==false)
        {

            //messaggio.setText("C'è un problema nel gatt server");
            CharSequence text = "C'è un problema nel gatt server!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this /* MyActivity */, text, duration);
            toast.show();
        }


    }
    public void savePublicly(String stringa) {
        // Requesting Permission to access External Storage
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                EXTERNAL_STORAGE_PERMISSION_CODE);
        // getExternalStoragePublicDirectory() represents root of external storage, we are using DOWNLOADS
        // We can use following directories: MUSIC, PODCASTS, ALARMS, RINGTONES, NOTIFICATIONS, PICTURES, MOVIES
        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        // Storing the data in file with name as geeksData.txt
        File file = new File(folder, "java_ble_android.txt");
        try {
            boolean append=true;
            FileOutputStream fileOutputStream = new FileOutputStream(file,append);
            //
            /* and set the boolean to true. That way, the data you write will be appended to the end of the file, rather than overwriting what was already there.*/
            //
            fileOutputStream.write(stringa.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

}


