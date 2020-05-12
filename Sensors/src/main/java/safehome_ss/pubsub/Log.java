package safehome_ss.pubsub;

import java.time.Instant;
import java.util.Date;

import safehome_ss.gui.GUI_Helper;

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
            case MEASURE_AUTOLOOP:  /* Write("Measuring " +message.DATA_TYPE + "."); */ break; 
            case MEASURE_RESPONSE: Write("Sensor "+ message.sensorUID + " (" + message.DATA_TYPE + ") received realData = "+ message.response_Data.value); break;
            case MEASURE_DONE: Write("Sensor "+ message.sensorUID + " (" + message.DATA_TYPE + ") measured = "+ message.response_Data.value); break;
            case SENSOR_FAILURE: break; // since this is implemented as a broadcast message this will get written many many times; avoid logging from here;
            case MEASUREMENT_ERROR: break; // since this is implemented as a broadcast message this will get written many many times; avoid logging from here;
            case SENSOR_STATUS_UPDATE: Write(("SENSOR "+ message.sensorUID + " IS NOW "+ message.newSensorStatus + " ************************************************ ")); break;
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