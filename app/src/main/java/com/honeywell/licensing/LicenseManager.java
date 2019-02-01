package com.honeywell.licensing;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.ConditionVariable;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import com.honeywell.licenseservice.ILicenseListListener;
import com.honeywell.licenseservice.ILicenseListener;
import com.honeywell.licenseservice.ILicenseManager;
import com.honeywell.licenseservice.ILicenseManager.Stub;
import com.honeywell.licenseservice.LicenseParcel;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class LicenseManager {
    public static final int ERROR_LICENSE_EXPIRED = -2;
    public static final int ERROR_LICENSE_MANAGER_UNAVAILABLE = -101;
    public static final int ERROR_LICENSE_NOT_FOUND = -1;
    public static final int ERROR_NO_SERVICE = -100;
    public static final int ERROR_OLD_LICENSE_VERSION = -3;
    public static final int ERROR_UNKNOWN = -1000;
    private static final int REQUEST_GET_LICENSES_ID = 2;
    private static final int REQUEST_OBTAIN_LICENSE_ID = 1;
    public static final int SUCCESS = 0;
    private static final String TAG = "LicenseService";
    private boolean mBound;
    private Handler mCallbackHandler;
    private Context mContext;
    private ILicenseManager mLicenseManager;
    private Handler mRequestHandler;
    private boolean mServiceConnected;
    private ServiceConnection mServiceConnection = new C00111();
    private ConditionVariable mWaitForService;

    /* renamed from: com.honeywell.licensing.LicenseManager$1 */
    class C00111 implements ServiceConnection {
        C00111() {
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(LicenseManager.TAG, "++onServiceConnected 1 ");
            LicenseManager.this.mLicenseManager = Stub.asInterface(service);
            LicenseManager.this.mServiceConnected = true;
            LicenseManager.this.mWaitForService.open();
            Log.e(LicenseManager.TAG, "--onServiceConnected ");
        }

        public void onServiceDisconnected(ComponentName name) {
            LicenseManager.this.mServiceConnected = false;
        }
    }

    private class GetLicensesRequest {
        public LicenseListCallback callback;

        private GetLicensesRequest() {
        }

        /* synthetic */ GetLicensesRequest(LicenseManager licenseManager, GetLicensesRequest getLicensesRequest) {
            this();
        }
    }

    private class ObtainLicenseRequest {
        public LicenseCallback callback;
        public String feature;
        public boolean requestDemo;
        public String version;

        private ObtainLicenseRequest() {
        }

        /* synthetic */ ObtainLicenseRequest(LicenseManager licenseManager, ObtainLicenseRequest obtainLicenseRequest) {
            this();
        }
    }

    public LicenseManager(Context context) {
        Log.d(TAG, "++constructor ctx");
        this.mContext = context;
        initialize();
        Intent intent = new Intent();
        intent.setClassName("com.honeywell.licenseservice", "com.honeywell.licenseservice.LicenseService");
        this.mBound = this.mContext.bindService(intent, this.mServiceConnection, REQUEST_OBTAIN_LICENSE_ID);
        Log.d(TAG, "bindService returned " + this.mBound);
        Log.d(TAG, "--constructor ctx ");
    }

    private void initialize() {
        Log.d(TAG, "++initialize");
        if (this.mWaitForService == null) {
            this.mWaitForService = new ConditionVariable();
        }
        this.mServiceConnected = false;
        HandlerThread requestThread = new HandlerThread("background thread");
        requestThread.start();
        this.mRequestHandler = new Handler(requestThread.getLooper()) {
            public void handleMessage(Message msg) {
                LicenseManager.this.mWaitForService.block();
                if (!LicenseManager.this.mServiceConnected) {
                    LicenseManager.this.mLicenseManager = null;
                }
                if (msg.what == LicenseManager.REQUEST_OBTAIN_LICENSE_ID) {
                    Log.d(LicenseManager.TAG, "msg.what = REQUEST_OBTAIN_LICENSE_ID");

                    LicenseManager.this.obtainLicense((ObtainLicenseRequest) msg.obj);
                } else if (msg.what == LicenseManager.REQUEST_GET_LICENSES_ID) {
                    LicenseManager.this.getLicenses((GetLicensesRequest) msg.obj);
                }
            }
        };
        this.mCallbackHandler = new Handler(this.mContext.getMainLooper());
        Log.d(TAG, "--initialize");
    }

    public LicenseManager(Context context, BroadcastReceiver receiver) {
        Log.e(TAG, "++constructor - ctx, receiver ");
        if (context == null) {
            Log.e(TAG, "constructor - ctx is null! ");
        } else if (receiver == null) {
            Log.e(TAG, "constructor - receiver is null! ");
        } else {
            this.mContext = context;
            Intent intent = new Intent();
            intent.setClassName("com.honeywell.licenseservice", "com.honeywell.licenseservice.LicenseService");
            this.mLicenseManager = Stub.asInterface(receiver.peekService(context, intent));
            if (this.mLicenseManager != null) {
                Log.e(TAG, "++LicenseManager constructor - mLicenseManager created");
                this.mServiceConnected = true;
                if (this.mWaitForService == null) {
                    this.mWaitForService = new ConditionVariable();
                }
                this.mWaitForService.open();
                this.mBound = true;
            } else {
                Log.e(TAG, "++LicenseManager constructor - no mLicenseManager created");
            }
            initialize();
            Log.e(TAG, "--constructor - ctx, receiver ");
        }
    }

    public synchronized void obtainLicense(String feature, String version, boolean requestDemo, LicenseCallback callback) {
        Log.e(TAG, "++obtainLicense " + feature + ", " + version);
        if (feature == null || version == null || callback == null) {
            throw new InvalidParameterException();
        }
        ObtainLicenseRequest request = new ObtainLicenseRequest(this, null);
        request.feature = feature;
        request.version = version;
        request.requestDemo = requestDemo;
        request.callback = callback;
        if (this.mBound) {
            Log.e(TAG, "obtainLicense for " + feature + ".  send message to target");
            Message.obtain(this.mRequestHandler, REQUEST_OBTAIN_LICENSE_ID, request).sendToTarget();
        } else {
            Log.e(TAG, "obtainLicense for " + feature + ".  error not bound!");
            handleObtainLicenseCallback(callback, feature, -100, null);
        }
        Log.e(TAG, "--obtainLicense - feature, version ");
    }

    private void obtainLicense(final ObtainLicenseRequest request) {
        Log.e(TAG, "++obtainLicense - request ");
        if (this.mLicenseManager == null) {
            Log.e(TAG, "obtainLicense - mLicenseManager is null");
            handleObtainLicenseCallback(request.callback, request.feature, -100, null);
            return;
        }
        try {
            this.mLicenseManager.obtainLicense(request.feature, request.version, request.requestDemo, new ILicenseListener.Stub() {
                public void onLicense(int code, LicenseParcel parcel) throws RemoteException {
                    License license;
                    if (parcel == null) {
                        license = null;
                    } else {
                        license = new License(parcel);
                    }
                    LicenseManager.this.handleObtainLicenseCallback(request.callback, request.feature, code, license);
                }
            });
        } catch (DeadObjectException e) {
            Log.e(TAG, "obtainLicense - DeadObject");
            handleObtainLicenseCallback(request.callback, request.feature, -100, null);
        } catch (RemoteException e2) {
            handleObtainLicenseCallback(request.callback, request.feature, ERROR_UNKNOWN, null);
        }
        Log.e(TAG, "--obtainLicense - request ");
    }

    private void handleObtainLicenseCallback(LicenseCallback callback, String feature, int code, License license) {
        if (callback != null) {
            final LicenseCallback licenseCallback = callback;
            final int i = code;
            final String str = feature;
            final License license2 = license;
            this.mCallbackHandler.post(new Runnable() {
                public void run() {
                    Log.e(LicenseManager.TAG, "handleObtainLicenseCallback calling callback method");
                    licenseCallback.onLicense(i, str, license2);
                }
            });
        }
    }

    public synchronized void getLicenseList(LicenseListCallback callback) {
        if (callback == null) {
            throw new InvalidParameterException();
        }
        GetLicensesRequest request = new GetLicensesRequest(this, null);
        request.callback = callback;
        if (this.mBound) {
            Message.obtain(this.mRequestHandler, REQUEST_GET_LICENSES_ID, request).sendToTarget();
        } else {
            handleGetLicensesCallback(callback, -100, null);
        }
    }

    private void getLicenses(final GetLicensesRequest request) {
        if (this.mLicenseManager == null) {
            handleGetLicensesCallback(request.callback, -100, null);
            return;
        }
        try {
            this.mLicenseManager.getLicenseList(new ILicenseListListener.Stub() {
                public void onLicenseList(int code, List<LicenseParcel> parcels) throws RemoteException {
                    List<License> licenses = null;
                    if (code == 0) {
                        licenses = new ArrayList();
                        if (parcels != null) {
                            for (LicenseParcel parcel : parcels) {
                                licenses.add(new License(parcel));
                            }
                        }
                    }
                    LicenseManager.this.handleGetLicensesCallback(request.callback, code, licenses);
                }
            });
        } catch (DeadObjectException e) {
            handleGetLicensesCallback(request.callback, -100, null);
        } catch (RemoteException e2) {
            handleGetLicensesCallback(request.callback, ERROR_UNKNOWN, null);
        }
    }

    private void handleGetLicensesCallback(final LicenseListCallback callback, final int code, final List<License> licenses) {
        if (callback != null) {
            this.mCallbackHandler.post(new Runnable() {
                public void run() {
                    callback.onLicenseList(code, licenses);
                }
            });
        }
    }

    public synchronized void close() {
        if (this.mBound) {
            try {
                this.mContext.unbindService(this.mServiceConnection);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Unable to unbind from licensing service (already unbound)");
            }
            this.mServiceConnected = false;
            this.mWaitForService.open();
        }
        this.mRequestHandler.getLooper().quit();
        return;
    }
}