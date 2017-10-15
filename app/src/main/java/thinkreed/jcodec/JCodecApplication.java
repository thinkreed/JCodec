package thinkreed.jcodec;


import com.tencent.bugly.crashreport.CrashReport;

import android.app.Application;

/**
 * Created by thinkreed on 2017/10/13.
 */

public class JCodecApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(this);
    }
}
