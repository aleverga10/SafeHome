# SafeHome 4 Uncertainty

Uncertainty situations in smart homes are often the cause of the alarm system’s malfunctioning. 
The process of acquiring knowledge on the nature and causes of these non deterministic anomalies is not trivial. 

However, in a virtual environment, when said uncertain situations are correctly modeled, it is possible to observe the behaviour of a software system in response to those events in isolated and controlled conditions. 



# What is SafeHome 4 Uncertainty?

A software system simulating a smart home and its functioning through a Java virtual environment. Some <b>uncertainty situations</b> typically found in reality, ranging from measurement errors to sensors malfunctioning, are carefully injected into this isolated and controlled virtual environment, and the system's behaviour can be observed in response to such situations. 

The smart home itself is a realization as a software of the case study "SafeHome", an academic project invented by Dr. Roger Pressman, Ph.D., in his textbook "Software Engineering: a Practitioner’s Approach".
A remote control panel is built using "openHAB" (https://www.openhab.org/), an automation open-source software developed by openHAB Foundation, which lets users construct a remote interface from which they can control their smart homes. 

This project composes of two open source applications, both written in Java, : <b>Sensors</b> and <b>Environment</b>. The first is used to model (in a non-physical way) the inner workings of devices, in order to acknowledge the presence of precise undesired situations (e.g. temperature too high, presence of smoke, or motion, etc.) stated in the case study; the latter app is used to simulate a smart home and its rooms.  

The openHAB server instance models the remote control panel of the smart home environment, and serves both as a dashboard to visualize sensor's data (e.g. to remotely check temperature in room Living Room), and as a way to remotely arm and disarm the alarm system. The alarm is modeled as a field in the server's dashboard. It uses the web service CallMeBot (https://www.callmebot.com/) to perform a Telegram call to a specific user. You may change the target user by editing the .rules file as explained below, as the call is commented by default.

The provided configuration can be run locally: once you have correctly installed and started the server, you can view its dashboard and Safehome's sitemap at http://localhost:8080/ . 

---------------------------------------------------------

This system represents my masters thesis' work at Politecnico di Milano university, Italy, developed in 2020 (A.Y 18/19), in which SafeHome was used to provide a controlled environment for occurrences of common uncertainty situations in real life (e.g. sensors failure, measurement error). Its behaviour was observed in response to those uncertainties.




# How does it work?

The smart home is modeled inside the Environment application. 
The provided software's house, by default, is composed by two rooms, "demo_bathroom" and "demo_livingroom". 
Environment app constantly listens to data measurement requests on a realtime communication channel (developed using PubNub's Pub/Sub service). Measurement requests come from the Sensors app: each sensor modeled in it has a refresh time, by default equal to 10 seconds, after which it will send a measurement request to Environment. Once Environment has checked the correct room, it sends back the correct room data, and the sensor performs some internal calculations (such as decimal sensitivity checks) and it outputs a value. 
Outputs can be viewed both using Sensors "local" GUI and the openHAB's server instance (the "remote" UI). Sensors app also contains sensors' statuses and other info about them. 
This "local" interface is standalone from the openHAB server; both applications (Sensors and Environment) infact just communicate with openHAB server through the provided binding in order to perform alarm checks, but they can work independently from it.


Users can control an environmental data by using the Environment UI: he or she can decide when and how much to variate any supported data type (e.g. increasing temperature or humidity or presence of smoke etc. in the living room), by specifying some parameters such as "variation speed" (ranging from "decrease very fast" to "increase very fast"), and "target value". 
No physics laws are involved in this variations, they vary based on the "variaton speed" turned into a percentage of the current value.


Users can also use openHAB remote interface to view sensor's outputs, arm and disarm the alarm system and view all the "undesirable situations" that will trigger the alarm (e.g. relative humidity > 70%, which is considered harmful to people). 




# Uncertainty situations

Three uncertainty situations are provided with this system: "sensor failure", "measurement errors" and "openHAB update delay" (ie. internet connection delays or errors). The system's behaviour can be observed in response to such situations. It is suggested to check the property "number of alarms triggered by the system", by checking the openHAB remote interface appropriate field "Testing scenarios -> Number of alarms, count". 

Users can select any of these situations using the Sensors UI, by clicking "Test Scenarios" in the side menu. A parameter is usually requested, such as failure probability for sensors' failure scenarios. 

- SENSOR FAILURE scenario: each sensor can permanently "break" (until the user closes the app or restarts their device). Failure probability, asked as an input, is checked before applying a measurement. This means every 10 seconds (ie. sensor's refresh time) a failure check is performed. If a sensor fails, it goes into the FAILURE status: this can be visualized in its UI page.

- MEASUREMENT ERROR scenario: two types of errors can be observed in this scenario. The first, called "GAUSSIAN", applies an uncertainty to a measure (e.g. +- 2%) based on a gaussian distribution centered on the real environmental value with sigma = the sensor's sensitivity. Sensitivities are taken from real sensors that can be found commercially. More details here (thesis link). Uncertainty on measurement happen on every measure.
The latter, called "RANDOM", models situations in which sensors occasionally output a wrong value (e.g. 54 °C in the living room instead of 22 °C). This happens randomly based on the inputted probability on eevry measure. 

- OPENHAB DELAY scenario: basically a simulation of networking connectivity delays and timeouts. Users are asked to input a max delay (in milliseconds). On every measure, the system will delay the openHAB runtime update by a random value in the range (0, maxDelay). A timeout threshold is set, hard-coded, to 4000ms. When a delay is greater than 4000ms, the update is never performed (ie. connection timeouts). 



An execution context in which these tests can be run has been standardized and it is provided with the software system. Users can activate this execution context in the Environment application's UI, by clicknign the side menu button "Test scenario". This had to be done because otherwise data would be still and tests would be meaningless. 

This STANDARD CONTEXT lasts for approximately 9 minutes and 30 seconds. 
During this time, each data that can be sensed by the software system's sensors gets variated at precise times, with predetermined variations speed and to predefined target values (for reference, the first is a motion trigger with value 0.92 in the living room at t1 = 12 seconds.). 

The observed property in each scenario, "number of alarms triggered by the system", can be checked versus the same property in the standard context, which from 10 already conducted tests resulted in an average of A = 20.36 alarms. 

More details on uncertainty scenarios, standard context and data variations here (thesis link). 




# What does this repo provide?

- The source code of the two applications that composes the SafeHome 4 Uncertainty system (Sensors, Environment). You can find the Java source code in the root folder, at paths /Sensors and /Environment.
- The source code of the openHAB configuration (in the folder /conf). This folder contains all the needed openHAB's items, rules, things, transforms, and provides a working sitemap. Please refer to openHAB official documentation for more informations about openHAB concepts (https://www.openhab.org/docs)
- The source code of the openHAB "binding" developed for SafeHome 4 Uncertainty, in which a list of supported devices (more specifically, openHAB things and channels) can be found. They can be installed through the openHAB runtime using its "Paper UI". 
- A .jar file package containing the binding itself. 

More instructions on where to place this content below.




# Installation Guide

- Download or clone the repository.
- Download a release of the openHAB server (https://www.openhab.org/download/)
- Follow the official openHAB user installation guide. (https://www.openhab.org/docs/installation/)
- Follow the official openHAB developer installation guide. (https://www.openhab.org/docs/developer/)


Once the openHAB is installed, you should have a folder named like "openhab-version" (e.g. "openhab-2.5.2"). 

- Open this folder and navigate to /addons.
- Place the .jar file provided in the root folder of the repository (org.openhab.binding.safehome... etc) in this folder. 
- Navigate to openhab's /addons/openhab-addons/bundles/ folder. If you cloned openHAB repo correctly, you should see a list of all supported bindings. 
- Place the org.openhab.binding.safehome... etc folder you find in the root of this repository inside this folder, alongside the other bindings. This is needed because SafeHome 4 Uncertainty binding is not distributed and published to the openHAB repository (yet.), so you need to manually place it inside the list.

- Navigate back to openhab server root folder, and go to /conf folder. 
If yours is a clean installation of openHAB server, you can safely replace all the content of this folder (or the folder itself) with the provided contents of /conf folder you can find in the root of this repository. 
/ ! \ IMPORTANT: if you have already worked with openHAB, dont replace all the folders content. Instead, just add the following files which are necessary for the system to work on the server side.
    -- safehome.items file inside /conf/items;
    -- safehome.rules file inside /conf/rules;
    -- safehome.sitemap file inside /conf/sitemap;
    -- safehome.things file inside /conf/things;
    -- safehome.map and safehomealarm.map files inside /conf/transform;

OPTIONAL: If you want the system to call you when the alarm is triggered, you can now edit the safehome.rules file inside /conf/rules in order to change the Telegram user who will receive a call (by the CallMeBot web service API) when an alarm is triggered. The line is commented by default, and it looks like this: ---> sendHttpGetRequest(...&user=[your telegram username goes here]&...).



- Start the openHAB server by executing start.bat file inside your release root folder. You can navigate to http://localhost:8080/ after a few seconds to use the openHAB dashboard. You can use your preferred UI, but my suggestion is to use the Basic UI and open the provided sitemap. 
- Once you've done this, you should see the system's "remote" dashboard, with text fields that will contain sensor's data, a switch to arm and disarm the house alarm. 


How to run the system:

- Open both Environment and Sensors project folders with your favourite Java IDE. The system has been developed using Visual Studio Code (which is a text editor, not an actual IDE).
- Navigate to the GUI_Main.java file. You can find it inside \environment\src\main\java\safehome_se\gui\ folder, and sensor's equivalent for the other application.
- Run the main files in any order. 





# User guide: how to use the provided demo

The provided system works already without any modifications. It is infact a working demo, but can be extended to provide users a custom experience. More details in the appropriate section below. 


Run both Environment and Sensors applications.
Once you have powered the devices using the main page UI, every 10 seconds (by default) sensors modeled in the Sensor app will update their values (e.g. the temperature sensor inside the device "Demo Device 00" after 10 seconds will change its value from "N/A" to "20.0 °C" ).

If you don't do anything, environmental data will remain constant. 
You can change rooms' data (e.g. increase bathroom humidity value) using the provided UI in the Environment app, by clicking "variate data". 
Once you've done this, go back to the Environment main page with the side menu, navigate to the correct room and you will see that data are being variated automatically. 

You can test the three provided uncertainty situations by clicking "Test Scenario" on the side menu in both applications. The Environment app will start the standard context, whereas the Sensors app will ask to choose which uncertainty scenario to test and to input some necessary parameters (such as event probability). 


# FAQs

-   <b>Q: Can i edit the smart home configuration?</b>
    A: Yes. You can both add more rooms and devices with sensors, or edit existing ones. This can be done by adding (or editing existing) files in both the apps' /local folders. This files are checked in the initialization process by the main, so changes need to be performed before running the apps. Such files follow a small DSL-like pattern, as parameters and values need to be specified with precise textual syntax. Please refer to the provided files for a guide on how to edit the systems' configuration. 

-   <b>Q: If i add a device / room by changing files, do i need to update openHAB conf?</b>
    A: Only if you want to access the openHAB remote instance (the apps are standalones and work independently from it). However, if you added or edited a room, you would need to create a new Group item in both .items and .sitemap openHAB files; if you added or edited a device, or a sensor, you would need to install a new Thing by using openHAB's Paper UI (or by editing textual .things files, that's equivalent). OpenHAB docs cover these passages in detail (https://www.openhab.org/docs). 

-   <b>Q: Can i edit the undesidered situations threshold values?</b>
    A: Yes. They are specified inside the .rules file in the /conf/rules folder. Current values are taken from various health organizations.

-   <b>Q: Is there an .exe file? </b>
    A: Not yet. The system is still in its beta version. You can run the applications by executing the GUI_Main.java files

-   <b>Q: Can i add another environmental execution scenario, or edit the existing standard one?</b>
    A: It depends on what you want to do. Theoretically you could create a new execution scenario using the Environment UI, by simply manually varying the data you wish to vary, when you need to. The provided standard execution context is specified in the java file GUI_ScenarioSceneController (located in the /gui folder in the Environment app), and it is hard coded as a list of scheduled threads who will each perform a variation request (by raising a VARIATE_DATA_REQUEST event). You could edit this class or create a new one with the same structure to create a new execution context. 


