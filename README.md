# Description

This repo showcases how to integrate Java / TestNG test for Native apps, using Appium with Applitools and execute on Genymotion Cloud

# About the tests
* The Test (GenyParallel.java) assumes the following:
    * There are 2 instances of the test running in parallel
    * The port numbers, udid are hard-coded in the test to make the demo easier. But this can / should be externalized for better flexibility / scaling
    * The below instructions (for Genymotion devices) assumes this hard-coded setup. The test infrastructure code should be able to make this dynamic at runtime

# Applitools Setup
* Create a free Applitools account: https://applitools.com/free
* Get your Applitools API key: https://help.applitools.com/hc/en-us/articles/360006914732-The-runner-key-API-Key-
* Set the **APPLITOOLS_API_KEY** environment variable: 
    - Mac: ```export APPLITOOLS_API_KEY='YOUR_API_KEY' ```
    - Windows: ```set APPLITOOLS_API_KEY='YOUR_API_KEY'```

# Genymotion Devices Setup

* Sign up on [Genymotion Cloud](http://cloud.geny.io/?&utm_source=github&utm_medium=integration&utm_campaign=applitools) to create an account
* Setup Genymotion command line tool - **gmsaas** using the instructions here - https://www.genymotion.com/blog/gmsaas-new-cli-automate-lifecycle-genymotion-cloud, or https://pypi.org/project/gmsaas/
* To check if you are connected to Genymotion Cloud, run

    ```    $ gmsaas auth whoami ``` 
    
    The above should show the username / email of your Genymotion Cloud account
* See list of devices available devices in Genymotion Cloud:

    ```    $ gmsaas recipes list | grep "Pixel 3" ```
    
    For this demo, I am going to use Google Pixel 3 devices, so I can narrow the search using the below command:
    
    ```    $ gmsaas recipes list | grep "Pixel 3" ```

    ```
    143eb44a-1d3a-4f27-bcac-3c40124e2836  Google Pixel 3         9.0        1080 x 2160 dpi 420  genymotion
    e5008049-8394-40fc-b7f8-87fa9f1c305f  Google Pixel 3 XL      9.0        1440 x 2960 dpi 560  genymotion
    ```
    
* Start 2 devices
    ```
    gmsaas instances start 143eb44a-1d3a-4f27-bcac-3c40124e2836 pixel3
    gmsaas instances start e5008049-8394-40fc-b7f8-87fa9f1c305f pixel3xl
    ```
    
    **_NOTE: The above commands will start a new instance of the devices in Genymotion Cloud. When you see the started devices, make note of the devices' udid._**
    Example:
    
    ```
    $ gmsaas instances list
    UUID                                  NAME      ADB SERIAL      STATE
    ------------------------------------  --------  --------------  -------
    8e70fac5-0dc5-449c-8458-6af6bd09e201  pixel3    0.0.0.0         ONLINE
    3553cc0c-c6cd-48be-99f6-b56fe86a7cb1  pixel3xl  0.0.0.0         ONLINE
    ``` 
    
    **_NOTE: The UDIDs listed above will keep changing. So do not copy / paste directly._**
    
* You can also see the started instances in Genymotion Cloud Ui - https://cloud.geny.io/app/default-devices
* Establish adb connection with the started instances
    ```
    $ gmsaas instances adbconnect --adb-serial-port 5554 <udid1>
    $ gmsaas instances adbconnect --adb-serial-port 5556 <udid2>
    ```
* You will now see the ADB serial port updated for the device instances.  
    ```
    $ gmsaas instances list
    UUID                                  NAME      ADB SERIAL      STATE
    ------------------------------------  --------  --------------  -------
    <udid1>                               pixel3    localhost:5554  ONLINE
    <udid2>                               pixel3xl  localhost:5556  ONLINE
    ```
* You will now be able to see these Genymotion Cloud virtual devices as a local device
    ```
    $ adb devices
    List of devices attached
    localhost:5554	device
    localhost:5556	device 
    ```

# Run the Tests
* From command line, start Appium server using the command shown below:
    ``` 
    $ appium -p 4723 &
    ```
    
* You can now run the test in parallel and see the test execute in Genymotion Cloud, and see the Visual Testing results in the Applitools Test Manager Dashboard
