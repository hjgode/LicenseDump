package com.honeywell.licensing;

public interface LicenseCallback {
    void onLicense(int i, String str, License license);
}