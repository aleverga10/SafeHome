package safehome_se.local;

import java.util.ArrayList;
import java.util.List;

import safehome_se.environment.Data;
import safehome_se.gui.GUI_Helper;
import safehome_se.gui.GUI_RoomController;
import safehome_se.gui.ModelDataLoader;
import safehome_se.pubsub.Channel;
import safehome_se.pubsub.Message;
import safehome_se.pubsub.Subscriber;

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
        // channel names are always like CHANNEL_ROOM_ROOMNAME
        // so we can use this fact to get the desired room 
        String roomName = channel.CHANNEL_NAME.substring("CHANNEL_ROOM_".length()); 
        
        // it may happen that we receive these messages even when the room page is not shown / not loaded 
        // this should avoid it 
        if (helper.controllers_Room.get(roomName) != null)
        {
           switch (message.EVENT_TYPE)
            {
                // internal variation of an environmental data (origin: environment) (destination: environment)
                case VARIATE_DATA: 
                    // a message indicating a thread has done variating a value,
                    // so this class needs to inform someone to update the displayed value now
                   
                    //Data data = new Data(message.DATA_TYPE, message.variation_NewValue); 
                    
                    // find the model data and copy it
                    // since we can't know whether this method will be executed before or after
                    // the model's "update" method, so we possibly will display the old value, 
                    // we need to act on a copy of the mdoel value
                    // and update the copy ourselves
                    // just like the model does.
                    // in this way we can safely say we display the updated value
                    Data data = null; 

                    List<Data> dataList = new ArrayList<Data>(); 
                    // this makes a copy of the model's list and gets the correct-typed data
                    dataList.addAll(ModelDataLoader.getInstance().house.getRoom_fromRoomName(roomName).realData); 
                    for (Data d : dataList) 
                    {
                        if (d.TYPE.equals(message.DATA_TYPE))
                            data = d; 
                    }
                    // update this copy with the new value
                    data.value = data.VariateValue_FromFloat(message.variation_NewValue, message.variation_AirQualityParam); 

                    // tell the right gui room controller to update its table values
                    helper.controllers_Room.get(roomName).setTableValue(message.DATA_TYPE, GUI_RoomController.COLUMN_NAME_VALUE, data.value);
                    break;

                case VARIATE_DATA_REQUEST: 
                    // a message REQUESTING to perform a variation on a real data (e.g. increase temperature) or start a trigger (e.g. motion sensor trigger)
                    // it gets received only ONCE when the variation is requested
                    helper.controllers_Room.get(roomName).setTableValue(message.DATA_TYPE, GUI_RoomController.COLUMN_NAME_VARIATION, message.VARIATION_TYPE.name());
                    helper.controllers_Room.get(roomName).setTableValue(message.DATA_TYPE, GUI_RoomController.COLUMN_NAME_TARGET, message.variation_Target.toString());
                    break;

                case MEASURE_RESPONSE_REQUEST: 
                    // internal seek for appropriate data to publish as a response, 
                    // gui doesn't really care about it, but we can use it to update last request id and time
                    helper.controllers_Room.get(roomName).setField("lastRequest", message.request_sensorUID);
                    helper.controllers_Room.get(roomName).setField("lastRequestTime", message.request_timestamp.toString());  
                    break; 

                // respond to a measure request   (origin: environment) (destination: sensors, openhab after sensibility casting (e.g. 21.36233°C -> 21.4°C))
                case MEASURE_RESPONSE: 
                    // ignore this. 
                    break;

                default: 
                    break;
            }
        }
    }

}