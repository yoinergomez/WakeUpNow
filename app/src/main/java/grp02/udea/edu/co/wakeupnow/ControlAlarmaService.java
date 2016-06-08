package grp02.udea.edu.co.wakeupnow;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class ControlAlarmaService extends Service {

    public static final int ID_NOTIFICACION_ALARMA = 22061995;
    private boolean isActivoServicio=false;
    private Vibrator vibrator;
    private Ringtone ringtone;

    public ControlAlarmaService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        activarSonidoVibracion();
        return START_NOT_STICKY;
    }

    /**
     * Desactiva la alarma y cancela la notidicacion creada
     */
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.i("@@@","onDestroy");
        if(isActivoServicio){
            isActivoServicio = false;
            vibrator.cancel();
            ringtone.stop();
            stopForeground(true);
        }
    }

    public void activarSonidoVibracion(){

        //Notificacion
        Log.i("@@@","activarSonidoVibracion");
        isActivoServicio = true;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_alarm_white_48dp)
                        .setContentTitle("WakeUpNow: Alarma activada")
                        .setContentText("Toca para apagar la alarma");
        Intent resultIntent = new Intent(this, AlarmaQR.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setOngoing(true);
        mBuilder.setAutoCancel(false);

        startForeground(ID_NOTIFICACION_ALARMA, mBuilder.build());



        //Vibracion
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 1000, 2000};
        vibrator.vibrate(pattern, 0);



        //Sonido alarma
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmSound);
        ringtone.setStreamType(AudioManager.STREAM_ALARM);
        ringtone.play();
    }

}
