package hq.remview.ui.main.scanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.Locale;

import hq.remview.R;
import hq.remview.constant.Constants;
import hq.remview.data.local.prefs.PreferencesService;

public class CustomScanner extends Activity {
    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    Button gallery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.scanner_fullscreen);

        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);
        gallery = findViewById(R.id.gallery);


        gallery.setFocusable(false);
        gallery.setFocusableInTouchMode(false);
        gallery.setOnClickListener(v -> {
            Intent it = new Intent();
            it.putExtra("gallery",true);
            setResult(RESULT_OK,it);
            finish();
        });
        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences mPrefs= newBase.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        newBase.getResources().getConfiguration().setLocale(new Locale(mPrefs.getString(PreferencesService.LANG,"vi")));
        this.applyOverrideConfiguration(newBase.getResources().getConfiguration());
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
