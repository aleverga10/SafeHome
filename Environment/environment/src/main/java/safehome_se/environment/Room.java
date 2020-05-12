package safehome_se.environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import safehome_se.pubsub.Channel;
import safehome_se.pubsub.Event;
import safehome_se.pubsub.Log;
import safehome_se.pubsub.Message;
import safehome_se.pubsub.PubSubManager;
import safehome_se.pubsub.Subscriber;
import safehome_se.pubsub.Channel.ChannelType;
import safehome_se.pubsub.Event.EventType;
import safehome_se.pubsub.Message.VariationType;
import safehome_se.environment.Data.DataType;
import safehome_se.gui.ModelDataLoader;


public class Room implements Subscriber
{
    public final String NAME; 
    public List<String> NEIGHBORING_ROOMS;  //dovrebbe essere final ma ci sono dei problemi di costruzione penso degli oggetti (devi già sapere come si chiameranno quando crei ognuna delle stanza)

    public enum RoomType {LIVING_ROOM, BATHROOM, CORRIDOR, KITCHEN, BEDROOM}
    public RoomType TYPE;
    
    public List<Data> realData; 
    
    private ScheduledExecutorService executor_Service;
    
    // this is a map of threads started by the executor_Service. 
    // the Future<?> type inside it is something i dont fully understand but it is clearly related to threads and promises and so.
    // it is used to stop a specific thread from executing, ie. when a data in a room is being variated (e.g TEMP increase) and it reached the target value
    // the thread must be stopped, but only this one. If there were other variations in the same time (e.g. HUMIDITY increase) in the same room, they would still need to run
    public Map<DataType, DataVariation_Params> variations_InProgress; 
    
    // this is a thread "tic"
    private float interval_Between_Variations_Seconds;     

    public Room(String name)
    {
        this.NAME = name; 
        executor_Service = Executors.newScheduledThreadPool(Integer.MAX_VALUE);
        variations_InProgress = new HashMap<DataType, DataVariation_Params>();
        realData = new ArrayList<Data>();  
    }

    public Room(String name, RoomType type)
    {
        this.NAME = name; 
        this.TYPE = type; 
        this.NEIGHBORING_ROOMS = new ArrayList<String>(); 

        executor_Service = Executors.newScheduledThreadPool(Integer.MAX_VALUE);
        variations_InProgress = new HashMap<DataType, DataVariation_Params>(); 
        realData = new ArrayList<Data>(); 
    }

    public void Init_RealData(boolean useDefaultValues)
    {        
        // TEMPERATURE, HUMIDITY, AIR_QUALITY, SMOKE, FLOOD, PRESENCE, MOTION, CAMERA;

        if (useDefaultValues)
        {
            // these values are the same we can find in the gui and in demo files
            
            realData.add(new Data(DataType.TEMPERATURE, 20.00f)); 
            realData.add(new Data(DataType.HUMIDITY, 40f));
            realData.add(new Data(DataType.SMOKE, 0.2f)); 
            realData.add(new Data(DataType.MOTION, 0f));
            realData.add(new Data(DataType.FLOOD, 2f)); 
            
            //airq uses a special constructor
            Map<String, Float> airqParams = new HashMap<String, Float>(); 
            airqParams.put("CO2", 500f); 
            airqParams.put("CO", 3f); 
            airqParams.put("PM2.5", 5f); 
            airqParams.put("TVOC", 30f); 
            realData.add(new Data(airqParams)); 
        }
        else
        {
            // data.value will be equal to type.format (e.g. for temperature "* @")
            realData.add(new Data(DataType.TEMPERATURE)); 
            realData.add(new Data(DataType.HUMIDITY)); 
            realData.add(new Data(DataType.AIR_QUALITY)); 
            realData.add(new Data(DataType.SMOKE)); 
            realData.add(new Data(DataType.MOTION)); 
            realData.add(new Data(DataType.FLOOD)); 
        }
    }

