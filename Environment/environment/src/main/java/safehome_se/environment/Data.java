package safehome_se.environment;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;

public class Data
{
    public enum DataType 
    { 
        // * = value placeholder
        // @ = unit of measurement placeholder
        TEMPERATURE ("* @", DataType_Helper.UOM_TEMP), 
        HUMIDITY("RH: *@", DataType_Helper.UOM_HUMD), 
        AIR_QUALITY("CO2: * @ - CO: * @ - PM2.5: * @ - TVOC: * @", "?"), 
        SMOKE("* @", DataType_Helper.UOM_SMOK), 
        /* 
        PRESENCE,*/ 
        MOTION("@ *", DataType_Helper.UOM_MOTN),
        FLOOD("* @", DataType_Helper.UOM_FLOD)
        /*CAMERA*/; 

        private final String format; 
        private final String UOM; 

        private DataType(String format, String unitOfM)
        {
            this.format = format; 
            this.UOM = unitOfM; 
        }

    }

    public final DataType TYPE; 
    public String value; 
    // change the number of zeroes to increase or decrease real data sensitivity
    private final String DECIMAL_FORMAT_STRING = "#0.000000"; 

    // for any but AIRQ data type this constructor works
    public Data(DataType type, float initialValue)
    {
        this.TYPE = type; 
        this.value = FormatValue(initialValue, type, null); 
    }

    public Data(DataType type, float initialValue, String airqParam)
    {
        this.TYPE = type; 
        
    }

    // for an AIRQ data type this constructor must be used
    public Data(Map<String, Float> airqParams)
    {
        this.TYPE = DataType.AIR_QUALITY; 
        this.value = DataType.AIR_QUALITY.format; 

        for (Map.Entry<String, Float> entry : airqParams.entrySet()) 
        {   
            this.value = FormatValue(entry.getValue(), this.TYPE, entry.getKey()); 
        }
    }

    public Data(DataType type)
    {
        this.TYPE = type; 
        this.value = this.TYPE.format; 
    }

    public Data(DataType type, String value)
    {
        this.TYPE = type; 
        this.value = value; 
    }

    public String FormatValue(float value, DataType type, String airqParam)
    {
        if (type != DataType.AIR_QUALITY)
            return FormatValue(value, type, airqParam, type.format); 
        else
            return FormatValue(value, type, airqParam, this.value); 
    }

    private String FormatValue(float value, DataType type, String airqParam, String format)
    {
        String s = format;

        // all but an AIRQ data have one value and one unit of measurement
        if (type != DataType.AIR_QUALITY)
        {
            // format all floats with . as a decimal separator (e.g. 150.0015f)
            // because otherwise Float.parseValue breaks if it finds a , 
            DecimalFormat dcf = new DecimalFormat(DECIMAL_FORMAT_STRING); 
            dcf.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
            String v = dcf.format(value);
            s = s.replaceFirst("\\*", v);
            s = s.replaceFirst("@", type.UOM);
        }

        else 
        {
            // a valid AIRQ Data.value is like "CO2: 500 ppm - CO: 5 ppm - PM2.5: 6.5 ppm - TVOC: 70 ppb"
            int i = s.indexOf(airqParam+":");
            int j = s.indexOf(" - ", i);
            if (j == -1)
                j = s.length(); 
            String oldSubstr = s.substring(i, j);
           
            // format all floats with . as a decimal separator (e.g. 150.0015f)
            // because otherwise Float.parseValue breaks if it finds a , 
            DecimalFormat dcf = new DecimalFormat(DECIMAL_FORMAT_STRING); 
            dcf.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
            String v = dcf.format(value);
            
            String newSubstr = oldSubstr.replaceFirst("\\*", v); 
            newSubstr = newSubstr.replaceFirst("@", DataType_Helper.GetUOM(DataType.AIR_QUALITY, airqParam)); 
            s = s.replace(oldSubstr, newSubstr); 

        }

        return s; 
    }


