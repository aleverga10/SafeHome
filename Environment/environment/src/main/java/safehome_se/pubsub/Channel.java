package safehome_se.pubsub;

import java.util.ArrayList;
import java.util.List;

public /*abstract*/ class Channel
{
    public final String CHANNEL_NAME;
    protected List<Subscriber> subs; 

    public enum ChannelType {ROOM, SENSOR}; 
    public final ChannelType TYPE; 

    public Channel(List<Subscriber> defaultSubs, String name, ChannelType type)
    {
        this.CHANNEL_NAME = name; 
        this.TYPE = type; 

        this.subs = new ArrayList<Subscriber>(); 
        this.subs.addAll(defaultSubs);
    }

    public void AddSub(Subscriber sub)
    {
        this.subs.add(sub); 
    }

    public void Send_ToAll(Message message)
    {
        for (Subscriber sub : subs) 
        {
            //System.out.println(this.CHANNEL_NAME + " (CHANNEL) : Sending message " + message.EVENT_TYPE + "to "+sub);
            sub.ReceiveMessage(this, message);
        }
    }

    //e.g. "Log" function will call this, otherwise may not be that useful
    public void Send_ToSpecificSub(Subscriber sub, Message message)
    {
        sub.ReceiveMessage(this, message);
    }
}