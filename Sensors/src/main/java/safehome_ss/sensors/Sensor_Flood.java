package safehome_ss.sensors;

import java.math.BigDecimal;
import java.math.RoundingMode;

import safehome_ss.local.ScenarioController;
import safehome_ss.local.ScenarioController.ScenarioTypes;
import safehome_ss.sensors.Data.DataType;

//measured in obscuration / meter

/**
 * Sensor_Smoke
 */
public class Sensor_Flood extends Sensor
{

    
    public boolean isTriggered; 
    
    // sensitivity of this detectorunit of measure: V (eg. 2.5 V)
    // a measured value that is > than this WILL trigger the detector alarm
    final Float MIN_TRIGGERVALUE = 2.4f; 
    final Float MIN_WORKINGVALUE = 2f; 

    Float measuredValue; 
    final int SENSITIVITY = 1;  

    public Sensor_Flood(String id, String name) 
    {
        super(id, name, DataType.FLOOD);
        ACCURACY = 0.3f; // ~ +- 0.3 V at most with gaussian error
    }

    @Override
    protected void Measure(Data realData) 
    {
        // we can't have flood values which are less than the working minimal number
        BigDecimal bdValue = new BigDecimal(-1); 
        while (bdValue.floatValue() < MIN_WORKINGVALUE)
        {
            // TESTING SCENARIO (S2: measurement error) only 
            Check_MeasurementError(realData.ToFlood());

            bdValue = new BigDecimal(realData.ToFlood() + this.bias);
            // the BigDecimal value is immutable so this assignment HAS to be done otherwise it's not rounding the number properly 
            bdValue = bdValue.setScale(this.SENSITIVITY, RoundingMode.HALF_UP); 
        }


        this.measuredValue = bdValue.floatValue(); 
        this.measuredData.value = this.measuredValue.toString();
    
        // format value as a gui-readable string (ie. "20.2" -> "20.2 °C")
        this.measuredData.value = this.measuredData.FormatValue();  

        


        this.isTriggered = IsTriggered(); 
    }
   
    @Override
    protected boolean IsTriggered() 
    {
        if (this.measuredValue > MIN_TRIGGERVALUE)
            return true; 

        return false;
    }
}