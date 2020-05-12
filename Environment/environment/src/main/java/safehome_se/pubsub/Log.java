package safehome_se.pubsub;

import java.time.Instant;
import java.util.Date;

import safehome_se.gui.GUI_Helper;

/**
 * Log
 */
public class Log implements Subscriber
{

    String logUntilNow; 

    //Singleton pattern
    private static Log instance = null; 
    public static Log getInstance()
    {
        if (instance == null)
            instance = new Log();

        return instance; 
    }

    private Log()
    {
        logUntilNow = ""; 
    }

    @Override
    public void SubscribeTo(Channel channel) 
    {
        channel.AddSub(this);
    }

    @Override
    public void ReceiveMessage(Channel channel, Message message) 
    {
        //Write("LOG (" + channel.CHANNEL_NAME + ") : " + message.EVENT_TYPE);
        switch (message.EVENT_TYPE)
        {
            case MEASURE_RESPONSE: Write("LOG (" + channel.CHANNEL_NAME + ") : Responding to Sensor "+ message.response_SensorUID + " (" + message.DATA_TYPE + ") with "+ message.response_Data.value); break;
            case VARIATE_DATA_REQUEST: Write("LOG (" + channel.CHANNEL_NAME + ") : Requesting " + message.VARIATION_TYPE + " " + message.DATA_TYPE + " to " + message.variation_Target); break; 
            case VARIATE_DATA: 
                String s = message.variation_AirQualityParam == null ? "" : message.variation_AirQualityParam;
                Write("LOG (" + channel.CHANNEL_NAME + ") : "+message.DATA_TYPE + s + " data variated to " + message.variation_NewValue); 
                break;
            default: break; 
        }
        
    }

    public void Write(String s)
    {
        String line = "[" +Date.from(Instant.now()) + "] " + s + "\n"; 
        logUntilNow += line; 
        
        if (GUI_Helper.getInstance().controller_Log != null) 
            GUI_Helper.getInstance().controller_Log.log(line);
    }

    public void Write_PastLog()
    {
        if (GUI_Helper.getInstance().controller_Log != null) 
            GUI_Helper.getInstance().controller_Log.log(logUntilNow);
    }

    
}