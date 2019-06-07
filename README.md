# Description

This repo showcases how to integrate Java / TestNG test for Native apps, using Appium with Applitools and execute on Genymotion Cloud

# About the tests
* The Test (GenyParallel.java) assumes the following:
    * There are 2 instances of the test running in parallel
    * The port numbers, udid are hard-coded in the test to make the demo easier. But this can / should be externalized for better flexibility / scaling
    * The below instructions (for GenyMotion devices) assumes this hard-coded setup. The test infrastructure code should be able to make this dynamic at runtime

# Applitools Setup
* Create an Applitools account
* Get the Applitools API key from the Applitools Dashboard, or from the email you would have received with the key, and set is as environment variable - **APPLITOOLS_API_KEY**

# GenyMotion Devices Setup

* Create a GenyMotion Cloud account (https://www.genymotion.com/account/create/)
* Setup GenyMotion utility - **gmsaas** using the instructions here - https://www.genymotion.com/blog/gmsaas-new-cli-automate-lifecycle-genymotion-cloud, or https://pypi.org/project/gmsaas/
* To check if you are connected to GenyMotion Cloud, run

    ```    $ gmsaas auth whoami ``` 
    
    The above should show the username / email of your GenyMotion Cloud account
* See list of devices available "Google Pixel" in GenyMotion Cloud:

    ```    $ gmsaas recipes list | grep "Pixel 3" ```

    ex:
    ```
    143eb44a-1d3a-4f27-bcac-3c40124e2836  Google Pixel 3         9.0        1080 x 2160 dpi 420  genymotion
    e5008049-8394-40fc-b7f8-87fa9f1c305f  Google Pixel 3 XL      9.0        1440 x 2960 dpi 560  genymotion
    ```
    
    **_NOTE: The UDIDs listed above will keep changing. So do not copy / paste directly._**
    
* Start 2 devices
    ```
    gmsaas instances start <udid1> pixel3
    gmsaas instances start <udid2> pixel3xl
    ```
* You can see the started instances in GenyMotion Cloud Ui - https://cloud.geny.io/app/default-devices
* Establish adb connection with the started instances
    ```
    $ gmsaas instances adbconnect --adb-serial-port 5554 <udid1>
    $ gmsaas instances adbconnect --adb-serial-port 5556 <udid2>
    ```
* See the list of GenyMotion Cloud devices connected 
    ```
    $ gmsaas instances list
    UUID                                  NAME      ADB SERIAL      STATE
    ------------------------------------  --------  --------------  -------
    <udid1>                               pixel3    localhost:5554  ONLINE
    <udid2>                               pixel3xl  localhost:5556  ONLINE
    ```


# Run the Tests
* From command line, start 2 instances of Appium server using the commands shown below:
    ``` 
    $ appium -p 4723 &
    $ appium -p 4724 & 
    ```
    
* You can now run the test in parallel and see the test execute in GenyMotion Cloud, and see the Visual Testing results in the Applitools Test Manager Dashboard
