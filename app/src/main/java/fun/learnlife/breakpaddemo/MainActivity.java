package fun.learnlife.breakpaddemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.sample.breakpad.BreakpadInit;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 100;
    private File externalReportPath;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("crash-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        } else {
            initExternalReportPath();
        }
        initBreakPad();
        // Example of a call to a native method
        TextView tv = findViewById(R.id.text);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crash();
            }
        });
    }

    private void initBreakPad() {
        if (externalReportPath == null) {
            externalReportPath = new File(getFilesDir(), "crashDump");
            if (!externalReportPath.exists()) {
                externalReportPath.mkdirs();
            }
        }
        String path = externalReportPath.getAbsolutePath();
        Log.i("lcyy", path);
        BreakpadInit.initBreakpad(path);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        initExternalReportPath();
    }

    private void initExternalReportPath() {
        externalReportPath = new File(Environment.getExternalStorageDirectory(), "crashDump");
        if (!externalReportPath.exists()) {
            externalReportPath.mkdirs();
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native void crash();
}
