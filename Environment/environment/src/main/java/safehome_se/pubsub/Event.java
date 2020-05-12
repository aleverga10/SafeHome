package safehome_se.pubsub;

public class Event implements Publisher
{

    public enum EventType { 
        // respond to a measure request   (origin: environment) (destination: sensors, TO DO openhab after sensibility casting (e.g. 21.36233°C -> 21.4°C))
        // and request a measure response (ie. internal seek for appropriate data to publish as a response)
        MEASURE_RESPONSE, MEASURE_RESPONSE_REQUEST,

        // internal variation of an environmental data (origin: environment) (destination: environment)
        VARIATE_DATA_REQUEST, VARIATE_DATA 
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