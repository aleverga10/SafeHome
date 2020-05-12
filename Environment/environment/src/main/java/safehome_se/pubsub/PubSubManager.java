package safehome_se.pubsub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import safehome_se.communication.PubNub_Instance;
import safehome_se.local.GraphicalUI;
import safehome_se.pubsub.Channel.ChannelType;

/**
 * PubSubManager
 */
public class PubSubManager 
{
    //map channels to their names, can act as a list also
    /*
    private Map<String, RoomChannel> roomChannels; 
    private Map<String, SensorChannel> sensorChannels; 
    private Map<String, Channel> otherChannels; 
    */
    private Map<String, Channel> internalPubSub_Channels; 
    
    private List<Subscriber> defaultSubs; 
 
    public final String ROOM_CHANNEL_NAMING_PROTOCOL = "CHANNEL_ROOM_"; 
    public final String SENSOR_CHANNEL_NAMING_PROTOCOL = "CHANNEL_SENSOR_"; 
    public final String ALARM_CHANNEL_NAMING_PROTOCOL = "CHANNEL_ALARM_"; 
    public String channel_Naming_Protocol; 
    //TO DO aggiungi naming protocol per sensor & room  ecc diversi va

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
        
        internalPubSub_Channels = new HashMap<String, Channel>(); 
    }

    public void CreateChannel(String name, ChannelType type)
    {
        switch (type)
        {
            case ROOM:  channel_Naming_Protocol = ROOM_CHANNEL_NAMING_PROTOCOL;  break;
            case SENSOR: channel_Naming_Protocol = SENSOR_CHANNEL_NAMING_PROTOCOL; break;
        }

        String channelName = (this.channel_Naming_Protocol + name).toUpperCase(); 

        internalPubSub_Channels.put(channelName, new Channel(this.defaultSubs, channelName, type)); 
        Log.getInstance().Write("PUBSUBMANAGER : created new "+ type + " channel "+channelName);
    }

    public Channel GetChannel_FromName(String name, ChannelType type)
    {
        switch (type)
        {
            case ROOM:  channel_Naming_Protocol = ROOM_CHANNEL_NAMING_PROTOCOL;  break;
            case SENSOR: channel_Naming_Protocol = SENSOR_CHANNEL_NAMING_PROTOCOL; break;
        }
        
        String channelName = (this.channel_Naming_Protocol + name).toUpperCase(); 

        /*
        if (roomChannels.containsKey(channelName))
            return roomChannels.get(channelName); 
        
        if (sensorChannels.containsKey(channelName))
            return sensorChannels.get(channelName); 

        if (otherChannels.containsKey(channelName))
            return otherChannels.get(channelName); 
          
        */

        if (internalPubSub_Channels.containsKey(channelName))
            return internalPubSub_Channels.get(channelName); 
            
        return null; 
    }


}