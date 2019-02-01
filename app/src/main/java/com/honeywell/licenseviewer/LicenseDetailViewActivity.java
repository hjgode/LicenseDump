package com.honeywell.licenseviewer;

import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.honeywell.licensing.License;
import com.honeywell.licensing.LicenseCallback;
import com.honeywell.licensing.LicenseManager;

import hsm.licensedump.R;

public class LicenseDetailViewActivity extends ListActivity {
    private static final String TAG = "LicenseDetailViewActivity";
    private LicenseManager mLicenseManager;
    private String mName = "";
    ListView listView;

    /* renamed from: com.honeywell.licenseviewer.LicenseDetailViewActivity$2 */
    class C00032 implements OnClickListener {
        C00032() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        this.mName = intent.getStringExtra(LicenseViewActivity.EXTRA_FEATURE);

        listView=(ListView)findViewById(R.id.details_activity_main);
        setListAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, intent.getStringArrayExtra(LicenseViewActivity.EXTRA_DETAILS)));

        //        setListAdapter(new ArrayAdapter(this, R.layout.details_activity_action, intent.getStringArrayExtra(LicenseViewActivity.EXTRA_DETAILS)));

        this.mLicenseManager = new LicenseManager(this);
    }

    protected void onDestroy() {
        super.onDestroy();
        this.mLicenseManager.close();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnu_check_licensed:
                if (this.mLicenseManager == null) {
                    return true;
                }
                Builder builder = new Builder(this);
                builder.setTitle("Enter version string");
                final EditText input = new EditText(this);
                input.setInputType(1);
                builder.setView(input);
                builder.setPositiveButton("OK", new OnClickListener() {

                    /* renamed from: com.honeywell.licenseviewer.LicenseDetailViewActivity$1$1 */
                    class C00021 implements LicenseCallback {
                        C00021() {
                        }

                        public void onLicense(int code, String feature, License license) {
                            Toast.makeText(LicenseDetailViewActivity.this, "Licensed: " + (code == 0 ? "yes" : "no"), 0).show();
                        }
                    }

                    public void onClick(DialogInterface dialog, int which) {
                        if (LicenseDetailViewActivity.this.mLicenseManager != null) {
                            if (LicenseDetailViewActivity.this.mName.endsWith(".demo")) {
                                LicenseDetailViewActivity.this.mName = LicenseDetailViewActivity.this.mName.substring(0, LicenseDetailViewActivity.this.mName.lastIndexOf(46));
                            }
                            LicenseDetailViewActivity.this.mLicenseManager.obtainLicense(LicenseDetailViewActivity.this.mName, input.getText().toString(), false, new C00021());
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new C00032());
                builder.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}