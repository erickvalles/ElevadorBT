package warriorsoft.com.elevadorbt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class ControlElevador extends AppCompatActivity implements View.OnClickListener{
    Button btn1, btn2, btn3;
    String direccion = null;
    BluetoothAdapter myBluetooth;
    BluetoothSocket btSocket = null;
    private boolean isBtConectado = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_elevador);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent intent = getIntent();
        direccion = intent.getStringExtra("direccion");

        btn1 = findViewById(R.id.btnPiso1);
        btn2 = findViewById(R.id.btnPiso2);
        btn3 = findViewById(R.id.btnPiso3);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);

        new ConectarBT().execute();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                desconectar();

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnPiso1:
                irAPiso("1");
                break;
            case R.id.btnPiso2:
                irAPiso("2");
                break;
            case R.id.btnPiso3:
                irAPiso("3");
                break;
        }
    }


    private class ConectarBT extends AsyncTask<Void, Void, Void>{
        private boolean conexionExitosa = true;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(),"Intentando conectar",Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
          try{
              if(btSocket == null || !isBtConectado){
                  myBluetooth = BluetoothAdapter.getDefaultAdapter();
                  BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(direccion);
                  btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                  BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                  btSocket.connect();
              }
          }catch (IOException e){
              conexionExitosa = false;
          }
          return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(!conexionExitosa){
                mensaje("Conexión fallida, Vuelve a intentar");
            }else{
                mensaje("Conectado");
                isBtConectado = true;
            }
        }


    }

    private void mensaje(String msj){
        Toast.makeText(getApplicationContext(),msj,Toast.LENGTH_LONG).show();
    }

    private void desconectar(){
        if(btSocket!=null){
            try{
                btSocket.close();
                mensaje("Desconectado");
            }catch (IOException e){
                mensaje("Error: "+e.getMessage());
            }
        }
    }


    private void irAPiso(String piso){
        if(btSocket != null){
            try{
                btSocket.getOutputStream().write(piso.toString().getBytes());
                Toast.makeText(getApplicationContext(),"Yendo al piso "+piso,Toast.LENGTH_SHORT).show();
            }catch (IOException e){
                mensaje("Error"+e.getMessage());
            }
        }
    }

}