    public String VariateValue_FromFloat(float newValue, String airQualityParam)
    {
        Float oldValue = null; 
        switch (TYPE)
        {
            case TEMPERATURE:   oldValue = this.ToTemperature();  break;
            case SMOKE:         oldValue = this.ToSmoke(); break;
            case HUMIDITY:      oldValue = this.ToHumidity(); break;
            case AIR_QUALITY:   oldValue = this.ToAirQuality(airQualityParam); break;
            case MOTION:        oldValue = this.ToMotion(); break; 
            case FLOOD:         oldValue = this.ToFlood(); break;
            default: break; 
        }

        // threshold value checks: we can't have a value for flood sensors which is below its minimum working voltage value
        if ((TYPE == DataType.FLOOD) && (newValue < DataType_Helper.FLOOD_SENSOR_MINACCEPTABLEVALUE))
            newValue = DataType_Helper.FLOOD_SENSOR_MINACCEPTABLEVALUE; 
        
        // same for smoke
        if ((TYPE == DataType.SMOKE) && (newValue < DataType_Helper.SMOKE_SENSOR_MINACCEPTABLEVALUE))
            newValue = DataType_Helper.SMOKE_SENSOR_MINACCEPTABLEVALUE; 
            
        String oldValue_String = Float.toString(oldValue);
        String newValue_String = Float.toString(newValue); 

        //TO DO TEST THIS. there might be an extreme case in which two airQ params have the same float value (e.g. 500), this should update the correct one only
        if (airQualityParam != null)
            this.value = this.value.replace(airQualityParam + ": "+ oldValue_String, airQualityParam + ": "+ newValue_String);
        else 
            this.value = this.value.replace(oldValue_String, newValue_String);
    
        //String s = airQualityParam == null ? "" : airQualityParam; 
        //Log.getInstance().Write("DATA (" + this.TYPE + s + ") : variated from "+oldValue_String + " to "+newValue_String);
        return this.value; 
        
    }

    //a valid temperature String value is "20.0000000 °C"
    public float ToTemperature()
    {
        String uom = this.TYPE.UOM; 
        int i = this.value.indexOf(uom); 
        String v = value.substring(0, i-1); 
        return Float.parseFloat(v); 
    }

    //a valid HUMIDITY value is like "RH: 40%"
    public float ToHumidity()
    {
        int i = this.value.indexOf(" ");
        String uom = this.TYPE.UOM; 
        int j = this.value.indexOf(uom);
        String v = value.substring(i+1, j); 

        return Float.parseFloat(v);
    }

    // a valid AIRQ Data.value is like "CO2: 500 ppm - CO: 5 ppm - PM2.5: 6.5 ppm - TVOC: 70 ppb"
    // might be using other units of measure, like ug / m^3 for TVOC 
    public float ToAirQuality(String param)
    {
        int i = this.value.indexOf(param+": "); 
        // index of "ppm" or "ppb" or whatever the uom for this param is, but starting from index i previously found
        int j = this.value.indexOf(DataType_Helper.GetUOM(this.TYPE, param), i); 

        String v = this.value.substring(i, j);
        
        //now v is e.g "CO2: 1000"
        String[] tokens = v.split(" "); 
        return Float.parseFloat(tokens[1]); 
    }

    //a valid SMOKE value is like "8 % obs/m"
    public float ToSmoke()
    {
        String uom = this.TYPE.UOM; 
        int j = this.value.indexOf(uom);
        String v = value.substring(0, j-1); 

        return Float.parseFloat(v); 
    }


    //a valid MOTION value is like "Motion 0.75f"
    public float ToMotion()
    {
        //String uom = this.TYPE.UOM; 
        int i = this.value.indexOf(" "); 
        int j = this.value.length(); 

        String v = value.substring(i, j); 

        return Float.parseFloat(v); 
    }

    // a valid FLOOD value is like "2.7 V"
    public float ToFlood()
    {
        String uom = this.TYPE.UOM; 
        int j = this.value.indexOf(uom); 
        String v = value.substring(0, j-1);
        
        return Float.parseFloat(v); 
    }
}