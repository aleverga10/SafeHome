package safehome_ss.sensors;

import safehome_ss.sensors.Data.DataType;

public class SensorsFactory 
{    

    //Singleton pattern
    private static SensorsFactory instance = null; 
    public static SensorsFactory getInstance()
    {
        if (instance == null)
            instance = new SensorsFactory();

        return instance; 
    }

    private int currentUID = 0; 

    public Sensor CreateSensor(DataType sensorType)
    {
        // we only need this when creating a new sensor not when reading it from config files actually
        currentUID++; 
        
        //formats UID to be "0x" if x < 10 or just "x" otherwise
        String s = currentUID < 10 ? "0"+String.valueOf(currentUID) : String.valueOf(currentUID); 

        switch (sensorType) 
        {
            case TEMPERATURE:   return new Sensor_Temperature   ("TEMP_"+s, "Temperature sensor "+s);
            case AIR_QUALITY:   return new Sensor_AirQuality    ("AIRQ_"+s, "Air quality sensor "+s);
            case SMOKE:         return new Sensor_Smoke         ("SMOK_"+s, "Smoke detector "+s);
            case HUMIDITY:      return new Sensor_Humidity      ("HUMD_"+s, "Humidity sensor "+s);
            case MOTION:        return new Sensor_Motion        ("MOTN_"+s, "Motion sensor "+s);
            case FLOOD:         return new Sensor_Flood         ("FLOD_"+s, "Flood detector "+s);
        /*  
            case PRESENCE:      return new Sensor_Presence      ("PRSN_"+s, "Presence sensor "+s);
            case CAMERA:        return new Sensor_Camera        ("CAMR_"+s, "Camera "+s);
        */
            default:
                break;
        }

        currentUID--; 
        return null; 
    }
    
}