package org.asdtm.goodweather;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.asdtm.goodweather.JacocoInstrumentation.TAG;

// adb shell am broadcast -a com.example.pkg.END_EMMA
public class EndEmmaBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String path = context.getFilesDir().getPath().toString() + "/" + "coverage.ec";
        Log.d(TAG, "generateCoverageReport():" + path);
        OutputStream out = null;
        try {
            out = new FileOutputStream(path, false);
            Object agent = Class.forName("org.jacoco.agent.rt.RT")
                    .getMethod("getAgent")
                    .invoke(null);
            out.write((byte[]) agent.getClass().getMethod("getExecutionData", boolean.class)
                    .invoke(agent, true));
        } catch (Exception e) {
            Log.d(TAG, e.toString(), e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // once coverage is dumped, the processes is ended.
//		Process.killProcess(Process.myPid());
    }

}