package safehome_ss.sensors;

import java.math.BigDecimal;
import java.math.RoundingMode;

import safehome_ss.sensors.Data.DataType;

/**
 * Sensor_Humidity
 */
public class Sensor_Humidity extends Sensor 
{ 
    final int SENSITIVITY = 1; 

    public Sensor_Humidity(String id, String name) 
    {
        super(id, name, DataType.HUMIDITY);
        ACCURACY = 4f; // +- 4% at most with gaussian error 
    }

    @Override
    protected void Measure(Data realData) 
    {
        // TESTING SCENARIO (S2: measurement error) only 
        Check_MeasurementError(realData.ToHumidity());

        BigDecimal bdValue = new BigDecimal(realData.ToHumidity() + this.bias);
        // the BigDecimal value is immutable so this assignment HAS to be done otherwise it's not rounding the number properly 
        bdValue = bdValue.setScale(this.SENSITIVITY, RoundingMode.HALF_UP); 
        this.measuredValue = bdValue.floatValue(); 
        this.measuredData.value = this.measuredValue.toString(); 

        // format value as a gui-readable string (ie. "20.2" -> "20.2 °C")
        this.measuredData.value = this.measuredData.FormatValue(); 
    }

    @Override
    protected boolean IsTriggered() 
    {
        return true; 
    }

}