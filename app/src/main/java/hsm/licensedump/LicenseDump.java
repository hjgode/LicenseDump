package hsm.licensedump;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.honeywell.licensing.License;
import com.honeywell.licensing.LicenseListCallback;
import com.honeywell.licensing.LicenseManager;
import com.intentfilter.androidpermissions.PermissionManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singleton;

public class LicenseDump extends AppCompatActivity {
    public static final String EXTRA_DETAILS = "com.honeywell.licenseviewer.DETAILS";
    public static final String EXTRA_FEATURE = "com.honeywell.licenseviewer.FEATURE";
    private static final String INTENT_ACTION_LICENSE_INSTALLED = "com.honeywell.licenseservice.intent.action.LICENSE_INSTALLED";
    private static final String INTENT_EXTRA_FEATURE = "com.honeywell.licenseservice.intent.extra.FEATURE";
    private LicenseManager mLicenseManager;
    private BroadcastReceiver mLicenseReceiver = new myBroadcastReceiver();
    private Map<String, License> mLicenses = new HashMap();

    Context context=this;
    final static String TAG="LicenseDump";

    TextView textDump;
    TextView textFile;
    Button btnRefersh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license_dump);

        textFile =(TextView)findViewById(R.id.txtFile);

        textDump =(TextView)findViewById(R.id.txtDump);
        textDump.setMovementMethod(new ScrollingMovementMethod());

        checkPermissions(context);

        this.mLicenseManager = new LicenseManager(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(INTENT_ACTION_LICENSE_INSTALLED);
        registerReceiver(this.mLicenseReceiver, filter);

        refreshLicenseList();

        btnRefersh=(Button)findViewById(R.id.btnRefresh);
        btnRefersh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshLicenseList();
            }
        });
    }

    class myBroadcastReceiver extends BroadcastReceiver {
        myBroadcastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (INTENT_ACTION_LICENSE_INSTALLED.equals(intent.getAction())) {
                refreshLicenseList();
            }
        }
    }

    private void refreshLicenseList() {
        this.mLicenseManager.getLicenseList(new myLicenseCallback());
    }

    class myLicenseCallback implements LicenseListCallback {
        myLicenseCallback() {
        }

        public void onLicenseList(int code, List<License> licenses) {
            if (code != 0) {
                Toast.makeText(context, "Error: " + code, Toast.LENGTH_LONG).show();
                return;
            }
            List<String> features = new ArrayList(licenses.size());
            StringBuilder sb=new StringBuilder();

            Date date=new Date();
            sb.append(date.toGMTString()+"\n");

            String sModelCode=hsm.util.SystemPropertyAccess.getModelCode();
            String sSerial=hsm.util.SystemPropertyAccess.getSerialNumber();
            sb.append("Model: " + sModelCode+"\n");
            sb.append("Serial: " + sSerial+"\n");
            sb.append("============================\n");

            for (License license : licenses) {
                features.add(license.getFeature());
                mLicenses.put(license.getFeature(), license);
                String[] array=license.toArray();
                Log.d(TAG,"\n=======================\n");
                for (String s:array
                     ) {
                    Log.d(TAG, s);
                    sb.append(s+"\n");
                }
                sb.append("============================\n");
                Log.d(TAG,"\n=======================\n");
            }
            textDump.setText(sb.toString());

            saveFile(sModelCode+"_"+sSerial+".txt", sb.toString());

//            LicenseViewActivity.this.setListAdapter(new ArrayAdapter(LicenseViewActivity.this, R.layout.details_activity_action, features));
            //setListAdapter(new ArrayAdapter(LicenseViewActivity.this, android.R.layout.simple_list_item_1, features));
        }
    }

    void saveFile(String sFilename, String s){
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Toast.makeText(context, "External storage not writeable",Toast.LENGTH_LONG);
        }
        String filename = sFilename;
        File filepath = getDocumentsStorageDir();
        File myExternalFile;
        try {
            myExternalFile = new File(filepath, filename);
            FileOutputStream fos = new FileOutputStream(myExternalFile);
            fos.write(s.getBytes());
            fos.close();
            updateMTP(myExternalFile,context,TAG);
            Toast.makeText(context, "File saved to "+myExternalFile.toString(),Toast.LENGTH_LONG);
            textFile.setText("File saved to "+myExternalFile.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    void saveFile(String s){
        String filename = "LicenseDump.txt";
        saveFile(filename, s);
    }

    public static File getDocumentsStorageDir() {
        //Starting with Android 6 you need to set the permissions in Settings-Apps-TotalFreedom-Permissions
        // Get the directory for the user's public pictures directory.
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        if (!file.mkdirs()) {
            Log.d(TAG, "Directory not created");
        }
        Log.d(TAG, "getDocumentsStorageDir() return with " + file.toString());
        return file;
    }

    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }
    public static void updateMTP(File _f, Context _context, String TAG){
        Log.d(TAG, "sending Boradcast about file change to MTP...");
        //make the file visible for PC USB attached MTP
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(_f));
        _context.sendBroadcast(intent);

    }

    static boolean bPermissionOK=false;
    static void checkPermissions(final Context context){
        PermissionManager permissionManager = PermissionManager.getInstance(context);
        permissionManager.checkPermissions(singleton(Manifest.permission.WRITE_EXTERNAL_STORAGE), new PermissionManager.PermissionRequestListener() {
            @Override
            public void onPermissionGranted() {
                //Toast.makeText(context, "Permissions Granted", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Read external storage granted");
                bPermissionOK=true;
            }

            @Override
            public void onPermissionDenied() {
                Toast.makeText(context, "DataEditJS: External Storage Permissions Denied!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
