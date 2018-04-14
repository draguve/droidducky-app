# DroidDucky-App
![DroidDucky](https://raw.githubusercontent.com/draguve/droidducky-app/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png)

DroidDucky is an Android app to run USB Rubber Ducky type attack through your Android phone 
Computers recognize it as a regular keyboard and accept pre-programmed keystroke payloads

## Improvements
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

* It also has a http server which can be enabled from the "Enable Server" option inside the app it will server all files stored in the Droidducky/host directory. The server can be accessed over wifi as well as over RNDIS for windows , just tether the android from the settings menu if the in app option doesn't work 

## Prerequisites
* Rooted device with unlocked bootloader
* Kernel with HID Patch (Look for kernels at the xda forum for your device)
* Check this link for device specific procedure if you can't find a kernel on xda : [link](https://github.com/pelya/android-keyboard-gadget)

## Sources

* The original hid-gadget-test is taken from [pelya](https://github.com/pelya/android-keyboard-gadget) used to write the keys to usb
* [Nanohttpd](https://github.com/NanoHttpd/nanohttpd) used to create a http server on usb ethernet
* [material-dialogs](https://github.com/afollestad/material-dialogs) used to create dialog boxes
* [MaterialEditText](https://github.com/rengwuxian/MaterialEditText) to create better looking textboxes
* The source for [DuckEncoder](https://github.com/hak5darren/USB-Rubber-Ducky/tree/master/Encoder) is used to support all the languages it does
