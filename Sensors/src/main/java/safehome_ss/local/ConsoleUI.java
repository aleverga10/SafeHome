package safehome_ss.local;

import safehome_ss.pubsub.Channel;
import safehome_ss.pubsub.Message;
import safehome_ss.pubsub.Subscriber;

public class ConsoleUI implements Subscriber
{
    //Singleton pattern
    private static ConsoleUI instance = null; 
    public static ConsoleUI getInstance()
    {
        if (instance == null)
            instance = new ConsoleUI();

        return instance; 
    }
    

    public void RunUI()
    {
        //TO DO start a separate console, use a small Swing GUI, write the other output in a file or whatever, just separate this UI from the other output

        System.out.println("SafeHome Simulated Environment.");
        System.out.println("Type a command to interact with the Environment. !help for a list of commands.");

        while(true)
        {
            //System.
        }
    }

    @Override
    public void SubscribeTo(Channel channel) 
    {
        channel.AddSub(this);
    }

    @Override
    public void ReceiveMessage(Channel channel, Message message) 
    {
        //just print everything you receive
    }
}