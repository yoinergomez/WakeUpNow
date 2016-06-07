package grp02.udea.edu.co.wakeupnow;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;



/**
 * Created by Pc1 on 13/04/2016.
 */
public class AlarmReceiver extends BroadcastReceiver {

    public static final int ID_NOTIFICACION_ALARMA = 22061995;

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Alarm received!", Toast.LENGTH_LONG).show();

        //Lanzar la actividad de la alarma
        intent = intent.setClassName("grp02.udea.edu.co.wakeupnow",
                "grp02.udea.edu.co.wakeupnow.AlarmaQR");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        crearNotificacion(context);
    }

    private void crearNotificacion(Context context) {

        final long[] pattern = {10, 1000, 2000};

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_alarm_white_48dp)
                        .setContentTitle("WakeUpNow: Alarma activada")
                        .setContentText("Toca para apagar la alarma");
        Intent resultIntent = new Intent(context, AlarmaQR.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        mBuilder.setSound(alarmSound);
        //mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setOngoing(true);
        mBuilder.setAutoCancel(false);
        mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        mBuilder.setVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(ID_NOTIFICACION_ALARMA, mBuilder.build());


    }
}