package thinkreed.jcodec;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        obtainPermission();
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
    }

    private void obtainPermission() {
        if (checkPermission()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                            .READ_PHONE_STATE,
                            Manifest.permission.READ_LOGS, Manifest.permission
                            .ACCESS_NETWORK_STATE, Manifest
                            .permission.INTERNET, Manifest.permission.ACCESS_WIFI_STATE, Manifest
                            .permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE},
                    0);
        }
    }

    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_LOGS)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
