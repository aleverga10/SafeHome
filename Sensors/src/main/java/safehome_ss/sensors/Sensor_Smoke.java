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
public class Sensor_Smoke extends Sensor
{

    
    public boolean isTriggered; 
    
    //sensitivity of this detector = min quantity that can be measured. unit of measure: % obs / m (eg. 5.0% obs/m)
    //without further data i am assuming this is the threshold value. a measured value that is > than this WILL trigger the detector alarm
    final Float MIN_TRIGGERVALUE = 2.5f; //2.4 obs/m ion-1; 2.9 obs/m ion-2; 

    Float measuredValue; 
    final int SENSITIVITY = 1;  

    public Sensor_Smoke(String id, String name) 
    {
        super(id, name, DataType.SMOKE);
        ACCURACY = 1.2f; // ~ +- 1.2 obs/m at most with gaussian error
    }

    @Override
    protected void Measure(Data realData) 
    {
        // we can't have negative smoke values
        BigDecimal bdValue = new BigDecimal(-1); 
        while (bdValue.floatValue() < 0)
        {
            // TESTING SCENARIO (S2: measurement error) only 
            Check_MeasurementError(realData.ToSmoke());

            bdValue = new BigDecimal(realData.ToSmoke() + this.bias);
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