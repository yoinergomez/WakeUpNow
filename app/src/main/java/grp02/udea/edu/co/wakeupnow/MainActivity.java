package grp02.udea.edu.co.wakeupnow;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import grp02.udea.edu.co.wakeupnow.dao.AlarmaDAO;
import grp02.udea.edu.co.wakeupnow.modelo.Alarma;
import grp02.udea.edu.co.wakeupnow.view.ItemAlarma;
import grp02.udea.edu.co.wakeupnow.view.adapter.ItemAlarmaAdapter;


public class MainActivity extends AppCompatActivity {

    private ArrayList<ItemAlarma> itemAlarmas;
    private HashMap alarmasHM;
    private ListView lista;
    private Alarma alarma;
    private AlarmaDAO alarmaDAO;
    private SimpleDateFormat formatter ;
    private HashMap pendingintentsHM;
    private int numeroAlarmas;


    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            pendingintentsHM=new HashMap();
            alarmasHM=new HashMap();
            itemAlarmas = new ArrayList<ItemAlarma>();
            lista = (ListView) findViewById(R.id.ListView_alarmas);
            alarmaDAO=new AlarmaDAO(this);
            numeroAlarmas=alarmaDAO.getIdUltimoRegistro()+1;



            final TextView texoReloj = (TextView) findViewById(R.id.textoReloj);
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {


                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;


                    mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            final View view1 = view;
                            agregarAlarma(selectedHour, selectedMinute);
                            Snackbar.make(view1, "Alarma creada exitosamente!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
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

            Intent intent = new Intent(this, ControlAlarmaService.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this,cursor.getInt(0),
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            pendingintentsHM.put(cursor.getInt(0),pendingIntent);
            Alarma alarmaAux = new Alarma();
            alarmaAux.setFecha(fechaAlarma);
            if(cursor.getInt(3)==0){
                alarmaAux.setActivada(false);
            }else{
                alarmaAux.setActivada(true);
            }
            alarmaAux.setIdAlarma(cursor.getInt(0));
            alarmasHM.put(alarmaAux.getIdAlarma(),alarmaAux);
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
            setContentView(R.layout.acerca_de);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Crea una alarma indicando su hora:minuto de activación
     * @param hora
     * @param minuto
     */
    public void agregarAlarma(int hora, int minuto){
        Calendar calendar = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();

        //Inicio creaciòn del servicio de alarma
        calendar.set(Calendar.HOUR_OF_DAY, hora);
        calendar.set(Calendar.MINUTE, minuto);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        calendar2.set(Calendar.SECOND, 0);
        calendar2.set(Calendar.MILLISECOND, 0);
        if(calendar2.getTime().compareTo(calendar.getTime())!=-1){
            calendar.add(Calendar.DATE,1);
        }

        Intent intent = new Intent(this, ControlAlarmaService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this,numeroAlarmas,
                intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Log.d("@@@", "hora normal: " + calendar.getTimeInMillis());
        Log.d("@@@", "hora normal: " + calendar.getTime());
        //Fin creaciòn del servicio de alarma

        Date date = calendar.getTime();
        System.out.println(date==null);
        System.out.println(date.getTime());
        alarma = new Alarma();
        alarma.setFecha(date);
        alarma.setActivada(true);
        alarma.setIdAlarma(numeroAlarmas);
        Log.d("@@@", "numeroAlarmas:" + numeroAlarmas);
        pendingintentsHM.put(alarma.getIdAlarma(), pendingIntent);
        alarmasHM.put(alarma.getIdAlarma(),alarma);
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
            public void onEntrada(final Object entrada, View view) {
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
                ImageButton imageButton=(ImageButton)view.findViewById(R.id.imageButton);
                if (((ItemAlarma) entrada).isEstaActiva()) {
                    estado.setText(ItemAlarma.ACTIVADA);
                    activada.setChecked(true);
                } else {
                    estado.setText(ItemAlarma.DESACTIVADA);
                    activada.setChecked(false);
                }

                listenerClickSwitch(view, (ItemAlarma) entrada);
                listenerClickDelete(view, (ItemAlarma) entrada);
                listenerItemAlarma(view, (ItemAlarma) entrada);
            }
        });
    }

    private void listenerItemAlarma(View view, ItemAlarma item) {
        final ItemAlarma itemSeleccionado = item;
        final View view1 = view;
        final LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.linear_alarma);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editarAlarmaDialog(itemSeleccionado);
                CharSequence texto = "Seleccionado: " + itemSeleccionado.isEstaActiva();
                Toast toast = Toast.makeText(MainActivity.this, texto + " " + itemSeleccionado.getAlarma() + " " + itemSeleccionado.getHoraAlarma() + " " + itemSeleccionado.getMinutoAlarma(), Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    /**
     * Crea listener correspondiente al imageButton que permite eliminar una alarma
     * @param view
     * @param item
     */
    private void listenerClickDelete(View view, final ItemAlarma item) {
        final ItemAlarma itemSeleccionado = item;
        final View view1 = view;
        final ImageButton imageButton = (ImageButton) view.findViewById(R.id.imageButton);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Alarma alarmaAux=alarmas.get(((ItemAlarma) entrada).getIdItem());
                Alarma alarmaAux=(Alarma)alarmasHM.get(((ItemAlarma) item).getIdItem());
                AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                alarmManager.cancel((PendingIntent)pendingintentsHM.get(alarmaAux.getIdAlarma()));
                pendingintentsHM.remove(alarmaAux.getIdAlarma());
                alarmasHM.remove(alarmaAux.getIdAlarma());
                alarmaDAO.eliminarAlarma(alarmaAux);
                itemAlarmas.remove(item);
                cargarListaAdapter();

            }
        });

    }

