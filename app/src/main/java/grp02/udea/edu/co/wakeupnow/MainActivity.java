package grp02.udea.edu.co.wakeupnow;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;

import grp02.udea.edu.co.wakeupnow.view.ItemAlarma;
import grp02.udea.edu.co.wakeupnow.view.adapter.ItemAlarmaAdapter;

public class MainActivity extends AppCompatActivity {

    private ArrayList<ItemAlarma> alarmas;
    private int hora;
    private int minuto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        alarmas = new ArrayList<ItemAlarma>();


        final TextView texoReloj = (TextView) findViewById(R.id.textoReloj);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //agregarAlarma();
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;


                mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        actualizarListaAlarmas(selectedHour, selectedMinute);
                        Toast.makeText(getApplicationContext(),selectedHour+":"+selectedMinute+" "+timePicker.getBaseline(),Toast.LENGTH_LONG).show();
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Seleccione hora");
                mTimePicker.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    /*
    public void agregarAlarma(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_agregar_alarma);
        dialog.show();


        Button cancelar = (Button) findViewById(R.id.boton_cancelar);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        final int hora;
        String minuto;
        final TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker_reloj);

        Button aceptar = (Button) findViewById(R.id.boton_aceptar);
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int hora=timePicker.getHour();
                final int minuto = timePicker.getMinute();
                Toast.makeText(null,hora+":"+minuto,Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        //Toast.makeText(this,hora+":"+minuto,Toast.LENGTH_LONG).show();

    }
    */

    /**
     *
     */
    public void actualizarListaAlarmas(int hora, int minuto){
        // Nuevo item de alarma
        ItemAlarma itemAlarma = new ItemAlarma();

        //Asignando valores
        if (hora>12){
            hora = hora-12;
            itemAlarma.setEsPM(true);
        }

        itemAlarma.setHoraAlarma(hora);
        itemAlarma.setMinutoAlarma(minuto);
        alarmas.add(itemAlarma);
        cargarListaAdapter();

    }


    public void cargarListaAdapter(){
        ListView lista = (ListView) findViewById(R.id.ListView_alarmas);
        lista.setAdapter(new ItemAlarmaAdapter(this, R.layout.item_alarma, alarmas){
            @Override
            public void onEntrada(Object entrada, View view) {
                TextView reloj = (TextView) view.findViewById(R.id.textoReloj);
                reloj.setText(((ItemAlarma) entrada).getAlarma());

                TextView horario = (TextView) view.findViewById(R.id.textHorario);
                if(((ItemAlarma) entrada).isEsPM()){
                    horario.setText(ItemAlarma.PM);
                } else{
                    horario.setText(ItemAlarma.AM);
                }

                TextView estado = (TextView) view.findViewById(R.id.textView_estadoAlarma);
                if(((ItemAlarma) entrada).isEstaActiva()){
                    estado.setText(ItemAlarma.ACTIVADA);
                    SwitchCompat activada = (SwitchCompat) findViewById(R.id.switch_activarAlarma);
                } else{
                    estado.setText(ItemAlarma.DESACTIVADA);
                }

            }
        });
    }
}
