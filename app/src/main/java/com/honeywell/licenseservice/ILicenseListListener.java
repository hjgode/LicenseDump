package com.honeywell.licenseservice;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;

public interface ILicenseListListener extends IInterface {

    public static abstract class Stub extends Binder implements ILicenseListListener {
        private static final String DESCRIPTOR = "com.honeywell.licenseservice.ILicenseListListener";
        static final int TRANSACTION_onLicenseList = 1;

        private static class Proxy implements ILicenseListListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public void onLicenseList(int code, List<LicenseParcel> parcels) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(code);
                    _data.writeTypedList(parcels);
                    this.mRemote.transact(Stub.TRANSACTION_onLicenseList, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ILicenseListListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof ILicenseListListener)) {
                return new Proxy(obj);
            }
            return (ILicenseListListener) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case TRANSACTION_onLicenseList /*1*/:
                    data.enforceInterface(DESCRIPTOR);
                    onLicenseList(data.readInt(), data.createTypedArrayList(LicenseParcel.CREATOR));
                    reply.writeNoException();
                    return true;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    void onLicenseList(int i, List<LicenseParcel> list) throws RemoteException;
}