package grp02.udea.edu.co.wakeupnow;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Random;

import grp02.udea.edu.co.wakeupnow.view.adapter.OrientacionPortraitScanner;

public class AlarmaQR extends AppCompatActivity {

    private IntentIntegrator integrator;
    private Button button_verificar;
    private int indexRandom;
    private TextView fraseOriginal;
    private EditText fraseReplica;
    private final String[] frases = {
            "Por mucho madrugar, aparecen las ojeras",
            "Joven madrugador, viejo trasnochador",
            "A quien madruga, Dios le ayuda"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON );

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

        Random rand = new Random();
        indexRandom = rand.nextInt((frases.length));
        fraseReplica = (EditText) findViewById(R.id.editText);
        fraseOriginal = (TextView) findViewById(R.id.txv_frase);
        fraseOriginal.setText(frases[indexRandom]);

    }

    public void actionListenerButtonVerificar(View view) {
        if(fraseOriginal.getText().toString().equals(fraseReplica.getText().toString())){
            //apagarAlarma();
            stopService(new Intent(this, ControlAlarmaService.class));

            //iniciar actividad principal
            finish();
            Intent intentMain = new Intent(this, MainActivity.class);
            startActivity(intentMain);
        }else{
            Snackbar.make(view, "Escriba correctamente la frase", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        Log.d("@",fraseOriginal.getText().toString());
        Log.d("@",fraseOriginal.getText().toString());
    }

    @Override
    public void onBackPressed() {
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

                        //iniciar actividad principal
                        finish();
                        Intent intentMain = new Intent(this, MainActivity.class);
                        startActivity(intentMain);
                    } else {
                        Log.e("@ onActivityResult", "IntentResult es NULL!");
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.e("@ onActivityResult", "CANCELADO");
                }
        }
    }


}