    public void Init_RealData(DataType type, String value)
    {
        realData.add(new Data(type, value)); 
    }


    public void Add_Neighbor(String name)
    {
        this.NEIGHBORING_ROOMS.add(name); 
    }

    @Override
    public void SubscribeTo(Channel channel) 
    {
        //System.out.println(this.NAME + " (EVENT): Subscribed to "+channel.CHANNEL_NAME); 
        channel.AddSub(this);
    }

    @Override
    public void ReceiveMessage(Channel channel, Message message) 
    {
        if (channel.TYPE == ChannelType.ROOM)
        {
            switch(message.EVENT_TYPE)
            {
                
                case VARIATE_DATA: 
                    // a message indicating a thread has done variating a value, so this class needs to update it now
                    
                    GetRealData(message.DATA_TYPE).VariateValue_FromFloat(message.variation_NewValue, message.variation_AirQualityParam); 
                    
                    DataVariation_Params variation = variations_InProgress.get(message.DATA_TYPE);
                    
                    // sign correction helps having a single if, because if we are increasing values we'll go until (curr >= 25°C)
                    // if we are decreasing we'll go until ( -curr >= -18°C ), ie. (curr <= 18°C)
                    int decrease_SignCorrection = +1; 
                    if ( (variation.variationType == VariationType.DECREASE) || (variation.variationType == VariationType.DECREASE_FASTER) || (variation.variationType == VariationType.DECREASE_FASTEST) )
                        decrease_SignCorrection = -1; 

                    
                    // check on target value, if we reached it, kill thread 
                    if (decrease_SignCorrection * message.variation_NewValue >= decrease_SignCorrection * variation.targetValue)
                    {
                        // false param = wait the current execution, if there is one, before stopping the thread 
                        variation.future.cancel(false); 
                        variations_InProgress.remove(message.DATA_TYPE); 
                    }

                    //Log.getInstance().Write(this.NAME + " (VARIATE_DATA): Data "+message.DATA_TYPE + " variated to "+message.variation_NewValue);
                    
                break;

                case VARIATE_DATA_REQUEST: 
                    // a message REQUESTING to perform a variation on a real data (e.g. increase temperature) or start a trigger (e.g. motion sensor trigger)
                    // it gets received only ONCE when the variation is requested
                    if (message.VARIATION_TYPE != VariationType.TRIGGER)
                    {                        
                        // if there is already a variation in progress on this type 
                        // ie. another thread is running to update the value, kill it
                        // and start another variation from the current value 
                        // (ie. one variation at a time; 
                        // yes, this includes airq type with their many params. one. variation. at. a time.)
                        if (variations_InProgress.get(message.DATA_TYPE) != null) 
                        {
                            // true param = stop it now 
                            variations_InProgress.get(message.DATA_TYPE).future.cancel(true); 
                        }    

                        // start a new thread doing the variation on this.RealData every "tic" (aka variation speed)
                        // it will fire an event typed VARIATE_DATA, caught in the previous if, to tell this class it needs to update a value
                        interval_Between_Variations_Seconds = DataType_Helper.GetVariationSpeed(message.DATA_TYPE); 
                        DataVariation_Runnable thread = new DataVariation_Runnable(GetRealData(message.DATA_TYPE), message.VARIATION_TYPE, this.NAME, message.variation_AirQualityParam);
                        ScheduledFuture<?> future = executor_Service.scheduleAtFixedRate(thread, (long) interval_Between_Variations_Seconds, (long) interval_Between_Variations_Seconds, TimeUnit.SECONDS); 
                        // save this variations parameters for further controls, e.g. controls on having reached the target values 
                        DataVariation_Params params = new DataVariation_Params(future, message.variation_Target, message.VARIATION_TYPE);

                        variations_InProgress.put(message.DATA_TYPE, params);
                    }
                    else
                    {
                        // trigger (for motion sensors only) are handled here without generating a VARIATE_DATA event
                        // since there is no need to start a thread for this variation
                        GetRealData(message.DATA_TYPE).VariateValue_FromFloat(message.variation_Target, null); 

                        // since this is a trigger we actively send a message to our sensors telling them "something happened" 
                        // they will decide if this "something" is enough to trigger them (e.g. some motion sensors ignore small values)
                        // since this was not a request, the message does not know "who" to send this trigger though (e.g. sensor uid "MOTN_7")
                        // but from the house class we know which sensor was placed in the room where this trigger happened. we seek it there. 
                        Event event = new Event(EventType.MEASURE_RESPONSE); 
                        String sensorUID = ModelDataLoader.getInstance().house.GetUID_FromRoom(this.NAME, message.DATA_TYPE); 
                        Message response = new Message(EventType.MEASURE_RESPONSE, message.DATA_TYPE, sensorUID, GetRealData(message.DATA_TYPE)); 
                        event.PublishTo(channel, response);

                        if ((message.DATA_TYPE == DataType.MOTION) && (message.variation_motionDuration != -1))
                        {
                            // moreover, since this is a motion value, it has a "duration" parameter (in seconds)
                            // which is how long the duration is lasting
                            // we start a job that when that duration has passed, sends our sensor a message like "motion has stopped" automatically

                            executor_Service.schedule( 
                                () -> { send_StopMotionMessage(channel); }, // a lambda expression! nice job.
                                message.variation_motionDuration.longValue(), 
                                TimeUnit.SECONDS); 
                        }
                    }
                
                break; 

                case MEASURE_RESPONSE_REQUEST: 
                    // get the correct-typed real data in this room (e.g. temperature) and forward to anyone listening on my channel
                    // specifically, the PNinstance will receive it and dispatch it to PN and consequently to the correct Sensor requesting it
                    // which will in turn apply their calculations (e.g. sensitivity casting etc.) and forward it when done to the Openhab binding
                    // this message gets received by "this" very same object too, but we will ignore it.
                    Data realData = GetRealData(message.DATA_TYPE); 
                    Event event = new Event(EventType.MEASURE_RESPONSE); 
                    Message response = new Message(EventType.MEASURE_RESPONSE, message.DATA_TYPE, message.request_sensorUID, realData); 
                    event.PublishTo(channel, response);
                break; 
                
                case MEASURE_RESPONSE: break; 
                
                default: 
                    Log.getInstance().Write("ERROR (" +this.NAME + ") : received another Message type.");
                    break; 
            }
            
        }

        else 
        {
            if (channel.TYPE == ChannelType.SENSOR)
            {
                Log.getInstance().Write("(ERROR) : Received a message destined to a sensor channel.");
            }
            else 
            {
                Log.getInstance().Write("(ERROR) : channel unknown."); 
            }
        }
    }

