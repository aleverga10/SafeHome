package safehome_ss.sensors;

import java.util.ArrayList;
import java.util.List;

import safehome_ss.sensors.Data.DataType;

/*not exactly a factory in the sense of a  "factory pattern" 
(ie. Sensor_Thing is *not* an abstract class / an interface, so there is no need to have a factory pattern)
but 
to be consistent with the sensors factory (which *is* a factory pattern instead)
and to have more than one constructor with many but standardized options (e.g. battery full, battery empty, etc.)
and (mainly) to have consistent sensorsThings naming (ie. their uid)
i created this class. 
*/

public class SensorThingsFactory 
{

    //Singleton pattern
    private static SensorThingsFactory instance = null; 
    public static SensorThingsFactory getInstance()
    {
        if (instance == null)
            instance = new SensorThingsFactory();

        return instance; 
    }

    private int currentUID = 0; 

    private final float BATTERY_STARTING_PERCENTAGE = 100f; 
    private final int BATTERY_MAX_DURATION_SECONDS = 1*60*60;  //1 hour

    // valid model names are a combination of these basic sensors names (e.g. "THA", "THS", "TSM", "T", "HSM", "THASM", ...)
    // basic sensors names (T = temperature only, H = humidity only, ...)
    private final char MODEL_T = 'T'; 
    private final char MODEL_H = 'H'; 
    private final char MODEL_A = 'A'; 
    private final char MODEL_S = 'S'; 
    private final char MODEL_M = 'M'; 
    private final char MODEL_F = 'F'; 

    public Sensor_Thing CreateSensorThing(String modelName)
    {
        currentUID++; 
        
        //formats UID to be "0x" if x < 10 or just "x" otherwise (eg. sensor "09" || sensor "10")
        String s = currentUID < 10 ? "0"+String.valueOf(currentUID) : String.valueOf(currentUID); 

        Battery b = new Battery(BATTERY_STARTING_PERCENTAGE, BATTERY_MAX_DURATION_SECONDS); 

        // get which sensors to have from model name
        List<DataType> sensorsToHave = new ArrayList<DataType>(); 
        for (char c : modelName.toCharArray()) 
        {
            switch(c)
            {
                case MODEL_T: sensorsToHave.add(DataType.TEMPERATURE);  break;
                case MODEL_H: sensorsToHave.add(DataType.HUMIDITY);     break;
                case MODEL_A: sensorsToHave.add(DataType.AIR_QUALITY);  break;
                case MODEL_S: sensorsToHave.add(DataType.SMOKE);        break;
                case MODEL_M: sensorsToHave.add(DataType.MOTION);       break;
                case MODEL_F: sensorsToHave.add(DataType.FLOOD);        break;
                default: break; 
            }
        }

        return new Sensor_Thing(s, modelName + "_"+ s, b, sensorsToHave); 
    }
    
}