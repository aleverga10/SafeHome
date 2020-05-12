package safehome_se.pubsub;

public interface Subscriber 
{
    public void SubscribeTo(Channel channel); 
    public void ReceiveMessage(Channel channel, Message message); 
    
}