package utilities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by Hp on 10/24/2016.
 */
public class IsGranted {
    Context context;

    public IsGranted(Context context) {
        this.context = context;
    }

    public boolean checkReadContacts(){
        int res = context.checkCallingOrSelfPermission(Manifest.permission.READ_CONTACTS);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
}
