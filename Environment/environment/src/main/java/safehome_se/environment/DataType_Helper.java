package safehome_se.environment;

import safehome_se.environment.Data.DataType;;

/**
 * DataType_Helper
 */
public class DataType_Helper 
{

    //public final String measure_StringFormat; 
        
    // list of accepted unit of measurement
    public static final String UOM_TEMP = "°C"; 
    public static final String UOM_HUMD = "%"; 
    public static final String UOM_SMOK = "% obs/m"; 
    public static final String UOM_MOTN = "Movimento";
    public static final String UOM_FLOD = "V";

    public static final String UOM_AIRQ_CO2 = "ppm"; 
    public static final String UOM_AIRQ_CO = "ppm"; 
    public static final String UOM_AIRQ_PM25 = "ppm"; 
    public static final String UOM_AIRQ_TVOC = "ppb"; 


    // list of variation speeds
    // ie. thread "tics" 
    // every "tic" the environment value will change on a %
    private static final Float SPD_TEMP = 2.5f;
    private static final Float SPD_HUMD = 2.5f; 
    private static final Float SPD_SMOK = 1f;
    private static final Float SPD_MOTN = 0f;
    private static final Float SPD_FLOD = 1.5f;
    private static final Float SPD_AIRQ = 2.5f; 
    
    public static final Float SMOKE_SENSOR_MINACCEPTABLEVALUE = 0.2f;
    public static final Float FLOOD_SENSOR_MINACCEPTABLEVALUE = 2.0f; 
    
    public String airqParam; 

    public static String GetUOM(DataType type, String airqParam)
    {
        switch (type)
        {
            case TEMPERATURE:   return UOM_TEMP; 
            case HUMIDITY:      return UOM_HUMD; 
            case MOTION:        return UOM_MOTN; 
            case SMOKE:         return UOM_SMOK; 
            case FLOOD:         return UOM_FLOD;
            case AIR_QUALITY: 
                switch (airqParam.toUpperCase())
                {
                    case "CO2": return UOM_AIRQ_CO2; 
                    case "CO": return UOM_AIRQ_CO; 
                    case "PM2.5": return UOM_AIRQ_PM25; 
                    case "TVOC": return UOM_AIRQ_TVOC; 
                    default: return ""; 
                }
            default:            return ""; 
        }
    }
    
    public static Float GetVariationSpeed(DataType type)
    {
        switch (type)
        {
            case TEMPERATURE:   return SPD_TEMP; 
            case HUMIDITY:      return SPD_HUMD; 
            case MOTION:        return SPD_MOTN; 
            case SMOKE:         return SPD_SMOK; 
            case FLOOD:         return SPD_FLOD;
            case AIR_QUALITY:   return SPD_AIRQ; 
            default:            return null; 
        }
    }
}