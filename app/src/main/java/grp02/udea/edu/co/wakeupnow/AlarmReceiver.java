package grp02.udea.edu.co.wakeupnow;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;

import grp02.udea.edu.co.wakeupnow.view.adapter.OrientacionPortraitScanner;


/**
 * Created by Pc1 on 13/04/2016.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Alarm received!", Toast.LENGTH_LONG).show();

        /*
        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TRAININGCOUNTDOWN");
        wl.acquire();
        */

        //Lanzar la actividad de la alarma
        intent = intent.setClassName("grp02.udea.edu.co.wakeupnow",
                "grp02.udea.edu.co.wakeupnow.AlarmaQR");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}