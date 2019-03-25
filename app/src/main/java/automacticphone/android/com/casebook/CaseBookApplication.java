package automacticphone.android.com.casebook;

import android.app.Application;
import android.os.Build;

import automacticphone.android.com.casebook.activity.push.MyFirebaseMessagingService;

public class CaseBookApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            MyFirebaseMessagingService.createChannel( this );
        }
    }
}
