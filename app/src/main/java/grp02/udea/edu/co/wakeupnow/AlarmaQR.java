package grp02.udea.edu.co.wakeupnow;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import grp02.udea.edu.co.wakeupnow.view.adapter.OrientacionPortraitScanner;

public class AlarmaQR extends AppCompatActivity {

    private IntentIntegrator integrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Ocultar navigation bar
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
        integrator = new IntentIntegrator(this);
        setContentView(R.layout.activity_alarma_qr);
        escanearCodigoQR();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onPause() {
        super.onPause();

        //Prohibe salir de la aplicación desde el botón de aplicaciones recientes
        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(), 0);
    }

    /**
     * Inicia el escaner del codigo QR
     */
    public void escanearCodigoQR() {
        integrator.setCaptureActivity(OrientacionPortraitScanner.class);
        integrator.setPrompt("Escanee el codigo QR para apagar la alarma");
        integrator.setOrientationLocked(true);
        integrator.initiateScan();
    }

    /**
     * Decodifica el código QR para obtener su respectivo texto
     *
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

                        //apagarAlarma();
                        stopService(new Intent(this, ControlAlarmaService.class));

                        String contents = intentResult.getContents();
                        String format = intentResult.getFormatName();

                        Toast.makeText(getApplicationContext(), contents, Toast.LENGTH_LONG).show();
                        Log.d("@ onActivityResult", "CONTENIDO_DEL_QR: " + contents + ", FORMATO: " + format);
                    } else {
                        Log.e("@ onActivityResult", "IntentResult es NULL!");
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.e("@ onActivityResult", "CANCELADO");
                }
        }
    }


}

