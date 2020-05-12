
package safehome_ss.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import safehome_ss.communication.OpenHAB_Caller;
import safehome_ss.communication.PubNub_Instance;
import safehome_ss.local.ScenarioController;
import safehome_ss.pubsub.*;
import safehome_ss.pubsub.Channel.ChannelType;
import safehome_ss.sensors.*;
import safehome_ss.sensors.Sensor.SensorStatus;

/**
 * gui_main
 */


public class GUI_Main extends Application 
{

    GUI_Helper helper; 
    PubSubManager pubSubManager; 

    @Override
    public void start(Stage primaryStage) throws Exception 
    {       
        initInstances(); 
        load();
        loadGUI(primaryStage);  
        run(); 
    }
    
    public static void main(String[] args) 
    {
        launch();
        // close everything including threads

        
        System.out.println("SCENARIO 1 RESULTS: ");
        // int correct = 0, failures = 0
        for (Sensor s : ModelDataLoader.getInstance().sensors.values())
        {
            System.out.println("SENSOR "+s.UID + " status "+ s.status);
            //correct += s.correctMeasurements; 
            //failures += s.failures; 
        }

        System.out.println("SCENARIO 4 RESULTS: ");
        System.out.println("timeouts: "+ScenarioController.getInstance().timeouts);
        /*
        int correct = 0, falsep = 0, falsen = 0; 
        for (Sensor s : ModelDataLoader.getInstance().sensors.values())
        {
            correct += s.correctCalls; 
            falsep += s.positiveFalses;
            falsen += s.negativeFalses;
        }
        System.out.println("SCENARIO 2 RESULTS: correct calls "+correct + " || Positive falses "+ falsep+ " || negative falses "+ falsen);
*/
        System.out.println("SCENARIO 2 RANDOM TYPE RESULTS: random errors: ");
        for (Sensor s : ModelDataLoader.getInstance().sensors.values())
        {
            System.out.println("SENSOR "+s.UID + " errors " +s.randomErrors);
        }


        Platform.exit();
        System.exit(0);
    }
    
    private void initInstances()
    {
        helper = GUI_Helper.getInstance(); 
        PubNub_Instance.getInstance();
        pubSubManager = PubSubManager.getInstance(); 
    }

    private void load()
    {      
        File path = new File(GUI_Helper.SENSORTHINGS_FILEPATH); 
        String[] files = path.list(); 

        for (String file : files) 
        {
            //System.out.println(file);

            LinkedHashMap<String, String> values = (LinkedHashMap<String, String>) loadModel(GUI_Helper.PAGE_SENSORTHING, file, ""); 
            fillModel(values);
        }
    }

