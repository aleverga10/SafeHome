package safehome_ss.pubsub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import safehome_ss.communication.OpenHAB_Caller;
import safehome_ss.communication.PubNub_Instance;
import safehome_ss.local.GraphicalUI;
import safehome_ss.pubsub.Channel.ChannelType;

/**
 * PubSubManager
 */
public class PubSubManager 
{
    private Map<String, Channel> internalPubSub_Channels; 
    
    private List<Subscriber> defaultSubs; 
 
    public final String SENSOR_CHANNEL_NAMING_PROTOCOL = "CHANNEL_SENSOR_"; 
    public String channel_Naming_Protocol; 
   
    //Singleton pattern
    private static PubSubManager instance = null; 
    public static PubSubManager getInstance()
    {
        if (instance == null)
            instance = new PubSubManager();

        return instance; 
    }

    private PubSubManager()
    {
        defaultSubs = new ArrayList<Subscriber>(); 
        // defaultSubs.add(ConsoleUI.getInstance()); 
        defaultSubs.add(GraphicalUI.getInstance()); 
        defaultSubs.add(Log.getInstance());
        defaultSubs.add(PubNub_Instance.getInstance());  
        defaultSubs.add(OpenHAB_Caller.getInstance()); 
   
        internalPubSub_Channels = new HashMap<String, Channel>(); 
    }

    public void CreateChannel(String name, ChannelType type)
    {
        switch (type)
        {
            //case ROOM:  channel_Naming_Protocol = ROOM_CHANNEL_NAMING_PROTOCOL;  break;
            case SENSOR: channel_Naming_Protocol = SENSOR_CHANNEL_NAMING_PROTOCOL; break;
        }

        String channelName = (this.channel_Naming_Protocol + name).toUpperCase(); 

        internalPubSub_Channels.put(channelName, new Channel(this.defaultSubs, channelName, type)); 

    }

    public Channel GetChannel_FromName(String name, ChannelType type)
    {
        switch (type)
        {
            //case ROOM:  channel_Naming_Protocol = ROOM_CHANNEL_NAMING_PROTOCOL;  break;
            case SENSOR: channel_Naming_Protocol = SENSOR_CHANNEL_NAMING_PROTOCOL; break;
        }
        
        String channelName = (this.channel_Naming_Protocol + name).toUpperCase(); 

        if (internalPubSub_Channels.containsKey(channelName))
            return internalPubSub_Channels.get(channelName); 
            
        return null; 
    }

    public void Broadcast(Event event, Message message)
    {
        // publish this message to all channels
        // this gets used in scenario 1 where we want all sensors to know they need to perform "the failure check"
        for (Entry<String, Channel> entry: internalPubSub_Channels.entrySet())
        {
            event.PublishTo(entry.getValue(), message);
        }
        
    }

}