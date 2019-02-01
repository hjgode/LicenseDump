package com.honeywell.licensing;

import java.util.List;

public interface LicenseListCallback {
    void onLicenseList(int i, List<License> list);
}