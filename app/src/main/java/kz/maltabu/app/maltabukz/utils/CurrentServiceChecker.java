package kz.maltabu.app.maltabukz.utils;

import android.app.ActivityManager;
import android.content.Context;

public class CurrentServiceChecker {

    private Context context;

    public CurrentServiceChecker(Context context){
        this.context=context;
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        if(context!=null) {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }
}
