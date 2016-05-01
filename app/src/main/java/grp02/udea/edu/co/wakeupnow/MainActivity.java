package grp02.udea.edu.co.wakeupnow;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import grp02.udea.edu.co.wakeupnow.dao.AlarmaDAO;
import grp02.udea.edu.co.wakeupnow.modelo.Alarma;
import grp02.udea.edu.co.wakeupnow.view.ItemAlarma;
import grp02.udea.edu.co.wakeupnow.view.adapter.ItemAlarmaAdapter;

public class MainActivity extends AppCompatActivity {

    private ArrayList<ItemAlarma> alarmas;
    private ListView lista;
    private int hora;
    private int minuto;
    private Alarma alarma;
    private AlarmaDAO alarmaDAO;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);


            alarmas = new ArrayList<ItemAlarma>();
            lista = (ListView) findViewById(R.id.ListView_alarmas);
            alarmaDAO=new AlarmaDAO(this);
            listenerListaAlarmas();

            final TextView texoReloj = (TextView) findViewById(R.id.textoReloj);
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;


                    mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            agregarAlarma(selectedHour, selectedMinute);
                            Toast.makeText(getApplicationContext(), selectedHour + ":" + selectedMinute + " " + timePicker.getBaseline(), Toast.LENGTH_LONG).show();
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


    /**
     *
     */
    public void agregarAlarma(int hora, int minuto){
        Calendar calendar = Calendar.getInstance();

        //Inicio creaciòn del servicio de alarma
        calendar.set(Calendar.HOUR_OF_DAY, hora);
        calendar.set(Calendar.MINUTE, minuto);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 11,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        //Fin creaciòn del servicio de alarma

        Date date = calendar.getTime();
        System.out.println(date==null);
        System.out.println(date.getTime());
        alarma = new Alarma();
        alarma.setFecha(date);
        alarmaDAO.guardarAlarma(alarma);
        Cursor cursor = alarmaDAO.getTodasLasAlarmas();
        while (cursor.moveToNext()){
            System.out.println(cursor.getString(2));
        }

        // Nuevo item de alarma
        ItemAlarma itemAlarma = new ItemAlarma();

        //Asignando valores
        if (hora>12){
            hora = hora-12;
            itemAlarma.setEsPM(true);
        } else if(hora==12){
            itemAlarma.setEsPM(true);
        }

        itemAlarma.setHoraAlarma(hora);
        itemAlarma.setMinutoAlarma(minuto);
        alarmas.add(itemAlarma);
        cargarListaAdapter();

    }


    public void cargarListaAdapter(){
        lista.setAdapter(new ItemAlarmaAdapter(this, R.layout.item_alarma, alarmas) {
            @Override
            public void onEntrada(Object entrada, View view) {
                TextView reloj = (TextView) view.findViewById(R.id.textoReloj);
                reloj.setText(((ItemAlarma) entrada).getAlarma());

                TextView horario = (TextView) view.findViewById(R.id.textHorario);
                if (((ItemAlarma) entrada).isEsPM()) {
                    horario.setText(ItemAlarma.PM);
                } else {
                    horario.setText(ItemAlarma.AM);
                }

                SwitchCompat activada = (SwitchCompat) view.findViewById(R.id.switch_activarAlarma);
                TextView estado = (TextView) view.findViewById(R.id.textView_estadoAlarma);
                if (((ItemAlarma) entrada).isEstaActiva()) {
                    estado.setText(ItemAlarma.ACTIVADA);
                    activada.setChecked(true);
                } else {
                    estado.setText(ItemAlarma.DESACTIVADA);
                    activada.setChecked(false);
                }

                listenerClickSwitch(view, (ItemAlarma) entrada);
            }
        });
    }

    /**
     * Agrega el listener para cada item de la lista de alarmas
     */
    public void listenerListaAlarmas(){
        lista.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> pariente, View view, int posicion, long id) {
                ItemAlarma elegido = (ItemAlarma) pariente.getItemAtPosition(posicion);

                CharSequence texto = "Seleccionado: " + elegido.isEstaActiva();
                Toast toast = Toast.makeText(MainActivity.this, texto + " " + elegido.getAlarma() + " " + elegido.getHoraAlarma() + " " + elegido.getMinutoAlarma(), Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    /**
     * Agrega el listener paara el switch que permite activar o desactivar una alarma
     * @param view
     * @param item
     */
    public void listenerClickSwitch(View view, ItemAlarma item){
        final ItemAlarma itemSeleccionado = item;
        final View view1 = view;
        final SwitchCompat activada = (SwitchCompat) view.findViewById(R.id.switch_activarAlarma);

        /**
         * Es necesario crear el OnCLick porque en el item de la alarma
         * existen varios componentes de la vista que son cliqueables
         */
        activada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        activada.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                final TextView estado = (TextView) view1.findViewById(R.id.textView_estadoAlarma);
                if(itemSeleccionado.isEstaActiva()) {
                    estado.setText(ItemAlarma.DESACTIVADA);
                    estado.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorDesactivado));
                    itemSeleccionado.setEstaActiva(false);
                } else {
                    estado.setText(ItemAlarma.ACTIVADA);
                    estado.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorActivado));
                    itemSeleccionado.setEstaActiva(true);
                }
            }
        });
    }
}