    /**
     * Agrega el listener para el switch que permite activar o desactivar una alarma
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
                PendingIntent pendingIntent=(PendingIntent)pendingintentsHM.get(((ItemAlarma) itemSeleccionado).getIdItem());
                Calendar calendar = Calendar.getInstance();
                Alarma alarmaSeleccionada=(Alarma)alarmasHM.get(((ItemAlarma) itemSeleccionado).getIdItem());
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


    private void editarAlarmaDialog(final ItemAlarma itemAlarma){
        //Configurando dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_editar_alarma);
        dialog.setTitle("Editar alarma");

        //Cambiando hora en timepicker
        final TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.timePicker_reloj);
        int hora =Integer.parseInt(itemAlarma.getHoraAlarma());
        final int minutos = Integer.parseInt(itemAlarma.getMinutoAlarma());
        if(itemAlarma.isEsPM()){
            hora+=12;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(hora);
            timePicker.setMinute(minutos);
        } else {
            timePicker.setCurrentHour(hora);
            timePicker.setCurrentMinute(minutos);
        }


        final CheckBox checkBox0 = (CheckBox) dialog.findViewById(R.id.checkBox_lunes);
        final CheckBox checkBox1 = (CheckBox) dialog.findViewById(R.id.checkBox_martes);
        final CheckBox checkBox2 = (CheckBox) dialog.findViewById(R.id.checkBox_miercoles);
        final CheckBox checkBox3 = (CheckBox) dialog.findViewById(R.id.checkBox_jueves);
        final CheckBox checkBox4 = (CheckBox) dialog.findViewById(R.id.checkBox_viernes);
        final CheckBox checkBox5 = (CheckBox) dialog.findViewById(R.id.checkBox_sabado);
        final CheckBox checkBox6 = (CheckBox) dialog.findViewById(R.id.checkBox_domingo);


        dialog.findViewById(R.id.boton_cancelar)
            .setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    dialog.dismiss();
                }
            });

        dialog.findViewById(R.id.boton_editar)
                .setOnClickListener(new View.OnClickListener(){
                    @Override public void onClick(View v) {
                        final int horaEdit = timePicker.getCurrentHour();
                        final int minutosEdit = timePicker.getCurrentMinute();
                        itemAlarma.setHoraAlarma(horaEdit);
                        itemAlarma.setMinutoAlarma(minutosEdit);
                        Alarma alarma = (Alarma) alarmasHM.get(itemAlarma.getIdItem());
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(alarma.getFecha());
                        calendar.set(Calendar.HOUR_OF_DAY, horaEdit);
                        calendar.set(Calendar.MINUTE, minutosEdit);
                        alarma.setFecha(calendar.getTime());

                        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                        alarmManager.cancel((PendingIntent) pendingintentsHM.get(alarma.getIdAlarma()));
                        pendingintentsHM.remove(alarma.getIdAlarma());

                        Intent intent = new Intent(v.getContext(), ControlAlarmaService.class);
                        PendingIntent pendingIntent = PendingIntent.getService(v.getContext(),alarma.getIdAlarma(),
                                intent,PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                        pendingintentsHM.put(alarma.getIdAlarma(), pendingIntent);
                        alarmaDAO.actualizarAlarma(alarma);

                        final boolean lunes = checkBox0.isChecked();
                        final boolean martes = checkBox1.isChecked();
                        final boolean miercoles = checkBox2.isChecked();
                        final boolean jueves = checkBox3.isChecked();
                        final boolean viernes = checkBox4.isChecked();
                        final boolean sabado = checkBox5.isChecked();
                        final boolean domingo = checkBox6.isChecked();
                        cargarListaAdapter();
                        dialog.dismiss();
                    }
                });


        dialog.show();

    }
}