    public Data GetRealData(DataType type)
    {
        //in c# c'era quella cosa del get x => x.type == type, qui mi sa che si fa così 
        for (Data data : realData) 
            if (data.TYPE == type)
                return data; 

        return null; 
    }
    

    private void send_StopMotionMessage(Channel channel)
    {
        Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
        // this is a special message that will tell our room to send a message to its sensor
        // in which there is written "motion in this room has stopped (value 0f)"
        // -1 as the duration param serves as a sentinel, which will avoid this thread method to be called again with this new message
        Message message = new Message(EventType.VARIATE_DATA_REQUEST, DataType.MOTION, VariationType.TRIGGER, 0f, -1f);
        event.PublishTo(channel, message);
    }


    private class DataVariation_Runnable implements Runnable
    {
        //in percentage every thread "tic"
        private final float VARIATION_LEVEL1 = 1.25f; // 0.5 
        private final float VARIATION_LEVEL2 = 1.5f; //0.75
        private final float VARIATION_LEVEL3 = 2.25f; //1.25
        private final float VARIATION_LEVEL; 

        // smoke variation needs to be quickier than the others
        // it usually happens very fast
        private final float SMOKE_VARIATION_SPEED = 3f;
        private final float SMOKE_VARIATION_OFFSET = 0.2f;  

