package safehome_ss.communication;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
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

import safehome_ss.pubsub.Channel;
import safehome_ss.pubsub.Event;
import safehome_ss.pubsub.Log;
import safehome_ss.pubsub.Message;
import safehome_ss.pubsub.PubSubManager;
import safehome_ss.pubsub.Subscriber;
import safehome_ss.pubsub.Channel.ChannelType;
import safehome_ss.pubsub.Event.EventType;
import safehome_ss.sensors.Data;

/**
 * 
 */
public class PubNub_Instance implements Subscriber
{    
    private final String SUBSCRIBE_KEY = "sub-c-70e96e80-60a1-11ea-8216-b6c21e45eadc";
    private final String PUBLISH_KEY = "pub-c-35689d5b-52f9-46d2-be8d-8ea50e465fbb";
    
    private final String UUID = "SafeHome_SS";
    private final String PUBNUB_CHANNELNAME_MEASURE_REQUEST = "SH_SE_MREQ"; 
    private final String PUBNUB_CHANNELNAME_MEASURE_RESPONSE = "SH_SE_MRESP";

    private PubNub pubNub;
    private PNConfiguration config;
    private PubNub_Listener listener;

    private MReq_Json mReq_JsonBuilder; 
    private MResp_Json mResp_JsonBuilder; 

    // Singleton pattern
    private static PubNub_Instance instance = null;

    public static PubNub_Instance getInstance() {
        if (instance == null)
            instance = new PubNub_Instance();

        return instance;
    }

    private PubNub_Instance() 
    {
        PubNub_Init();
        Add_Listener();

        mReq_JsonBuilder = new MReq_Json();
        mResp_JsonBuilder = new MResp_Json();  
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
        //channels.add(PUBNUB_CHANNELNAME_MEASURE_REQUEST);
        channels.add(PUBNUB_CHANNELNAME_MEASURE_RESPONSE); 
        this.pubNub.subscribe().channels(channels).execute(); 
    }

    public void Add_Listener()
    {
        listener = new PubNub_Listener();
        this.pubNub.addListener(listener);
    }

    
    private void Publish_PubNubMessage(String toPublish, String pnChannelName)
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
            //System.out.println("Message received from PubNub on channel: "+pnMessageResult.getChannel()); 
            //System.out.println(pnMessageResult.getMessage().toString());

            switch (pnMessageResult.getChannel())
            {
                case PUBNUB_CHANNELNAME_MEASURE_RESPONSE: 
                    //convert the Message into a meaningful object
                    MResp response = mResp_JsonBuilder.unmarshal(pnMessageResult.getMessage().getAsString()); 
                    
                    // tell the measurer that sensor needs to update its internal value 
                    // (and possibly perform some operations, e.g. sensitivity check, on the real data, before storing it)
                    Data realData = new Data(response.dataType, response.data); 
                    
                    try 
                    {   
                        Event event = new Event(EventType.MEASURE_RESPONSE); 
                        Message message = new Message(EventType.MEASURE_RESPONSE, realData.TYPE, response.sensorUID, realData); 
                        Channel channel = PubSubManager.getInstance().GetChannel_FromName(response.sensorUID, ChannelType.SENSOR); 
                        event.PublishTo(channel, message);
                    }
                    catch (NullPointerException ex)
                    { Log.getInstance().Write("ERROR (PUBNUB / MEASURER): received sensor uid is null"); }

                    break;
                default: 
                    // we should not receive any message on any other channel (we're not subscribed to, it shouldn't be possible)
                    Log.getInstance().Write("ERROR (PUBNUB): Received a message on the" + pnMessageResult.getChannel() +" channel."); 
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
        //Log.getInstance().Write("INTERNAL PUBSUB (PUBNUB_INSTANCE): Message received on "+channel + " of type "+message.EVENT_TYPE);
        switch (message.EVENT_TYPE) 
        {
            //case MEASURE_DONE: 
            //Publish_PubNubMessage(FormatMessage(message), PUBNUB_CHANNELNAME_MEASURE_DONE); break;

            case MEASURE_REQUEST: 
            Publish_PubNubMessage(FormatMessage(message), PUBNUB_CHANNELNAME_MEASURE_REQUEST); break;

            case MEASURE_AUTOLOOP: 
            Publish_PubNubMessage(FormatMessage(message), PUBNUB_CHANNELNAME_MEASURE_REQUEST); break;

            default: break; 
        }
    }

    private String FormatMessage(Message message)
    {
        // **** custom serializer approach ****
       
        // formatting for a "Measure Request" type message, to the simulated environment
        if (message.response_Data == null) 
        {
            MReq origin = new MReq(
                Date.from(Instant.now()), 
                message.sensorUID, 
                message.DATA_TYPE.toString(), 
                message.EVENT_TYPE.toString()
            ); 
            return mReq_JsonBuilder.marshal(origin); 
        }
        return null; 
               
    }

}