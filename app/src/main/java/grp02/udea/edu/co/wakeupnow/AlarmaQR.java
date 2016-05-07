package grp02.udea.edu.co.wakeupnow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import grp02.udea.edu.co.wakeupnow.view.adapter.OrientacionPortraitScanner;

public class AlarmaQR extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarma_qr);

        activarAlarma();
        escanearCodigoQR();
    }


    /**
     * Inicia el escaner del codigo QR
     */
    public void escanearCodigoQR(){
        IntentIntegrator integrator = new IntentIntegrator((Activity) AlarmaQR.this);
        integrator.setCaptureActivity(OrientacionPortraitScanner.class);
        integrator.setPrompt("Escanee el codigo QR para apagar la alarma");
        integrator.setOrientationLocked(true);
        integrator.initiateScan();
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

                        Toast.makeText(getApplicationContext(), contents, Toast.LENGTH_LONG).show();
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
     * Dispara la alarma activando la vibración y el sonido
     */
    public void activarAlarma(){
        // Vibrate the mobile phone
        Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(2000);
    }
}
