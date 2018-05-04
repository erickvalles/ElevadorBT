package warriorsoft.com.elevadorbt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class ListaDispositivos extends AppCompatActivity {
    ListView listaDispositivos;
    private BluetoothAdapter myBluetooth = null;
    private Set dispositivosPairados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_dispositivos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        listaDispositivos = findViewById(R.id.lvDispositivos);



        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if (myBluetooth==null){
            Snackbar.make(listaDispositivos,"Esta cosa no tiene bluetooth",Snackbar.LENGTH_INDEFINITE).show();
            finish();
        }else{
            if (!myBluetooth.isEnabled()){
                Intent encenderBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(encenderBt,1);
            }
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listaDeDispositivosConectados();

                Snackbar.make(view, "Cargando dispositivos", Snackbar.LENGTH_LONG)
                        .show();
            }
        });
    }


    private void listaDeDispositivosConectados(){
        dispositivosPairados = myBluetooth.getBondedDevices();
        ArrayList lista = new ArrayList();

        if(dispositivosPairados.size()>0){
            for (Iterator<BluetoothDevice> bt = dispositivosPairados.iterator(); bt.hasNext();){
                BluetoothDevice btD = bt.next();
                lista.add(btD.getName() + "\n" + btD.getAddress());
            }

        }else{
            Toast.makeText(getApplicationContext(),"No hay dispositivos conectados",Toast.LENGTH_LONG).show();
        }

        final ArrayAdapter adaptador = new ArrayAdapter(ListaDispositivos.this,android.R.layout.simple_list_item_1,lista);
        listaDispositivos.setAdapter(adaptador);
        listaDispositivos.setOnItemClickListener(myListClickListener);

    }


    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String informacion = ((TextView) view).getText().toString();
            String direccion = informacion.substring(informacion.length()-17);
            Intent i = new Intent(ListaDispositivos.this,ControlElevador.class);
            i.putExtra("direccion",direccion);
            startActivity(i);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lista_dispositivos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
