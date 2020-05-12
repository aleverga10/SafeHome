package safehome_ss.sensors;

import java.math.BigDecimal;
import java.math.RoundingMode;

import safehome_ss.sensors.Data.DataType;


public class Sensor_Temperature extends Sensor
{
    Float measuredValue;
    final int SENSITIVITY = 1; 

    public Sensor_Temperature(String id, String name) 
    {
        super(id, name, DataType.TEMPERATURE);
        this.ACCURACY = 0.4f; // to have ~ +- 0.4 at most with gaussian error distrib. °C
    }
   

    @Override
    protected void Measure(Data realData) 
    {
        // TESTING SCENARIO (S2: measurement error) only 
        Check_MeasurementError(realData.ToTemperature());

        // apply error to measure
        BigDecimal bdValue = new BigDecimal(realData.ToTemperature() + this.bias);
        // the BigDecimal value is immutable so this assignment HAS to be done otherwise it's not rounding the number properly 
        bdValue = bdValue.setScale(this.SENSITIVITY, RoundingMode.HALF_UP); 
        this.measuredValue = Float.valueOf(bdValue.floatValue()); 
        this.measuredData.value = this.measuredValue.toString(); 
    
        // format value as a gui-readable string (ie. "20.2" -> "20.2 °C")
        this.measuredData.value = this.measuredData.FormatValue(); 

        // check false positives, negatives etc.  with thresholds 
        // only for testing purposes, checks are performed in openhab
        //scenarioME_checkResults(realData, bdValue);
    }

    @Override
    protected boolean IsTriggered() 
    {
        return true; 
    }

    /*
    private void scenarioME_checkResults(Data realData, BigDecimal bdValue)
    {
        // this checks "the alarm should have been called"
        float realValue = realData.ToTemperature();
        float value = bdValue.floatValue(); // for some unknown reasons that i suspect are related to BigDecimals i have to use this instead of getting this.measuredValue which is assigned to the same value (why is it null though ?)

        if ((realValue < MIN_THRESHOLD) || (realValue > MAX_THRESHOLD))
        {     
            // this checks "alarm is actually called" ( = correct call)
            if ((value < MIN_THRESHOLD) || (value > MAX_THRESHOLD))
                correctCalls++;                

            // this checks "should be called but it is not because of error bias" (negative false)
            else
                negativeFalses++; 
        }
        else 
        {
            // this checks "should NOT be called but it is because of error bias" (positive false)
            if ((value < MIN_THRESHOLD) || (value > MAX_THRESHOLD))
                positiveFalses++; 
        }
    }
    */
}