package safehome_ss.pubsub;

import safehome_ss.sensors.Data;
import safehome_ss.sensors.Data.DataType;
import safehome_ss.sensors.Sensor.SensorStatus;
import safehome_ss.local.ScenarioController.SensorBiasType;
import safehome_ss.pubsub.Event.EventType;

public class Message 
{
    /*
        classe che rappresenta i messaggi inviati tramite il protocollo pub / sub che avviene tra 
        la classe Event (pub) e tutti coloro che ascoltano eventi (Sensori, Rooms, Log, ...)
    */

    // *** MEASUREMENT RELATED FIELDS ***
    public final EventType EVENT_TYPE;
    public DataType DATA_TYPE; 

    // measure_response type params
    public Data response_Data; 
    public String sensorUID;

    
    // *** SCENARIO RELATED FIELDS ***
    
    // sensor failure
    public float scenario_Failure_Pmax;
    public String scenario_sensorUID; 
    public SensorStatus newSensorStatus; 

    // measurement error 
    public SensorBiasType biasType; 
    public float maxRandomError_Probability;
    public float maxRandomError; 

    // openhab delay
    public long delay; 

    // results update
    public int correctMeasurements, failedMeasurements; 
    public int correctCalls, positiveFalses, negativeFalses; 

    // **** MEASUREMENTS RELATED MESSAGES ****

    // MEASURE_REQUEST, MEASURE_AUTOLOOP
    // e.g (MEASURE_AUTOLOOP, TEMPERATURE) on channel TEMP_01 (sensorchannel)
    public Message(EventType eventType, DataType dataType, String sensorUID)
    {
        this.EVENT_TYPE = eventType; 
        this.DATA_TYPE = dataType; 
        
        this.sensorUID = sensorUID;
    }

    // MEASURE_RESPONSE, MEASURE_DONE, ALARM_ALERT and ALARM_DANGER messages
    public Message(EventType eventType, DataType dataType, String sensorUID, Data responseData)
    {
        this.EVENT_TYPE = eventType; 
        this.DATA_TYPE = dataType; 

        this.sensorUID = sensorUID; 
        this.response_Data = responseData; 
    }

    // **** TESTING SCENARIO RELATED MESSAGES ****

    // sensor failure 
    // (both activation and deactivation of the "sensor failure" scenario use this message but with different param, [ie. if null is deactivation] )
    public Message(float failure_maxProbability)
    {
        this.EVENT_TYPE = EventType.SENSOR_FAILURE; 
        this.scenario_Failure_Pmax = failure_maxProbability;  
    }
    
    // update sensor status
    public Message(String sensorUID, SensorStatus status)
    {
        this.EVENT_TYPE = EventType.SENSOR_STATUS_UPDATE; 
        this.sensorUID = sensorUID; 
        this.newSensorStatus = status; 
    }

    // measurement error
    public Message(SensorBiasType biasType, float probability, float maxError)
    {
        this.EVENT_TYPE = EventType.MEASUREMENT_ERROR;
        this.biasType = biasType; 
        this.maxRandomError_Probability = probability; 
        this.maxRandomError = maxError; 
    }

    // openhab delay
    public Message(long delay)
    {
        this.EVENT_TYPE = EventType.OPENHAB_DELAY; 
        this.delay = delay;
    }   

    // scenarios results update
    public Message(int correctCount, int failuresCount)
    {
        this.EVENT_TYPE = EventType.SCENARIO_RESULTS_UPDATE; 
        this.correctMeasurements = correctCount; 
        this.failedMeasurements = failuresCount; 
    }

    // scenarios results update
    public Message(int correctCalls, int positiveFalses, int negativeFalses)
    {
        this.EVENT_TYPE = EventType.SCENARIO_RESULTS_UPDATE; 
        this.correctCalls = correctCalls; 
        this.positiveFalses = positiveFalses; 
        this.negativeFalses = negativeFalses; 
    }
}
