package org.asdtm.goodweather;

/**
 * Created by huangan on 2018/6/16.
 */

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class JacocoInstrumentation  extends Instrumentation{
    public static String TAG = "JacocoInstrumentation:";
    private Intent mIntent;

    public JacocoInstrumentation() {
    }
    @Override
    public void onCreate(Bundle arguments) {
        Log.d(TAG, "onCreate(" + arguments + ")");
        super.onCreate(arguments);
        // bad notation, better use NAME+TimeSeed because you might generate more than 1 corage file
        mIntent = getTargetContext().getPackageManager()
                .getLaunchIntentForPackage("YOUR_TARGET_PACKAGE_NAME")
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        start();
    }
    @Override
    public void onStart() {
        startActivitySync(mIntent);
        LocalBroadcastManager.getInstance(getTargetContext()).registerReceiver(
                new EndEmmaBroadcast(), new IntentFilter("com.example.pkg.END_EMMA"));
    }
    private String getCoverageFilePath() {
        String path = getContext().getFilesDir().getPath().toString() + "/" + "coverage.ec";
        File file = new File(path);
        if(!file.exists()){
            try{
                file.createNewFile();
            }catch (IOException e){
                Log.d(TAG,"File Exception ："+e);
                e.printStackTrace();}
        }
        return path;
    }
    private void generateCoverageReport() {
        Log.d(TAG, "generateCoverageReport():" + getCoverageFilePath());
        OutputStream out = null;
        try {
            out = new FileOutputStream(getCoverageFilePath(), false);
            Object agent = Class.forName("org.jacoco.agent.rt.RT")
                    .getMethod("getAgent")
                    .invoke(null);
//            out.write((byte[]) agent.getClass().getMethod("getExecutionData", boolean.class)
//                    .invoke(agent, false));
//            reset coverage information
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
    }
}
