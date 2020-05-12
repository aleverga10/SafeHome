package safehome_ss.gui;

import java.util.Set;

import javafx.fxml.FXML;

import javafx.scene.layout.AnchorPane;
import safehome_ss.pubsub.Log;


public class GUI_SensorThingSceneController 
{
    @FXML
    AnchorPane sensorthing_rootPane; 

    private int currentSensor;
    private Integer maxActiveSensors;
    private final int DEFAULT_SENSOR = 0;

    private String[] sensorThingsUIDs; 

    public void initialize()
    {
        Set<String> keys = ModelDataLoader.getInstance().sensorThings.keySet(); 
        sensorThingsUIDs = keys.toArray(new String[keys.size()]); 
        loadDefaultSensor();
        maxActiveSensors = sensorThingsUIDs.length; 
    }

    public void loadSensor(int n) 
    {
        GUI_SensorThingController controller = (GUI_SensorThingController) GUI_Loader.getInstance()
                                                    .loadScene(GUI_Helper.PAGE_SENSORTHING, sensorthing_rootPane);

        controller.sensorThing_UID = sensorThingsUIDs[currentSensor]; 
        GUI_Helper.getInstance().controllers_SensorThing.put(controller.sensorThing_UID, controller); 
        controller.showScene();
    }

    private void loadDefaultSensor() 
    {
        currentSensor = DEFAULT_SENSOR;
        loadSensor(currentSensor);
    }

    public void prevSensorThing()
    {     
        currentSensor--; 
        if (currentSensor < 0)
            currentSensor = maxActiveSensors - 1; 

        loadSensor(currentSensor);
    }

    public void nextSensorThing()
    {
        currentSensor++; 
        if (currentSensor > maxActiveSensors - 1)
            currentSensor = 0;
        
        loadSensor(currentSensor);   
    }

 
}