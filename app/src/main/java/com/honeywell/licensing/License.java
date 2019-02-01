package com.honeywell.licensing;

import android.os.Bundle;
import com.honeywell.licenseservice.LicenseParcel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class License {
    private static final SimpleDateFormat DATE_FORMAT_LONG = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    private static final SimpleDateFormat DATE_FORMAT_SHORT = new SimpleDateFormat("MM/dd/yyyy");
    private String mActivation = "";
    private String mActivationCode = "";
    private String mBundle = "";
    private String mCustomerInfo = "";
    private String mDefault = "";
    private String mExpiration = "";
    private String mInfo = "";
    private Map<String, String> mInfoMap = new HashMap();
    private String mInstance = "";
    private Bundle mKeys = new Bundle();
    private String mNotice = "";
    private String mQuantity = "";
    private String mServer = "";
    private List<SimpleDateFormat> mSupportedDateFormats = new ArrayList();
    private String mVersion = "";

    License(LicenseParcel parcel) {
        this.mInstance = parcel.getInstance();
        this.mVersion = parcel.getVersion();
        this.mExpiration = parcel.getExpiration();
        this.mQuantity = parcel.getQuantity();
        this.mServer = parcel.getServer();
        this.mNotice = parcel.getNotice();
        this.mKeys = parcel.getKeys();
        this.mInfo = parcel.getInfo();
        this.mCustomerInfo = parcel.getCustomerInfo();
        this.mDefault = parcel.getDefault();
        this.mBundle = parcel.getBundle();
        this.mActivation = parcel.getActivation();
        this.mActivationCode = parcel.getActivationCode();
        this.mInfoMap = stringInfoToMap(this.mInfo);
        this.mSupportedDateFormats.add(DATE_FORMAT_SHORT);
        this.mSupportedDateFormats.add(DATE_FORMAT_LONG);
    }

    private Date stringToDate(String date) {
        Date parsedDate = null;
        if (this.mSupportedDateFormats.isEmpty()) {
            throw new RuntimeException("No date formats supported");
        }
        for (SimpleDateFormat format : this.mSupportedDateFormats) {
            try {
                parsedDate = format.parse(date);
                break;
            } catch (ParseException e) {
            }
        }
        return parsedDate;
    }

    private Map<String, String> stringInfoToMap(String info) {
        Map<String, String> map = new HashMap();
        if (!info.isEmpty()) {
            for (String infoBlob : info.split(";")) {
                String[] partArray = infoBlob.split("=");
                if (partArray.length == 2) {
                    map.put(partArray[0], partArray[1]);
                }
            }
        }
        return map;
    }

    public String getFeature() {
        return this.mInstance;
    }

    public String getVersion() {
        return this.mVersion;
    }

    public Date getExpirationDate() {
        return stringToDate(this.mExpiration);
    }

    public String getExpirationString() {
        return this.mExpiration;
    }

    public String getQuantityString() {
        return this.mQuantity;
    }

    public int getQuantity() {
        int quantity = 1;
        try {
            return Integer.parseInt(this.mQuantity);
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            return quantity;
        }
    }

    public String getServerId() {
        return this.mServer;
    }

    public String getNotice() {
        return this.mNotice;
    }

    public Map<String, String> getInfo() {
        return Collections.unmodifiableMap(this.mInfoMap);
    }

    public String getInfoString() {
        return this.mInfo;
    }

    public String getCustomer() {
        String customer = "";
        if (this.mInfoMap.containsKey("customer")) {
            return (String) this.mInfoMap.get("customer");
        }
        return customer;
    }

    public String getCustomerInfo() {
        return this.mCustomerInfo;
    }

    public String getDefault() {
        return this.mDefault;
    }

    public String getBundle() {
        return this.mBundle;
    }

    public Date getActivationDate() {
        return stringToDate(this.mActivation);
    }

    public String getActivationString() {
        return this.mActivation;
    }

    public String getActivationCode() {
        return this.mActivationCode;
    }

    public String getStatus() {
        return "okay";
    }

    public String getType() {
        if (this.mInfoMap.containsKey("type")) {
            return ((String) this.mInfoMap.get("type")).toUpperCase();
        }
        return "NONE";
    }

    public String getModel() {
        StringBuilder model = new StringBuilder();
        if (!this.mQuantity.equals("0")) {
            model.append("COUNTED");
        }
        if (hasExpiration()) {
            model.append(", EXPIRING");
        }
        return model.toString();
    }

    public boolean hasExpiration() {
        return (this.mExpiration.isEmpty() || this.mExpiration.equalsIgnoreCase("No Expiration")) ? false : true;
    }

    public int getDaysUntilExpiration() {
        if (!hasExpiration()) {
            return 0;
        }
        int daysToExpiration = (int) ((stringToDate(this.mExpiration).getTime() - new Date().getTime()) / 86400000);
        if (daysToExpiration <= 0) {
            return -1;
        }
        return daysToExpiration;
    }

    public boolean isExpired() {
        return getDaysUntilExpiration() < 0;
    }

    public boolean isDemo() {
        return "DEMO".equalsIgnoreCase(getType());
    }

    public String[] toArray() {
        List<String> list = new ArrayList();
        list.add("Type: " + getType());
        list.add("Model: " + getModel());
        list.add("Status: " + getStatus());
        list.add("Days to expiration: " + getDaysUntilExpiration());
        list.add("Feature: " + getFeature());
        list.add("Version: " + getVersion());
        list.add("Expiration: " + getExpirationString());
        list.add("Quantity: " + getQuantityString());
        list.add("ServerId: " + getServerId());
        list.add("Notice: " + getNotice());
        list.add("Info: " + getInfoString());
        list.add("Default: " + getDefault());
        list.add("Bundle: " + getBundle());
        list.add("Activation: " + getActivationString());
        list.add("Activaton Code: " + getActivationCode());
        return (String[]) list.toArray(new String[list.size()]);
    }
}