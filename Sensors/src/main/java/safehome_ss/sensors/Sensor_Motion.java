package safehome_ss.sensors;

import java.math.BigDecimal;
import java.math.RoundingMode;

import safehome_ss.sensors.Data.DataType;

/**
 * Sensor_Motion
 */
public class Sensor_Motion extends Sensor
 {

    
    public boolean isTriggered; 

    final Float MIN_TRIGGERABLE_MOTION = 0.15f; // reccommended values 20-100 day, 1-20 night so more or less 15% is ok
    
    public Float measuredValue; 
    final int SENSITIVITY = 2; 

    public Sensor_Motion(String id, String name) 
    {
        super(id, name, DataType.MOTION);
        this.ACCURACY = 0.15f; // ~ +-15 % at most with gaussian error
    }

    @Override
    protected void Measure(Data realData) 
    {
        BigDecimal bdValue = new BigDecimal(-1); 
        // can't have negative motion values 
        while (bdValue.floatValue() < 0)
        {
            // TESTING SCENARIO (S2: measurement error) only 
            Check_MeasurementError(realData.ToMotion());
                
            bdValue = new BigDecimal(realData.ToMotion() + this.bias);
            // the BigDecimal value is immutable so this assignment HAS to be done otherwise it's not rounding the number properly 
            bdValue = bdValue.setScale(this.SENSITIVITY, RoundingMode.HALF_UP); 
        }

        this.measuredValue = bdValue.floatValue(); 
        this.measuredData.value = this.measuredValue.toString();

        // format value as a gui-readable string (ie. "20.2" -> "20.2 °C")
        this.measuredData.value = this.measuredData.FormatValue(); 
        
        //System.out.println(" MOTION real "+ realData.value + " measured "+this.measuredMotion + " after sens. "+this.measuredData.value);

        this.isTriggered = IsTriggered(); 
    }

    @Override
    protected boolean IsTriggered() 
    {
        if (this.measuredValue > MIN_TRIGGERABLE_MOTION)
            return true; 
        return false;
    }
}