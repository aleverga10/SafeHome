package safehome_se.communication;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pubnub.api.*;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.api.models.consumer.pubsub.PNSignalResult;
import com.pubnub.api.models.consumer.pubsub.message_actions.PNMessageActionResult;
import com.pubnub.api.models.consumer.pubsub.objects.PNMembershipResult;
import com.pubnub.api.models.consumer.pubsub.objects.PNSpaceResult;
import com.pubnub.api.models.consumer.pubsub.objects.PNUserResult;

import safehome_se.environment.Data.DataType;
import safehome_se.gui.ModelDataLoader;
import safehome_se.pubsub.Channel;
import safehome_se.pubsub.Log;
import safehome_se.pubsub.Message;
import safehome_se.pubsub.PubSubManager;
import safehome_se.pubsub.Subscriber;
import safehome_se.pubsub.Channel.ChannelType;
import safehome_se.pubsub.Event.EventType;
import safehome_se.pubsub.Event;

/**
 * 
 */
public class PubNub_Instance implements Subscriber
{    
    private final String SUBSCRIBE_KEY = "sub-c-70e96e80-60a1-11ea-8216-b6c21e45eadc";
    private final String PUBLISH_KEY = "pub-c-35689d5b-52f9-46d2-be8d-8ea50e465fbb";
    
    private final String UUID = "SafeHome_SE";
    private final String PUBNUB_CHANNELNAME_MEASURE_REQUEST = "SH_SE_MREQ"; 
    private final String PUBNUB_CHANNELNAME_MEASURE_RESPONSE = "SH_SE_MRESP"; 

    private PubNub pubNub;
    private PNConfiguration config;
    private PubNub_Listener listener;

    private MResp_Json mResp_JsonBuilder; 
    private MReq_Json mReq_JsonBuilder; 

    // Singleton pattern
    private static PubNub_Instance instance = null;

    public static PubNub_Instance getInstance() 
    {
        if (instance == null)
            instance = new PubNub_Instance();

        return instance;
    }

    private PubNub_Instance() 
    {
        PubNub_Init();
        Add_Listener(listener);

        mResp_JsonBuilder = new MResp_Json(); 
        mReq_JsonBuilder = new MReq_Json();
    }

    private void PubNub_Init()
    {
        config = new PNConfiguration();
        config.setSubscribeKey(SUBSCRIBE_KEY);
        config.setPublishKey(PUBLISH_KEY);
        config.setSecure(false);
        config.setUuid(UUID);
        pubNub = new PubNub(config);

        List<String> channels = new ArrayList<String>(); 
        //we don't need to sub on a channel to publish on it
        //channels.add(PUBNUB_CHANNELNAME_MEASURE_RESPONSE);
        channels.add(PUBNUB_CHANNELNAME_MEASURE_REQUEST); 
        this.pubNub.subscribe().channels(channels).execute(); 
    }

    public void Add_Listener(PubNub_Listener listener)
    {
        listener = new PubNub_Listener();
        this.pubNub.addListener(listener);
    }

    
    public void Publish_PubNubMessage(String toPublish, String pnChannelName)
    {
        //i really really hate anon classes, but i could not work this out because of the <T> param here
        pubNub.publish().message(toPublish).channel(pnChannelName).async( new PNCallback<PNPublishResult>()
        {
            @Override
            public void onResponse(PNPublishResult result, PNStatus status) 
            {
               // handle publish result, status always present, result if successful
                // status.isError() to see if error happened
                if(!status.isError()) 
                {
                    //System.out.println("pub timetoken: " + result.getTimetoken());
                }
                else 
                {
                    Log.getInstance().Write("pub error data: "+status.getErrorData());
                }

                //System.out.println("pub status code: " + status.getStatusCode());
                
            }
        });
            
            
        
    }


    private class PubNub_Listener extends SubscribeCallback 
    {

        @Override
        public void status(PubNub pubnub, PNStatus pnStatus) 
        {
        }

        @Override
        public void message(PubNub pubnub, PNMessageResult pnMessageResult) 
        {
            switch (pnMessageResult.getChannel())
            {
                
                case PUBNUB_CHANNELNAME_MEASURE_REQUEST: 
                    //convert the Message into a meaningful object
                    MReq request = mReq_JsonBuilder.unmarshal(pnMessageResult.getMessage().getAsString()); 
                    Log.getInstance().Write("PUBNUB (REQUEST): Received a request from Sensor = "+ request.sensorUID + ", reqType = " + request.reason);
                    
                    // forward the request to the system to retrieve the data
                    // we will receive an internal message with the data very soon (which is typed MEASURE_RESPONSE)
                    // with all the requested data, which we will publish
                    Event event = new Event(EventType.MEASURE_RESPONSE_REQUEST); 
                    Message message = new Message(EventType.MEASURE_RESPONSE_REQUEST, DataType.valueOf(request.dataType.toUpperCase()), request.sensorUID, request.timestamp); 
                    String roomName = ModelDataLoader.getInstance().house.uidToRooms.get(request.sensorUID); 
                    Channel channel = PubSubManager.getInstance().GetChannel_FromName(roomName, ChannelType.ROOM); 
                    event.PublishTo(channel, message);
                    break;
                default:
                    // we should not receive messages on any other channel (we're not subscribed to, so it should not be possible anyway..)
                    Log.getInstance().Write("ERROR (PUBNUB): Received a message on the"+ pnMessageResult.getChannel() + " channel."); 
                    break;
            }
        }

        @Override
        public void presence(PubNub pubnub, PNPresenceEventResult pnPresenceEventResult) 
        {
        }

        @Override
        public void signal(PubNub pubnub, PNSignalResult pnSignalResult)
        {
        }

        @Override
        public void user(PubNub pubnub, PNUserResult pnUserResult)
        {
        }

        @Override
        public void space(PubNub pubnub, PNSpaceResult pnSpaceResult) 
        {
        }

        @Override
        public void membership(PubNub pubnub, PNMembershipResult pnMembershipResult) 
        {
        }

        @Override
        public void messageAction(PubNub pubnub, PNMessageActionResult pnActionResult) 
        {
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
        switch (message.EVENT_TYPE) 
        {
            case MEASURE_RESPONSE: 
            Publish_PubNubMessage(FormatMessage(message), PUBNUB_CHANNELNAME_MEASURE_RESPONSE); break;

            default: break; 
        }
    }

    private String FormatMessage(Message message)
    {
        // **** custom serializer approach ****
       
        //Response
        if (message.response_Data != null) 
        {
            MResp origin = new MResp(Date.from(Instant.now()), message.response_SensorUID, message.DATA_TYPE.toString(), message.response_Data.value, message.EVENT_TYPE.toString()); 
            return mResp_JsonBuilder.marshal(origin); 
        }

        //Request
        else
        {
            return null;
        }
       
    }
}