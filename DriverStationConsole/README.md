# Robot Controler in Emulators

This fork enables running the Robot Controller app in Android emulators. Currently
tested working emulators are,
* [Visual Studio Android Emulator](https://www.visualstudio.com/vs/msft-android-emulator/)
* [Oracle VirtualBox](https://www.virtualbox.org/) with [Android x86](http://www.android-x86.org/)

When running in an emulator, the RC app does not communicate with the official Driver
Station (DS) app. Instead, this fork provides a DS console app which can be run to
interact with the RC app in the emulator.

## Emulator Setup

### Visual Studio Android Emulator

Download the installer [here](https://www.visualstudio.com/vs/msft-android-emulator/). Before
installing the emulator, make sure that Hyper-V is enabled. You may need a Windows edition
that supports Hyper-V. Windows 10 Home edition does not support Hyper-V so it cannot be used
in this setup.

After the emulator is installed, install a profile (as shown in the following image).  
![alt text](https://github.com/robogo/ftc_app/tree/master/DriverStationConsole/media/ftc-rc-emulator-vs-profile.png "Install Profile")

Start the installed profile. Make sure the emulated device is connected to the same network as
where your computer is connected. If not, open Virtual Switch Manager in Hyper-V manager, make
sure there are two switches (Internal and External) created and both are enabled in the virtual
machine.

Once the emulator is started, Android Studio should list it in the devices list and be able to
adb connect to it. If not, try the following,
* enable Developer Options and USB debug in the emulated device.
* restart emulator and/or Android Studio
* find the IP address of the device and run `adb connect <IP>`.

### Oracle VirtualBox

Download VirtualBox and Android x86. Install VirtualBox. Install Android x86 from the downloaded
iso file. If you want to change the screen resolution, follow online documents, such as answers in
[this question](https://stackoverflow.com/questions/6202342/switch-android-x86-screen-resolution).

Enable Developer Options. Find the IP address of the emulated device (e.g. opening a terminal and
running the `ifconfig` command). Run `adb connect <IP>` to connect to the device. USB adb connect
does not seem to work in this case.

## Development Setup

Follow the common process to setup Android Studio and JDK.

Clone the repo from `https://github.com/robogo/ftc_app.git`. Open the project in Android Studio.

Select DriverStationConsole in the Project view and select menu `Run` -> `Make Module DriverStationConsole`
to build it.

Execute `Run TeamCode` from the menu or toolbar to build and deploy the app to the emulator.

Start the DS console by running the following command from the ftc_app home directory in a command
prompt
```
java -cp libs\gson-2.8.0.jar;DriverStationConsole\build\classes\main com.robogo.Console
```

The RC app and the DS console should find each other and start communicating. You should see
output from the command prompt.

The DS console also opens a simple UI window where you can see the available OpMode in the dropdown
list. After an OpMode is select, the middle button is enabled, which you can click to INIT/START/STOP
the OpMode. Telemetry is also sent from RC app to the DS console and is displayed in the bottom
window.  
![alt text](https://github.com/robogo/ftc_app/tree/master/DriverStationConsole/media/ftc-ds-console-ui.png "DS Console")

On HDPI screen the text in the console UI may be too small to read. This is a common issue for Java Swing
GUI on HDPI screen. Please consoult Google to find the solution.

If the RC app and the DS console cannot connect, check the following.
* Android device is connected to the same network as your computer does.
* Firewall is disabled on your computer.
* For VS emulator, the VM has both Internal and External switches enabled. If not, add the missing one.
* UDP is fully supported by the network. For example, some wireless network may not UDP in all cases.
Test it with your home network. In most cases it should not have this particular issue.
* If you are using a different Android image, set a breakpoint in the `Device.isEmulator` method in
the file `RobotCore\src\main\java\com\qualcomm\robotcore\util\Device.java` and check if extra check
of the Build properties should be added.

## Mock Hardware Devices

When the RC app runs in the emulator, it uses mocked hardware devices. The mock hardware devices are
created in `EmulatedHardwareFactory` in the following file.
```
FtcRobotController\src\main\java\com\robogo\EmulatedHardwareFactory.java
```

The name of the devices are hardcoded so make sure that you are using the same name to retrieve
a device from the hardware map. The robot configuration is completely ignored in emulated setup.

When an OpMode is active, the current values of all mocked devices are displayed in the centor of the screen.

There are two types of mocks.
* Output mock devices. Examples are DcMotor and Servo. These devices take a value and apply it on
the hardware to change something, e.g. motor power and servo position, etc.
* Input mock devices. Examples are LightSensor, TouchSensor and OpticalDistanceSensor. These are
also called sensor devices as they provide input to the RC app about certain aspects of the running
environment.

Data of input devices can be manipulated in two ways.
* `SensorMock.setTimeValues(double[] seconds, double[] values)`: this method uses an array of time
and an array of value that changes at the corresponding time to control the device. For example,
```
HardwarePushbot robot = new HardwarePushbot();
robot.init(hardwareMap);
OpticalDistanceSensor ods = hardwareMap.opticalDistanceSensor.get("sensor_ods");
((SensorMock)ods).setTimeValues(new double[] { 5.0, 6.0, 8.0 }, new double[] { 0.5, 0.1, 0.6 });
```

The above code sets the OpticalDistanceSensor to change its value to 0.5 at second 5, to 0.1 at second
6, and to 0.6 at second 8. The time starts when the OpMode INIT is called. You can set more entries
in the arrays but the two array must have the same items.

* `SensorMock.setDataSource(DataSource source)`: this methods sets a custom DataSource to generate
a value at a given time.
```
HardwarePushbot robot = new HardwarePushbot();
robot.init(hardwareMap);
OpticalDistanceSensor ods = hardwareMap.opticalDistanceSensor.get("sensor_ods");
((SensorMock)ods).setDataSource(new DataSource() {
            @Override
            public double getData(double time) {
                return Math.sin(time);
            }
        });
```

The above code sets a function of time to provide values of the given sensor. It allows for more
advanced control of a sensor.

## Screen Shots
The Visual Studio Android Emulator  
![alt text](https://github.com/robogo/ftc_app/tree/master/DriverStationConsole/media/ftc-rc-emulator-vs.png "VS Emulator")

Android x86 in VirtualBox  
![alt text](https://github.com/robogo/ftc_app/tree/master/DriverStationConsole/media/ftc-rc-emulator-vb.png "VirtualBox & Android x86")
