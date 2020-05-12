package safehome_ss.pubsub;

public interface Subscriber 
{
    public void SubscribeTo(Channel channel); 
    public void ReceiveMessage(Channel channel, Message message); 
    
}