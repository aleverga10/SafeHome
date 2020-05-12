package safehome_se.pubsub;

public interface Publisher 
{
    public void PublishTo(Channel channel, Message message); 
}