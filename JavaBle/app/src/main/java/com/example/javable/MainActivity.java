package com.example.javable;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;



public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView stato_bluetooth = findViewById(R.id.StatoBluetooth);
        stato_bluetooth.setText("App Bluetooth Low energy");
        TextView ultimo_messaggio = findViewById(R.id.UltimoMessaggioRicevuto);
        ultimo_messaggio.setText("L'app visualizza i messaggi ricevuti dal raspberry,per attivare il servizio clicca il bottone visualizza messaggi.Assicurati di attivare il bluetooth.");
        //Vado ad implementare bottone per chiudere applicazione
        Button Bottone=findViewById(R.id.Bottone);
        Button ButtonMessaggi=findViewById(R.id.ButtonMessaggi);
        Bottone.setOnClickListener(v -> {
            MainActivity.this.onDestroy(); //chiude l'applicazione
        });

        ButtonMessaggi.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PageTwoActivity.class);
            startActivity(intent);
        });
        //fine main layout initial
        }
}
