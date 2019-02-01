package com.honeywell.licenseservice;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;

public class LicenseParcel implements Parcelable {
    public static final Creator<LicenseParcel> CREATOR = new C00001();
    private String mActivation = "";
    private String mActivationCode = "";
    private String mBundle = "";
    private byte mByOptions = (byte) 0;
    private byte mByVersion = (byte) 1;
    private String mCustomerInfo = "";
    private String mDefault = "";
    private String mExpiration = "";
    private String mInfo = "";
    private String mInstance = "";
    private Bundle mKeys = new Bundle();
    private String mNotice = "";
    private String mQuantity = "";
    private String mServer = "";
    private String mVersion = "";

    /* renamed from: com.honeywell.licenseservice.LicenseParcel$1 */
    static class C00001 implements Creator<LicenseParcel> {
        C00001() {
        }

        public LicenseParcel createFromParcel(Parcel source) {
            return new LicenseParcel(source);
        }

        public LicenseParcel[] newArray(int size) {
            return new LicenseParcel[size];
        }
    }

    public LicenseParcel(Parcel source) {
        setByVersion(source.readByte());
        setByOptions(source.readByte());
        setInstance(source.readString());
        setVersion(source.readString());
        setExpiration(source.readString());
        setQuantity(source.readString());
        setServer(source.readString());
        setNotice(source.readString());
        setKeys(source.readBundle());
        setInfo(source.readString());
        setCustomerInfo(source.readString());
        setDefault(source.readString());
        setBundle(source.readString());
        setActivation(source.readString());
        setActivationCode(source.readString());
    }

    public byte getByVersion() {
        return this.mByVersion;
    }

    public void setByVersion(byte byVersion) {
        this.mByVersion = byVersion;
    }

    public byte getByOptions() {
        return this.mByOptions;
    }

    public void setByOptions(byte byOptions) {
        this.mByOptions = byOptions;
    }

    public String getInstance() {
        return this.mInstance;
    }

    public void setInstance(String instance) {
        this.mInstance = instance;
    }

    public String getVersion() {
        return this.mVersion;
    }

    public void setVersion(String version) {
        this.mVersion = version;
    }

    public String getExpiration() {
        return this.mExpiration;
    }

    public void setExpiration(String expiration) {
        this.mExpiration = expiration;
    }

    public String getQuantity() {
        return this.mQuantity;
    }

    public void setQuantity(String quantity) {
        this.mQuantity = quantity;
    }

    public String getServer() {
        return this.mServer;
    }

    public void setServer(String server) {
        this.mServer = server;
    }

    public String getNotice() {
        return this.mNotice;
    }

    public void setNotice(String notice) {
        this.mNotice = notice;
    }

    public Bundle getKeys() {
        return this.mKeys;
    }

    public void setKey(String host, String hash) {
        Log.d("LicenseParcel", "setKey k=" + host + " v=" + hash);
        this.mKeys.putString(host, hash);
    }

    public void setKeys(Bundle keyBundle) {
        this.mKeys = keyBundle;
    }

    public String getInfo() {
        return this.mInfo;
    }

    public void setInfo(String info) {
        this.mInfo = info;
    }

    public String getCustomerInfo() {
        return this.mCustomerInfo;
    }

    public void setCustomerInfo(String customerInfo) {
        this.mCustomerInfo = customerInfo;
    }

    public String getDefault() {
        return this.mDefault;
    }

    public void setDefault(String _default) {
        this.mDefault = _default;
    }

    public String getBundle() {
        return this.mBundle;
    }

    public void setBundle(String bundle) {
        this.mBundle = bundle;
    }

    public String getActivation() {
        return this.mActivation;
    }

    public void setActivation(String activation) {
        this.mActivation = activation;
    }

    public String getActivationCode() {
        return this.mActivationCode;
    }

    public void setActivationCode(String activationCode) {
        this.mActivationCode = activationCode;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.mByVersion);
        dest.writeByte(this.mByOptions);
        dest.writeString(this.mInstance);
        dest.writeString(this.mVersion);
        dest.writeString(this.mExpiration);
        dest.writeString(this.mQuantity);
        dest.writeString(this.mServer);
        dest.writeString(this.mNotice);
        dest.writeBundle(this.mKeys);
        dest.writeString(this.mInfo);
        dest.writeString(this.mCustomerInfo);
        dest.writeString(this.mDefault);
        dest.writeString(this.mBundle);
        dest.writeString(this.mActivation);
        dest.writeString(this.mActivationCode);
    }
}