    private Map<String, String> loadModel(String scene, String sensorThingUID, String sensorType)
    {
        // load model values from a config file to a map like (fx_id -> value)
        // for the corresponding scene
        Map<String, String> values = new LinkedHashMap<>();

        File sensorthingFile = new File(GUI_Helper.SENSORTHINGS_FILEPATH + sensorThingUID);

        // read
        Scanner reader;
        try 
        {
            reader = new Scanner(sensorthingFile);

            boolean wrongSensor = false; 

            while (reader.hasNextLine())
            {
                String line = reader.nextLine(); 
        
                // some lines are basically readme lines, 
                // or we've read sensorThing data when requesting sensor data
                // or, again, we've read another sensor's data
                // we can safely ignore those lines
                if (!(
                        (line.startsWith("*")) ||
                        (line.startsWith("[")) && (scene == GUI_Helper.PAGE_SENSOR) || 
                        (line.startsWith("{")) && (wrongSensor)
                    ))
                {
                    // we have encountered a line like (sensor) "value"
                    // where value is a sensor type (e.g. TEMPERATURE)
                    if (line.startsWith("("))
                    {
                        if (scene == GUI_Helper.PAGE_SENSOR)
                        {
                            // we can use that value to check if this is the correct sensor to readì
                            // e.g. if we're requesting humidity sensor data but we've found temperature data
                            String type = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
                            
                            // idk sometimes == fails with strings 
                            if (!type.equals(sensorType))
                                wrongSensor = true;
                            else
                                wrongSensor = false; 
                        }

                        // if the scene isn't PAGE_SENSOR, then it must be PAGE_SENSORTHING
                        else
                        {
                            // we can safely return when we've found this line
                            // we've already read all data we wanted
                            reader.close();
                            return values; 
                        }
                    }

                    if (line.startsWith("["))
                    {
                        // we get here when we've read a line starting with "["
                        // and we're requesting PAGE_SENSORTHING scene data

                        // that means, we've found the right data for this sensorThing
                        // pattern for sensor data is -> [paramID] "value"
                        String param = line.substring(1, line.indexOf("]")); 
                        String value = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\"")); 
                        
                        values.put(param, value); 
                    }

                    else if (line.startsWith("{"))
                    {
                        // we get here when we've read a line starting with "{"
                        // and the sensor is not wrong, and the page is not PAGE_SENSORTHING

                        // that means, we've found the right data for this sensor
                        // pattern for sensor data is -> {paramID} "value"
                        String param = line.substring(1, line.indexOf("}")); 
                        String value = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\"")); 

                        values.put(param, value); 
                    }
                }
            }

            reader.close();
        } 
        catch (FileNotFoundException e) 
        {
            // file not found
            e.printStackTrace();
        }

        return values; 
    }

    private void fillModel(Map<String, String> values)
    {
        // we create a temporary sensor thing object with default data
        Sensor_Thing temp = SensorThingsFactory.getInstance().CreateSensorThing(values.get("sensorThingModel")); 

        // we load data retrieved from file overwriting the temporary data
        temp.uid = values.get("sensorThingUID"); 
        temp.name = values.get("sensorThingName"); 
        temp.isPlacedIn = values.get("sensorThingPlacedIn");
        temp.model = values.get("sensorThingModel");

        // same thing, we have created a temporary sensor inside the thing object
        // we will fill its data with file content 
        // and we already know what type of sensors this Thing will have 
        // (from the Thing model, and from the fact sensor data are listed in the same order,
        // e.g. for model "THM" we have TEMP sensor first, then HUMD, then MOTN data) 
        for (Sensor tempSensor : temp.sensors)
        {
            LinkedHashMap<String, String> sensorValues = (LinkedHashMap<String, String>) loadModel(GUI_Helper.PAGE_SENSOR, temp.uid + ".txt", tempSensor.TYPE.name());
            fillModel_Sensor(tempSensor, sensorValues);
            // we sometimes need to show device (thing) id instead of sensor's id, but model does not know this data, so we need to keep track of it somewhere
            GUI_Helper.getInstance().SensorID_ToThingID.put(tempSensor.UID, temp.uid); 
        }

        ModelDataLoader.getInstance().sensorThings.put(temp.uid, temp);
    }

    private void fillModel_Sensor(Sensor temp, Map<String, String> values)
    {
        temp.UID = values.get("sensorUID");

        temp.NAME = values.get("sensorName"); 
        
        // motion detectors do not have a refresh time, they get "delivered" the value when a trigger happens in the environment
        if (values.get("refreshTime") != null)
            temp.refreshTime = Float.parseFloat(values.get("refreshTime"));

        temp.status = SensorStatus.valueOf(values.get("sensorStatus")); 
        temp.type_asShown = values.get("sensorType");
        
        ModelDataLoader.getInstance().sensors.put(temp.UID, temp); 

        createPubSubChannel(temp);
    }

    private void createPubSubChannel(Sensor sensor)
    {
        pubSubManager.CreateChannel(sensor.UID, ChannelType.SENSOR);
        Channel sch = pubSubManager.GetChannel_FromName(sensor.UID, ChannelType.SENSOR); 
        sensor.SubscribeTo(sch);
    }

    private void loadGUI(Stage primaryStage) throws IOException
    {
        GUI_Loader loader = GUI_Loader.getInstance();
        URL url = new File(loader.getScenePath(GUI_Helper.PAGE_HOME)).toURI().toURL(); 
        FXMLLoader fxmlLoader = new FXMLLoader(url); 
        Parent root = fxmlLoader.load();


        primaryStage.setTitle("SafeHome: Sensors");
        Scene scene = new Scene(root); 
        primaryStage.setScene(scene);
        loader.mainStage = primaryStage; 

        //primaryStage.setScene(new Scene(new AnchorPane()));
        primaryStage.show();
    }

    private void run()
    {
        for (Sensor_Thing sensor_Thing : ModelDataLoader.getInstance().sensorThings.values()) 
        {
            // temp finché non prendiamo i nomi di items e thing dalla sitemap
            //openHAB_Caller.storeOpenHABName_sensorThing(sensor_Thing.uid, sensor_Thing.isPlacedIn + "_" + sensor_Thing.uid);
            for (Sensor sensor : sensor_Thing.sensors) {
                OpenHAB_Caller.getInstance().storeOpenHABName_sensor(sensor.UID, sensor_Thing.isPlacedIn.replaceAll(" ", "") + "_" + sensor.UID);
            }

            //sensor_Thing.Start();    
        }
    }
}
