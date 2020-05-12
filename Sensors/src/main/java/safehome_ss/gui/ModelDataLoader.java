package safehome_ss.gui;

import java.util.HashMap;
import java.util.Map;

import safehome_ss.sensors.Sensor;
import safehome_ss.sensors.Sensor_Thing;

public class ModelDataLoader 
{
    public Map<String, Sensor_Thing> sensorThings;
    public Map<String, Sensor> sensors; 

    // Singleton pattern
    private static ModelDataLoader instance = null;

    public static ModelDataLoader getInstance() 
    {
        if (instance == null)
            instance = new ModelDataLoader();

        return instance;
    }

    private ModelDataLoader()
    {
        sensorThings = new HashMap<String, Sensor_Thing>();
        sensors = new HashMap<String, Sensor>(); 
    }


}