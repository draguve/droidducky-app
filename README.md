# DroidDucky-App
![DroidDucky](https://raw.githubusercontent.com/draguve/droidducky-app/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png)

DroidDucky is an Android app to run USB Rubber Ducky type attack through your Android phone 
Computers recognize it as a regular keyboard and accept pre-programmed keystroke payloads

## Improvements
### Duckyscript
* It has a new "write_file" command which can be used to write the entire content of a file throught usb.It can be used as:  
    ```
    WRITE_FILE filename.extension    
    ```
    where the filename.extension is stored in the droidducky/code directory on your device
* "local_ip" and "wifi_ip" can be used to input the ip of the rndis driver and wifi respectivly   
    ```
    LOCAL_IP
	WIFI_IP
    ```
    where the filename.extension is stored in the droidducky/code directory on your device
### HTTPServer
* It also has a http server on port 8080 which can be enabled from the "Enable Server" option inside the app it will server all files stored in the Droidducky/host directory. The server can be accessed over wifi as well as over RNDIS for windows , just tether the android from the settings menu if the in app option doesn't work 
* The server stores any post requests sent to the server at the Droidducky/responses directory in the  Internal Storage of the Device

### Javascript
* The app also has a javascript interpreter and js scripts can be stored in the Droidducky/JavaScript directory on the internal storage of the device. it uses the j2v8 api to map hid functions to js ones
* In a JS file ``` ducky.SendString(String text) ``` send a string through the USB
* ``` ducky.SendCommand(String command) ``` send a command through the USB (for example "Gui r","CRTL SHIFT R" etc)
* ``` ducky.Log(String text) ``` similar to REM in duckyscript
* ``` ducky.Delay(Integer timeinms) ``` delay similar to duckyscript
* ``` ducky.WriteFile(String filename) ``` writes a file char by char using the hid
* ``` ducky.PrintIP(Boolean wifi) ``` write the IP of the android device , if true then the wifi ip else the rndis ip

ExampleScript
```
ducky.SendCommand('GUI r');
ducky.Delay(1000);
ducky.SendString('notepad');
ducky.Delay(1000);
ducky.SendCommand('enter');
ducky.Delay(1000);
ducky.WriteFile('draguve.txt');
```


## Prerequisites
* Rooted device with unlocked bootloader
* Kernel with HID Patch (Look for kernels at the xda forum for your device)
* Check this link for device specific procedure if you can't find a kernel on xda : [link](https://github.com/pelya/android-keyboard-gadget)

## Dependencies
* The original hid-gadget-test is taken from [pelya](https://github.com/pelya/android-keyboard-gadget) used to write the keys to usb
* [Nanohttpd](https://github.com/NanoHttpd/nanohttpd) used to create a http server on usb ethernet
* [material-dialogs](https://github.com/afollestad/material-dialogs) used to create dialog boxes
* [MaterialEditText](https://github.com/rengwuxian/MaterialEditText) to create better looking textboxes
* The source for [DuckEncoder](https://github.com/hak5darren/USB-Rubber-Ducky/tree/master/Encoder) is used to support all the languages it 
* [J2V8](https://github.com/eclipsesource/J2V8) java bindings for v8 on android for the javascript interpreter 
