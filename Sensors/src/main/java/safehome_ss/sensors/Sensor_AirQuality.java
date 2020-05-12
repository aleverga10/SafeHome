package safehome_ss.sensors;

import java.math.BigDecimal;
import java.math.RoundingMode;

import safehome_ss.local.ScenarioController;
import safehome_ss.local.ScenarioController.ScenarioTypes;
import safehome_ss.pubsub.Log;
import safehome_ss.sensors.Data.DataType;

/**
 * Sensor_AirQuality!
 */ 



 /* 
  *  CO2 ranges (ppm, TWA): 
 * 250 - 400 -> normal; outdoor ambient air
 * 400 - 1000 -> normal; occupied indoor space with good air exchange (National Institute for Occupation Safety and Health (NIOSH))
 * 1000 - 2000 -> poor; drowsiness 
 * 2000 - 5000 -> stale air; headaches, sleepiness, poor concentration, loss of attention (rare: increased heart rate and slight nausea may also be present)
 * 5000 -> TLV, MAX LIMIT; workplace exposure limit (as 8-hour TWA) in most jurisdictions (ACGIH)
 * 40000 -> serious oxigen deprivation; permanent brain damage
 * 
 * CO2 (ppm, STEL): 
 * 30000 -> TLV, MAX LIMIT
 * 
 * 
 * FROM kane.co.uk/knowledge-centre/what-are-safe-levels-of-co-and-co2-in-rooms
 * 
 * CO ranges (ppm): 
 * 9 -> CO Max prolonged exposure (ASHRAE standard)
 * 35 -> TLV, CO Max exposure for 8 hour work day (OSHA) 
 * 800 -> CO Death within 2 to 3 hours
 *
 * According to the American Conference of Governmental Industrial Hygienists (ACGIH), the time-weighted average (TWA) limit for carbon monoxide (630–08–0) is 25 ppm. 
 * FROM Wikipedia 
 * 
 *  PM2.5 ranges ( ug / m^3, 24-hour data)
 *  25 : TLV, limit
 *  
 *  ( ug / m^3, 24-hour average )
 *  0 - 12 -> Good  (also 1-year average limit "TLV")
 *  12.1 - 35.4 -> Moderate (24-hour average limit "TLV")
 *  35.5 - 55.4 -> Unhealthy for certain groups
 *  55.5 - 150.4 -> Unhealthy
 *  150.5 - 250.4 -> Very Unhealthy
 *  250.5 + -> Hazardous
 * 
 * 
 * Recommended TVOC levels of IAQ that are considered acceptable range: 0.6 - 1 mg/m^3 
 * Conversion from mg/m3 to ppm for most common TVOC is by the factor ~0.5; eg. 1mg/m3 ~= 0.5ppm = 500ppb
 * 
 * The German Health Department defnes TVOC levels as TVOC ranges (ppb) (1ppm = 1000ppb)
 *  0 - 65 -> excellent
 *  65 - 220 -> good    ; 
 *  220 - 660 -> moderate (airing recommended)
 *  660 - 2200 -> poor (airing necessary)
 *  2200+ -> unhealthy (intense airing necessary)
 * 
 * TVOC ranges (mg / m^3) 
 * 0.3 -> Very Good (Level 1) Clean Hygienic Air (target value)  Very Good
   0.3 - 1.0 -> Good (Level 2) Good Air Quality (if no threshold value is exceeded) 
   1.0 - 3.0 -> Medium (Level 3) Noticeable Comfort Concerns (not recommended for exposure > 12 months)
   3.0 - 10.0 -> Poor  (Level 4) Significant Comfort Issues (not recommended for exposure > 1 month)
   10.0+ -> Bad (Level 5) Unacceptable Conditions(not recommended)

   FROM idt.com/eu/en/document/dst/zmod4410-datasheet
 */
public class Sensor_AirQuality extends Sensor
{

    // a valid AIRQ Data.value is like "CO2: 500 ppm - CO: 5 ppm - PM2.5: 6.5 ppm - TVOC: 7000 ppb"
    Float measuredCO2; 
    Float measuredCO; 
    Float measuredPM25; 
    Float measuredTVOC; 

