The purpose of instrumentation is to calculate  the coverage information during the testing process. Here, we use JaCoCo to instrument  the open source app. The main steps to use JaCoCo are as follows:

#### Modify 'AndroidManifest.xml' 

```xml
<manifest>
    <application>
		# add 'receiver'
         <receiver android:name=".EndEmmaBroadcast" >
              <intent-filter>
                  <action android:name="com.example.pkg.END_EMMA" />
              </intent-filter>
          </receiver>

    </application>
		
  // adding following information outside the application tag
  // NOTE : 'targetPackage' is the app's package name.
  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <instrumentation
        android:name=".JacocoInstrumentation"
        android:targetPackage="com.ichi2.anki" >
    </instrumentation>
</manifest>

```

### Add some related files

Place  **'EndEmmaBroadcast.java'** and **'JacocoInstrumentation.java'** under 'src/main/java/com.example/' dir 

**'JacocoInstrumentation.java'** (set getLaunchIntentForPackage‘s variable to current project's package name)

```java
import android.app.Instrumentation;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class JacocoInstrumentation  extends Instrumentation {
    public static String TAG = "JacocoInstrumentation:";
    private Intent mIntent;

    public JacocoInstrumentation() {
    }
    @Override
    public void onCreate(Bundle arguments) {
        Log.d(TAG, "onCreate(" + arguments + ")");
        super.onCreate(arguments);
      	// NOTE:change getLaunchIntentForPackage(current project's package name)
        mIntent = getTargetContext().getPackageManager()
                .getLaunchIntentForPackage("com.ichi2.anki")
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

```

### Modify 'build.gradle'

Adding following infomation to the **build.gradle of project**, 

Note: the task **jacocoTestReportMerge1** uses the relevant file paths, which are related to the specific app. 

```java
apply plugin: 'jacoco'

jacoco {
    toolVersion = "0.7.6.201602180812"
}
// get coverage information
def coverageSourceDirs = [
  		// NOTE: the file path you may need to modify
        './app/src/main/java'
]

task jacocoTestReportMerge1(type: JacocoReport) {
    group = "Reporting"
    description = "Generate Jacoco coverage reports after running tests."
    reports {
        xml.enabled = true
        html.enabled = true
    }
    classDirectories = fileTree(
      		// NOTE: the file path you may need to modify
            dir: './build/intermediates/classes',
            excludes: ['**/R*.class',
                       '**/*$InjectAdapter.class',
                       '**/*$ModuleAdapter.class',
                       '**/*$ViewInjector*.class'
            ])
    sourceDirectories = files(coverageSourceDirs)
    // NOTE: the file path you may need to modify
    // NOTE: Put your ec file here
    executionData = fileTree(dir: '/home/tim/PyCharmProject/Q-testing/benchmark/Anki_jacoco-output-1/coverage_merge_1', include: '**/*.ec')

    doFirst {
      	// NOTE: the file path you may need to modify
        new File("$buildDir/intermediates/classes/").eachFileRecurse { file ->
            if (file.name.contains('$$')) {
                file.renameTo(file.path.replace('$$', '$'))
            }
        }
    }
}
```

The above is about JaCoCo instrumentation in Q-testing. For details, you can see the cases in the [benchmark](https://github.com/njusegwgx/Q-testing-benchmark).

## Possible problems

- the generated coverage.ec file size is 0

  - Adding the following code to **build.gradle** (set `testCoverageEnabled = true`)

    ```gradle
    android {
        ...
        buildTypes {
        		...
            debug {
                testCoverageEnabled = true
            }
        }
        ...
    }
    ```

-  `gradle jacocoTestReportMerge1 ` doesn't generate report file, and no error infomation

  - the file path in task jacocoTestReportMerge1 is wrong

- `gradle jacocoTestReportMerge1 `  report **FileNotFoundException** (can't not find intermediates/classes)

  - copy -r app/build/  /build     (move the .class file under app/build/ to build/)

- `gradle jacocoTestReportMerge1 `  report `cannot read executation file`

  - Specify  JaCoCo version,  JaCoCo version is related to cradle version

    ```
    apply plugin: 'jacoco'
    
    jacoco {
        toolVersion = "0.7.6.201602180812"
        // alternative 0.7.1.201405082137
        // alternative 0.8.1
    }
    ```

    

