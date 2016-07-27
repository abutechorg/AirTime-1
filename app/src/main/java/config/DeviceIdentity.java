package config;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * Created by Owner on 7/9/2016.
 */
public class DeviceIdentity {
    private Context context;
    //default imei
    private  String imei="369";
    //default serialNumber
    private String serialNumber="456";
    //versionRelease
    private String versionRelease="1";
    //version
    private int version=1;

    public DeviceIdentity(Context context) {
        this.context = context;
    }

    public String getImei(){
        TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        imei=telMgr.getDeviceId();
        return imei;
    }

    public String getSerialNumber(){
        serialNumber= Build.SERIAL != Build.UNKNOWN ? Build.SERIAL : Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if(!TextUtils.isEmpty(serialNumber))
            return serialNumber;
        else{
            DeviceUuidFactory duf=new DeviceUuidFactory(context);
            try{
                serialNumber=String.valueOf(duf.getDeviceUuid());
                return serialNumber;
            }catch (Exception e){
                e.printStackTrace();
                TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                return telMgr.getDeviceId();
            }
        }
    }

    public String getVersion(){
        version = Build.VERSION.SDK_INT;
        versionRelease = Build.VERSION.RELEASE;
        return versionRelease+"("+version+")";
    }
}
