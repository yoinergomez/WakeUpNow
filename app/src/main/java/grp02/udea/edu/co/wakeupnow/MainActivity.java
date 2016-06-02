package grp02.udea.edu.co.wakeupnow;

import android.app.Activity;
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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import grp02.udea.edu.co.wakeupnow.dao.AlarmaDAO;
import grp02.udea.edu.co.wakeupnow.modelo.Alarma;
import grp02.udea.edu.co.wakeupnow.view.ItemAlarma;
import grp02.udea.edu.co.wakeupnow.view.adapter.ItemAlarmaAdapter;


public class MainActivity extends AppCompatActivity {

    private ArrayList<ItemAlarma> itemAlarmas;
    private ArrayList<Alarma> alarmas;
    private ListView lista;
    private Alarma alarma;
    private AlarmaDAO alarmaDAO;
    private SimpleDateFormat formatter ;
    private ArrayList<PendingIntent> pendingIntents;
    private int numeroAlarmas;


    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            pendingIntents=new ArrayList<>();
            alarmas = new ArrayList<Alarma>();
            itemAlarmas = new ArrayList<ItemAlarma>();
            lista = (ListView) findViewById(R.id.ListView_alarmas);
            alarmaDAO=new AlarmaDAO(this);
            numeroAlarmas=alarmaDAO.getIdUltimoRegistro()+1;
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

                            /*
                            //new IntentIntegrator((Activity) MainActivity.this).initiateScan();
                            IntentIntegrator integrator = new IntentIntegrator((Activity) MainActivity.this);
                            integrator.setCaptureActivity(OrientacionPortraitScanner.class);
                            integrator.setPrompt("Escanee el codigo QR para apagar la alarma");
                            integrator.setOrientationLocked(true);
                            integrator.initiateScan();
                            */




                        }
                    }, hour, minute, false);//Yes 24 hour time
                    mTimePicker.setTitle("Seleccione hora");
                    mTimePicker.show();
                }
            });
        //cargando las itemAlarmas y pendingIntents almacenados previamente
        int hora;
        int minuto;
        Date fechaAlarma;
        Calendar c=Calendar.getInstance();
        Cursor cursor = alarmaDAO.getTodasLasAlarmas();
        while (cursor.moveToNext()) {
            try {
                fechaAlarma=formatter.parse(cursor.getString(2));
            } catch (ParseException e) {
                fechaAlarma=null;
            }
            c.setTime(fechaAlarma);
            hora=c.get(Calendar.HOUR_OF_DAY);
            minuto=c.get(Calendar.MINUTE);
            ItemAlarma itemAlarma = new ItemAlarma();
            if (hora > 12) {
                hora = hora - 12;
                itemAlarma.setEsPM(true);
            } else if (hora == 12) {
                itemAlarma.setEsPM(true);
            }

            Intent intent = new Intent(this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this,cursor.getInt(0),
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            pendingIntents.add(pendingIntent);
            Alarma alarmaAux = new Alarma();
            alarmaAux.setFecha(fechaAlarma);
            if(cursor.getInt(3)==0){
                alarmaAux.setActivada(false);
            }else{
                alarmaAux.setActivada(true);
            }
            alarmaAux.setIdAlarma(cursor.getInt(0));
            alarmas.add(alarmaAux);
            itemAlarma.setIdItem(cursor.getInt(0));
            itemAlarma.setHoraAlarma(hora);
            itemAlarma.setMinutoAlarma(minuto);
            itemAlarma.setEstaActiva(alarmaAux.isActivada());
            itemAlarmas.add(itemAlarma);
            cargarListaAdapter();
        }
        cursor.close();
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
     * Decodifica el código QR para obtener su respectivo texto
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case IntentIntegrator.REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {

                    IntentResult intentResult =
                            IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

                    if (intentResult != null) {

                        String contents = intentResult.getContents();
                        String format = intentResult.getFormatName();

                        Log.d("@ onActivityResult","CONTENIDO_DEL_QR: " + contents + ", FORMATO: " + format);
                    } else {
                        Log.e("@ onActivityResult", "IntentResult es NULL!");
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.e("@ onActivityResult", "CANCELADO");
                }
        }
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
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,numeroAlarmas,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Log.d("@@@", "hora normal: " + calendar.getTimeInMillis());
        //Fin creaciòn del servicio de alarma

        Date date = calendar.getTime();
        System.out.println(date==null);
        System.out.println(date.getTime());
        alarma = new Alarma();
        alarma.setFecha(date);
        alarma.setActivada(true);
        alarma.setIdAlarma(numeroAlarmas);
        Log.d("@@@", "numeroAlarmas:" + numeroAlarmas);
        pendingIntents.add(pendingIntent);
        alarmas.add(alarma);
        alarmaDAO.guardarAlarma(alarma);

        // Nuevo item de alarma
        ItemAlarma itemAlarma = new ItemAlarma();
        itemAlarma.setIdItem(numeroAlarmas);
        numeroAlarmas=numeroAlarmas+1;
        //Asignando valores
        if (hora>12){
            hora = hora-12;
            itemAlarma.setEsPM(true);
        } else if(hora==12){
            itemAlarma.setEsPM(true);
        }

        itemAlarma.setHoraAlarma(hora);
        itemAlarma.setMinutoAlarma(minuto);
        itemAlarmas.add(itemAlarma);
        cargarListaAdapter();

    }


    public void cargarListaAdapter(){
        lista.setAdapter(new ItemAlarmaAdapter(this, R.layout.item_alarma, itemAlarmas) {
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
     * Agrega el listener para cada item de la lista de itemAlarmas
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
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                PendingIntent pendingIntent=pendingIntents.get(itemSeleccionado.getIdItem());
                Calendar calendar = Calendar.getInstance();
                Alarma alarmaSeleccionada=alarmas.get(itemSeleccionado.getIdItem());
                int horaReinicio;
                int minutoReinicio;
                if (buttonView.isPressed()) {
                    final TextView estado = (TextView) view1.findViewById(R.id.textView_estadoAlarma);
                    if (isChecked) {
                        estado.setText(ItemAlarma.ACTIVADA);
                        estado.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorActivado));
                        itemSeleccionado.setEstaActiva(true);
                        alarmaSeleccionada.setActivada(true);
                        //Reinicio del servicio de alarma
                        horaReinicio=Integer.parseInt(itemSeleccionado.getHoraAlarma());
                        minutoReinicio=Integer.parseInt(itemSeleccionado.getMinutoAlarma());
                        if(itemSeleccionado.isEsPM()){
                            horaReinicio=horaReinicio+12;
                        }
                        Log.d("@@@","hora: "+ itemSeleccionado.getHoraAlarma() + itemSeleccionado.getMinutoAlarma());
                        calendar.set(Calendar.HOUR_OF_DAY,horaReinicio );
                        calendar.set(Calendar.MINUTE,minutoReinicio);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        Log.d("@@@", "hora modificada: " +  calendar.getTimeInMillis());
                        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

                    } else {
                        estado.setText(ItemAlarma.DESACTIVADA);
                        estado.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorDesactivado));
                        itemSeleccionado.setEstaActiva(false);
                        alarmaSeleccionada.setActivada(false);
                        alarmManager.cancel(pendingIntent);
                        Log.d("@@","item seleccionado: "+ itemSeleccionado.getIdItem());

                    }
                    alarmaDAO.actualizarAlarma(alarmaSeleccionada);
                }
            }
        });
    }
}
