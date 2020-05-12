package safehome_ss.pubsub;

public interface Publisher 
{
    public void PublishTo(Channel channel, Message message); 
}