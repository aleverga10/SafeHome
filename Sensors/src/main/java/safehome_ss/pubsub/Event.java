package safehome_ss.pubsub;

public class Event implements Publisher
{
    public enum EventType 
    { 
        // request a measure on a sensor (origin: sensors, openhab binding) (destination: environment)
        MEASURE_AUTOLOOP, MEASURE_REQUEST, 
        // respond to a measure request   (origin: environment) (destination: sensors, openhab after sensibility casting (e.g. 21.36233°C -> 21.4°C))
        MEASURE_RESPONSE, MEASURE_DONE,

        // scenarios related messages
        SENSOR_FAILURE,
        MEASUREMENT_ERROR,
        OPENHAB_DELAY,

        SCENARIO_RESULTS_UPDATE,
        SENSOR_STATUS_UPDATE, 

        // unused
        SENSOR_BATTERY_AUTOLOOP, SENSOR_BATTERY_REQUEST
    }; 
                            
    public final EventType TYPE; 

    public Event(EventType type)
    {
        this.TYPE = type; 
    }

    @Override
    public void PublishTo(Channel channel, Message message) 
    {
        channel.Send_ToAll(message);
    }

}