package com.honeywell.licenseservice;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface ILicenseManager extends IInterface {

    public static abstract class Stub extends Binder implements ILicenseManager {
        private static final String DESCRIPTOR = "com.honeywell.licenseservice.ILicenseManager";
        static final int TRANSACTION_getLicenseList = 2;
        static final int TRANSACTION_obtainLicense = 1;

        private static class Proxy implements ILicenseManager {
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

            public void obtainLicense(String feature, String version, boolean requestDemo, ILicenseListener listener) throws RemoteException {
                int i = Stub.TRANSACTION_obtainLicense;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(feature);
                    _data.writeString(version);
                    if (!requestDemo) {
                        i = 0;
                    }
                    _data.writeInt(i);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    this.mRemote.transact(Stub.TRANSACTION_obtainLicense, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void getLicenseList(ILicenseListListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    this.mRemote.transact(Stub.TRANSACTION_getLicenseList, _data, _reply, 0);
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

        public static ILicenseManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof ILicenseManager)) {
                return new Proxy(obj);
            }
            return (ILicenseManager) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case TRANSACTION_obtainLicense /*1*/:
                    data.enforceInterface(DESCRIPTOR);
                    obtainLicense(data.readString(), data.readString(), data.readInt() != 0, com.honeywell.licenseservice.ILicenseListener.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getLicenseList /*2*/:
                    data.enforceInterface(DESCRIPTOR);
                    getLicenseList(com.honeywell.licenseservice.ILicenseListListener.Stub.asInterface(data.readStrongBinder()));
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

    void getLicenseList(ILicenseListListener iLicenseListListener) throws RemoteException;

    void obtainLicense(String str, String str2, boolean z, ILicenseListener iLicenseListener) throws RemoteException;
}