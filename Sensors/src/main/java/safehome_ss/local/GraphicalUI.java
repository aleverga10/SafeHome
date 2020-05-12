package safehome_ss.local;

import safehome_ss.gui.GUI_Helper;
import safehome_ss.pubsub.Channel;
import safehome_ss.pubsub.Message;
import safehome_ss.pubsub.Subscriber;

public class GraphicalUI implements Subscriber 
{
    GUI_Helper helper; 

    //Singleton pattern
    private static GraphicalUI instance = null; 
    public static GraphicalUI getInstance()
    {
        if (instance == null)
            instance = new GraphicalUI();

        return instance; 
    }

    private GraphicalUI()
    {
        helper = GUI_Helper.getInstance(); 
    }

    @Override
    public void SubscribeTo(Channel channel) 
    {
        channel.AddSub(this);
    }

    @Override
    public void ReceiveMessage(Channel channel, Message message) 
    {
        // save every data in order for various GUI controllers to get access to 
        switch (message.EVENT_TYPE)
        {
            case MEASURE_AUTOLOOP: 
                // request a measure on a sensor (origin: sensors, openhab binding) (destination: environment)
                break;

            case MEASURE_REQUEST:
                // request a measure on a sensor (origin: sensors, openhab binding) (destination: environment) 
                break; 

            case MEASURE_RESPONSE: 
                // respond to a measure request   (origin: environment) (destination: sensors, openhab after sensibility casting (e.g. 21.36233°C -> 21.4°C))
                break; 

            case MEASURE_DONE: 
                // respond to a measure request   (origin: environment) (destination: sensors, openhab after sensibility casting (e.g. 21.36233°C -> 21.4°C))
                if (helper.controllers_Sensor.get(message.sensorUID) != null)
                    // gui update
                    helper.controllers_Sensor.get(message.sensorUID).update_measuredData(message.response_Data.value); 

                break; 

            case SENSOR_STATUS_UPDATE: 
                if (helper.controllers_Sensor.get(message.sensorUID) != null)
                    // gui update
                    helper.controllers_Sensor.get(message.sensorUID).setField("sensorStatus", message.newSensorStatus.toString());
                break; 
            default: 
                break;
        }
    }

}