        // also water leakage, it is almost instant when there is sufficient water on the sensor
        private final float FLOOD_VARIATION_SPEED = 1.2f; 
        
        private Data dataToVariate; 
        private String airQualityParam = null;
        private String roomName; 

        private DataVariation_Runnable(Data dataToVariate, VariationType variationType, String roomName)
        {
            this.roomName = roomName; 
            this.dataToVariate = dataToVariate;
            
            float v = 0; 
            switch (variationType)
            {
                case DECREASE:          v = -VARIATION_LEVEL1;   break;
                case DECREASE_FASTER:   v = -VARIATION_LEVEL2;   break;
                case DECREASE_FASTEST:  v = -VARIATION_LEVEL3;   break;
                case INCREASE:          v = +VARIATION_LEVEL1;   break;
                case INCREASE_FASTER:   v = +VARIATION_LEVEL2;   break;
                case INCREASE_FASTEST:  v = +VARIATION_LEVEL3;   break;
                case TRIGGER:           break;  
                default: break;
            }
            
            this.VARIATION_LEVEL = v; 
        }

        private DataVariation_Runnable(Data dataToVariate, VariationType variationType, String roomName, String airQualityParam)
        {
            //dark magic to call main constructor
            this(dataToVariate, variationType, roomName); 
            this.airQualityParam = airQualityParam; 
        }

        @Override
        public void run() 
        {
            try {
            float value = 0; 
            switch (dataToVariate.TYPE)
            {
                case TEMPERATURE:   value = this.dataToVariate.ToTemperature(); break; 
                case HUMIDITY:      value = this.dataToVariate.ToHumidity(); break; 
                case SMOKE:         value = this.dataToVariate.ToSmoke(); break;
                case FLOOD:         value = this.dataToVariate.ToFlood(); break;
                case AIR_QUALITY:   value = this.dataToVariate.ToAirQuality(airQualityParam); break; 
                default: break; 
            }
            
            // smoke variations have a starting offset because we have % variations and it wouldn't work with [0..1] values
            if ((value == 0) && (dataToVariate.TYPE == DataType.SMOKE))
                value = SMOKE_VARIATION_OFFSET; 
            
            
            // smoke and flood variations need to be very fast
            if (dataToVariate.TYPE == DataType.SMOKE)
                value += (VARIATION_LEVEL / 100 * value) * SMOKE_VARIATION_SPEED; 
            
            else if (dataToVariate.TYPE == DataType.FLOOD)
                value += (VARIATION_LEVEL / 100 * value) * FLOOD_VARIATION_SPEED; 
            
            else 
                value += (VARIATION_LEVEL / 100 * value);
            
            //tell everyone listening on this roomChannel about these variations of safehome_simulatedenvironment (e.g Room object need to update its value, Log needs to know, ...)
            Event event = new Event(EventType.VARIATE_DATA); 
            Message message = new Message(dataToVariate.TYPE, value, airQualityParam); 
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(roomName, ChannelType.ROOM);
            event.PublishTo(channel, message);
            }
            catch (Throwable e) 
            {
                //throwable is the superclass of both exception and error, which a scheduled executor service might raise
                 if (e.getMessage() != null) Log.getInstance().Write("EXCEPTION : "+e.getMessage()); 
                 if (e.getStackTrace() != null) Log.getInstance().Write("ERROR : "+e.getStackTrace());  
                 e.printStackTrace();
            }
        }

    }

    public class DataVariation_Params 
    {
        public volatile ScheduledFuture<?> future; 
        public Float targetValue; 
        public VariationType variationType; 

        public DataVariation_Params(ScheduledFuture<?> future, Float targetValue, VariationType variationType)
        { 
            this.future = future; 
            this.targetValue = targetValue; 
            this.variationType = variationType;  
        }

    }
}