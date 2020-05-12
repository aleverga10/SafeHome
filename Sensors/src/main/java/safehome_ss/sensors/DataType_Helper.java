package safehome_ss.sensors;

import safehome_ss.sensors.Data.DataType;

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

    //public final String UnitOfMeasurement;
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
            
}