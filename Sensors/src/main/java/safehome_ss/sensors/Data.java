package safehome_ss.sensors;

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
        /*PRESENCE,*/ 
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
    // private final String DECIMAL_FORMAT_STRING = "#0.000000"; 

    public Data(DataType type)
    {
        this.TYPE = type; 
        this.value = this.TYPE.format; 
    }

    public Data(DataType type, String initialValue)
    {
        this.TYPE = type; 
        this.value = initialValue; 
    }

    public Data(String type, String value)
    {
        this.TYPE = Enum.valueOf(DataType.class, type.toUpperCase()); 
        this.value = value; 
    }

    public String FormatValue()
    {
        return FormatValue(this.value, this.TYPE, null, this.TYPE.format); 
    }

    public String FormatValue(String value, String airqParam)
    {
        return FormatValue(value, DataType.AIR_QUALITY, airqParam, this.value); 
    }

    private String FormatValue(String value, DataType type, String airqParam, String format)
    {
        String s = format;

        // all but an AIRQ data have one value and one unit of measurement
        // and their value formats are like "* @" 
        if (type != DataType.AIR_QUALITY)
        {
            s = s.replaceFirst("\\*", value);
            s = s.replaceFirst("@", type.UOM);
        }

        else 
        {
            // a valid AIRQ Data.value is like "CO2: * @ - CO: * @ - PM2.5: * @ - TVOC: * @"
            int i = s.indexOf(airqParam+":");
            int j = s.indexOf(" - ", i);
            if (j == -1)
                j = s.length(); 
            String oldSubstr = s.substring(i, j);
            
            String newSubstr;
            String[] tokens; 

            // first execution is slightly different because tokens cannot have "*" characters in them....
            if (oldSubstr.contains("*"))
            {   newSubstr = oldSubstr.replaceFirst("\\*", value); 
                newSubstr = newSubstr.replaceFirst("@", DataType_Helper.GetUOM(DataType.AIR_QUALITY, airqParam)); 
            }
            else
            {
                tokens = oldSubstr.split(" "); 
                newSubstr = oldSubstr.replaceFirst(tokens[1], value);
                newSubstr = newSubstr.replaceFirst(tokens[2], DataType_Helper.GetUOM(DataType.AIR_QUALITY, airqParam));
            }
           
            s = s.replace(oldSubstr, newSubstr); 

        }

        return s; 
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

    //a valid SMOKE value is like "8% obs/m"
    public float ToSmoke()
    {
        String uom = this.TYPE.UOM; 
        int j = this.value.indexOf(uom);
        String v = value.substring(0, j-1); 

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
    
    //a valid MOTION value is like "Motion 0.75f"
    public float ToMotion()
    {
        // String uom = this.TYPE.UOM; 
        int i = this.value.indexOf(" "); 
        int j = this.value.length(); 

        String v = value.substring(i, j); 

        return Float.parseFloat(v); 
    }
}