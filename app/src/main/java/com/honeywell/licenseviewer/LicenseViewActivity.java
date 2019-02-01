package com.honeywell.licenseviewer;

import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.honeywell.licensing.License;
import com.honeywell.licensing.LicenseCallback;
import com.honeywell.licensing.LicenseListCallback;
import com.honeywell.licensing.LicenseManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hsm.licensedump.R;

public class LicenseViewActivity extends ListActivity {
    public static final String EXTRA_DETAILS = "com.honeywell.licenseviewer.DETAILS";
    public static final String EXTRA_FEATURE = "com.honeywell.licenseviewer.FEATURE";
    private static final String INTENT_ACTION_LICENSE_INSTALLED = "com.honeywell.licenseservice.intent.action.LICENSE_INSTALLED";
    private static final String INTENT_EXTRA_FEATURE = "com.honeywell.licenseservice.intent.extra.FEATURE";
    private static final String TAG = "LicenseViewActivity";
    private LicenseManager mLicenseManager;
    private BroadcastReceiver mLicenseReceiver = new myBroadcastReceiver();
    private Map<String, License> mLicenses = new HashMap();

    /* renamed from: com.honeywell.licenseviewer.LicenseViewActivity$1 */
    class myBroadcastReceiver extends BroadcastReceiver {
        myBroadcastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (LicenseViewActivity.INTENT_ACTION_LICENSE_INSTALLED.equals(intent.getAction())) {
                LicenseViewActivity.this.refreshLicenseList();
            }
        }
    }

    /* renamed from: com.honeywell.licenseviewer.LicenseViewActivity$3 */
    class onClickListener implements OnClickListener {
        onClickListener() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    }

    /* renamed from: com.honeywell.licenseviewer.LicenseViewActivity$4 */
    class myLicenseCallback implements LicenseListCallback {
        myLicenseCallback() {
        }

        public void onLicenseList(int code, List<License> licenses) {
            if (code != 0) {
                Toast.makeText(LicenseViewActivity.this, "Error: " + code, Toast.LENGTH_LONG).show();
                return;
            }
            List<String> features = new ArrayList(licenses.size());
            for (License license : licenses) {
                features.add(license.getFeature());
                LicenseViewActivity.this.mLicenses.put(license.getFeature(), license);
            }
//            LicenseViewActivity.this.setListAdapter(new ArrayAdapter(LicenseViewActivity.this, R.layout.details_activity_action, features));
            LicenseViewActivity.this.setListAdapter(new ArrayAdapter(LicenseViewActivity.this, android.R.layout.simple_list_item_1, features));
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mLicenseManager = new LicenseManager(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(INTENT_ACTION_LICENSE_INSTALLED);
        registerReceiver(this.mLicenseReceiver, filter);
        refreshLicenseList();
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.mLicenseReceiver);
        this.mLicenseManager.close();
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        License license = (License) this.mLicenses.get((String) getListAdapter().getItem(position));
        String[] licenseInfo = license.toArray();
        Intent intent = new Intent(this, LicenseDetailViewActivity.class);
        intent.putExtra(EXTRA_FEATURE, license.getFeature());
        intent.putExtra(EXTRA_DETAILS, licenseInfo);
        startActivity(intent);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnu_request_demo:
                Builder builder = new Builder(this);
                builder.setTitle("Enter feature name");
                final EditText input = new EditText(this);
                input.setInputType(1);
                builder.setView(input);
                builder.setPositiveButton("OK", new OnClickListener() {

                    /* renamed from: com.honeywell.licenseviewer.LicenseViewActivity$2$1 */
                    class myLicenseCallback implements LicenseCallback {
                        myLicenseCallback() {
                        }

                        public void onLicense(int code, String feature, License license) {
                            LicenseViewActivity.this.refreshLicenseList();
                        }
                    }

                    public void onClick(DialogInterface dialog, int which) {
                        if (LicenseViewActivity.this.mLicenseManager != null) {
                            LicenseViewActivity.this.mLicenseManager.obtainLicense(input.getText().toString(), "1.0", true, new myLicenseCallback());
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new onClickListener());
                builder.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refreshLicenseList() {
        this.mLicenseManager.getLicenseList(new myLicenseCallback());
    }
}