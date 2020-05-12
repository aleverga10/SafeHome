package safehome_se.pubsub;

import java.util.Date;

import safehome_se.environment.Data;
import safehome_se.environment.Data.DataType;
import safehome_se.pubsub.Event.EventType;

public class Message 
{
    /*
        classe che rsafehome_simulatedenvironmentresenta i messaggi inviati tramite il protocollo pub / sub che avviene tra 
        la classe Event (pub) e tutti coloro che ascoltano eventi (Sensori, Rooms, Log, ...)
        questa classe contiene almeno 3 info chiave (Room, Tipo di Evento, Tipo di Dato)
        es Room3, +, Temperature // Room2, Trigger, Motion ??? 
        (magari diamogli un minimo di tolleranza, che deve stare nella classe del sensore)

        inoltre in Room fare distinzione tra stato reale e stato misurato
        es. Temperatura reale: 21.2°C, temperatura misurata 21°C o cose del genere comunque 
    */

    // questo è tipo VariationType
    public enum VariationType 
    {   INCREASE, INCREASE_FASTER, INCREASE_FASTEST, 
        DECREASE, DECREASE_FASTER, DECREASE_FASTEST, 
        TRIGGER; }; 
        
    public final EventType EVENT_TYPE;
    public final DataType DATA_TYPE; 

    // variate_data type message params
    public VariationType VARIATION_TYPE; 
    public Float variation_NewValue; 
    public String variation_AirQualityParam; 
    public Float variation_Target;
    public Float variation_motionDuration; 

    // measure_response type params
    public Data response_Data; 
    public String response_SensorUID;

    // measure_request_response type params
    public String request_sensorUID;
    public Date request_timestamp;

    // MEASURE_REQUEST_RESPONSE
    // e.g (MEASURE_AUTOLOOP, TEMPERATURE) on channel TEMP_01 (sensorchannel)
    public Message(EventType eventType, DataType dataType, String sensorUID, Date timestamp)
    {
        this.EVENT_TYPE = eventType; 
        this.DATA_TYPE = dataType; 
        
        this.request_sensorUID = sensorUID;
        this.request_timestamp = timestamp; 
    }

    // VARIATE_DATA_REQUEST 
    //e.g. (VARIATE_DATA_REQUEST, TEMPERATURE, INCREASE, 22) on channel ROOM_02 (roomchannel)
    public Message(EventType eventType, DataType dataType, VariationType variationType, Float variation_Target)
    {
        this.EVENT_TYPE = eventType; 
        this.DATA_TYPE = dataType;
        this.variation_AirQualityParam = null;
        this.VARIATION_TYPE = variationType; 
        this.variation_Target = variation_Target; 
    }

    // VARIATE_DATA_REQUEST for motion
    //e.g. ( VARIATE_DATA_REQUEST , MOTION,      TRIGGER,  0.94, 5) on channel ^
    public Message(EventType eventType, DataType dataType, VariationType variationType, Float variation_Target, Float variation_motionDuration)
    {
        this.EVENT_TYPE = eventType; 
        this.DATA_TYPE = DataType.MOTION;
        this.variation_AirQualityParam = null;
        this.VARIATION_TYPE = VariationType.TRIGGER; 
        this.variation_Target = variation_Target; 
        this.variation_motionDuration = variation_motionDuration; 
    }

    
    // VARIATE_DATA_REQUEST for airQuality
    //e.g. (VARIATE_DATA_REQUEST, AIR_QUALITY, CO2, INCREASE, ...) on channel ROOM_02 (roomchannel)
    public Message(EventType eventType, DataType dataType, String airQParam, VariationType variationType, Float variation_Target)
    {
        this.EVENT_TYPE = eventType; 
        this.DATA_TYPE = DataType.AIR_QUALITY;
        this.variation_AirQualityParam = airQParam; 
        this.VARIATION_TYPE = variationType; 
        this.variation_Target = variation_Target; 
    }

    //VARIATE_DATA
    //e.g. (TEMPERATURE, 20.15, null, ...) || (AIRQUALITY, ..., CO2, ...)
    public Message(DataType dataType, Float variation_NewValue, String variation_AirQualityParam)
    {
        this.EVENT_TYPE = EventType.VARIATE_DATA; 
        this.DATA_TYPE = dataType;

        this.variation_NewValue = variation_NewValue; 
        this.variation_AirQualityParam = variation_AirQualityParam; 
    }

    //MEASURE_RESPONSE, ALARM_ALERT and ALARM_DANGER messages
    public Message(EventType eventType, DataType dataType, String sensorUID, Data measuredData)
    {
        this.EVENT_TYPE = eventType; 
        this.DATA_TYPE = dataType; 

        this.response_SensorUID = sensorUID; 
        this.response_Data = measuredData; 
    }
    

    public boolean IsCoherent()
    {
        //TO DO check this.eventType is coherent with this.dataType 
        //e.g TRIGGER only valid for Motion sensors; you cant have an INCREASE event for Motion data type etc. 

        return false; 
    }
}