    final Float ACCURACY_CO2 = 30f; // ~ +- 30 ppm at most with gaussian error
    final Float ACCURACY_CO = 15f; // ~ +- 15 ppm at most with gaussian error
    final Float ACCURACY_PM25 = 6.8f; // ~ +- 6.8 ppm at most with gaussian error
    final Float ACCURACY_TVOC = 200f; // ~ +-10% (200 ppm) at most with gaussian error

    final int SENSITIVITY_CO2 = 0;
    final int SENSITIVITY_CO = 1;
    final int SENSITIVITY_PM25 = 1;
    final int SENSITIVITY_TVOC = 1;

    public Sensor_AirQuality(String id, String name)
    {
        super(id, name, DataType.AIR_QUALITY);
    }

    @Override
    protected void Measure(Data realData) 
    {
        this.measuredCO2 = MeasureParam(realData, "CO2");
        this.measuredCO =  MeasureParam(realData, "CO");
        this.measuredPM25 = MeasureParam(realData, "PM2.5");
        this.measuredTVOC = MeasureParam(realData, "TVOC"); 

        this.measuredData.value = this.measuredData.FormatValue(this.measuredCO2.toString(), "CO2"); 
        this.measuredData.value = this.measuredData.FormatValue(this.measuredCO.toString(), "CO"); 
        this.measuredData.value = this.measuredData.FormatValue(this.measuredPM25.toString(), "PM2.5"); 
        this.measuredData.value = this.measuredData.FormatValue(this.measuredTVOC.toString(), "TVOC"); 
    }

    @Override
    protected boolean IsTriggered() 
    {
        return true; 
    }

    private Float MeasureParam(Data realData, String param)
    {   
        int sensitivity = 0;   
        // gets the correct sensitivity for this param (ie. number of decimals; 1 -> 1000.5)
        switch (param)
        {
            case "CO2":     sensitivity = SENSITIVITY_CO2;  break;
            case "CO":      sensitivity = SENSITIVITY_CO;   break;
            case "PM2.5":   sensitivity = SENSITIVITY_PM25; break;
            case "TVOC":    sensitivity = SENSITIVITY_TVOC; break;
        }

        Check_MeasurementError(param, realData.ToAirQuality(param));

        BigDecimal bdValue = new BigDecimal(realData.ToAirQuality(param) + this.bias);
        bdValue = bdValue.setScale(sensitivity, RoundingMode.HALF_UP); 

        return bdValue.floatValue();
    }

    private void Check_MeasurementError(String param, Float currentValue) 
    {
        // TESTING SCENARIO (error measurement) checks here
        if (ScenarioController.getInstance().scenario == ScenarioTypes.MEASUREMENT_ERROR)
        {
            // we want to introduce a measurement error being gaussian or random
            switch (this.biasType)
            {
                case GAUSSIAN:  
                    // gets the correct param's accuracy
                    Float accuracy = 0f;  
                    switch (param)
                    {
                        case "CO2":     accuracy = ACCURACY_CO2;  break;
                        case "CO":      accuracy = ACCURACY_CO;   break;
                        case "PM2.5":   accuracy = ACCURACY_PM25; break;
                        case "TVOC":    accuracy = ACCURACY_TVOC; break;
                    }

                    this.bias = RNG.nextGaussian() * accuracy / 3;  // yields [-ACC, +ACC] 99.5% of the times (3 because 99.5% is 3 * sigma);
                    break;

                case RANDOM:    
                    
                    // init in case of no error happening during this measurement
                    this.bias = 0; 

                    // check p of a random error happening
                    float n = RNG.nextFloat(); // [0..1) uniformly distributed

                    if (n < bias_randomTyped_probability)
                    {
                        int sign = RNG.nextBoolean() == true ? +1 : -1; //yields -1 or +1
                        this.bias = sign * (currentValue * bias_randomMaxValue); // yields [-1, +1] * MAX % of current value
                        randomErrors++;
                        Log.getInstance().Write("******************** RANDOM ERROR for "+this.UID +""+param+ " ON "+ currentValue +" BIAS "+ bias+ " **************** ");

                    }

                    break;

                default: break;
            }
            
        }
        else // normal measurement are "perfect", aside from rounding
            this.bias = 0;
    }
}