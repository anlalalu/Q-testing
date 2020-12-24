## Q-testing

Q-testing is an automated testing tool for Android applications. It uses a reinforcement-learning based curiosity-driven strategy to explore the state space of the application under test.

For more technical details, please refer to our ISSTA '20 paper.

```
@inproceedings{10.1145/3395363.3397354,
author = {Pan, Minxue and Huang, An and Wang, Guoxin and Zhang, Tian and Li, Xuandong},
title = {Reinforcement Learning Based Curiosity-Driven Testing of Android Applications},
year = {2020},
isbn = {9781450380089},
publisher = {Association for Computing Machinery},
address = {New York, NY, USA},
url = {https://doi.org/10.1145/3395363.3397354},
doi = {10.1145/3395363.3397354},
booktitle = {Proceedings of the 29th ACM SIGSOFT International Symposium on Software Testing and Analysis},
pages = {153–164},
numpages = {12},
keywords = {Android app testing, reinforcement learning, functional scenario division},
location = {Virtual Event, USA},
series = {ISSTA 2020}
}
```

## Installation

#### System Requirements

- Python: 2.7
- Android SDK: API 19 (make sure `adb` and `aapt` commands are available)
- Linux: Ubuntu 16.04
- Gradle: (for instrumented apks)
- uiautomator: `pip install uiautomator==0.3.6`

The above versions of software have been tested in our experiments. We use Pyinstalller to bundle the Q-testing project into executable files which can be run in Linux.  You don't need to install the tool or any other python dependency; just download all the files will suffice. The application under test may be installed in a physical phone connected to a computer or in an Android virtual machine, of which the Genymotion Custom Phone (SDK 4.4, API level 19) has been tested.

## Usage

The artifact of Q-testing is shared with OneDrive, and can be downloaded [Here](https://1drv.ms/u/s!AmfV7AZ50ULThzDYTBcerncLZzWF?e=OWKl5S)

#### Subject Requirements

The applications under test can be open-source or closed-source. However, if you need test coverage information, source code is required and the application should be instrumented with JaCoCo first and then built as an APK file (see JaCoCo/README for detailed usage).

- The instrumented APK should be compiled and named with suffix "-debug.apk"
- The closed-source/non-instrumented APK name should end with ".apk"

#### Settings

Before running Q-testing, please create the **CONF.txt** as follow: 

```
[Path]
#Note: The apks should be placed under 'Benchmark' directory.
Benchmark = /Users/Your_Name/Benchmark/
APK_NAME = your_app_under_test.apk | your_app_under_test-debug.apk

# For instrumented APKs
APP_SOURCE_PATH = /Users/Your_Name/Projects/test
MANIFEST_FILE = /Users/Your_Name/Projects/test/src/main/AndroidManifest.xml


[Setting]
# You can use command `adb devices` to get the Android device's ID replace the DEVICE_ID.
DEVICE_ID = 192.168.72.1:5555
# You may change TIME_LIMIT for debug.
TIME_LIMIT = 3600
```

#### Running

For applications that require permission or login, you may want to install the app on your Android device and grant the permissions or login the account before testing. Then you can start testing:

1. Run the application

   ```shell
   ./Q-testing/main -r CONF.txt
   ```

2. Calculate the test coverage (for instrumented APKs)

   ```shell
   ./Q-testing/main -c CONF.txt
   ```

#### Output

The output contents are placed in folder `benchmark/<apk_name>_output-1/`

1. Run app to get coverage file

```
/coverage_merge_1
/coverage_original_1 --  These two folders contain the coverage files used to calculate the final code coverage. (for instrumented apks)

/event_output -- The structures and actionable events of each GUI screen.
/crash_log.txt -- Test results including the test cases and recorded crashes and exceptions.
```

2. Calculate coverage  (for instrumented apks)

```
/coverage_new_1/curve_data_all.txt -- The final code coverage.
```

