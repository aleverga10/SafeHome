package safehome_se.gui;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.jfoenix.controls.JFXButton;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import safehome_se.environment.Room;
import safehome_se.environment.Data.DataType;
import safehome_se.pubsub.Channel;
import safehome_se.pubsub.Event;
import safehome_se.pubsub.Message;
import safehome_se.pubsub.PubSubManager;
import safehome_se.pubsub.Channel.ChannelType;
import safehome_se.pubsub.Event.EventType;
import safehome_se.pubsub.Message.VariationType;

public class GUI_ScenarioSceneController
{

    @FXML
    private JFXButton startButton; 

    @FXML
    private FontAwesomeIconView okIcon; 

    public void startScenario(ActionEvent e)
    {
        try 
        {
            ActivateScenario_AutoVariations();
            okIcon.setOpacity(1);
        }
        catch (InterruptedException ex)
        {
        }
    }


    public void ActivateScenario_AutoVariations() throws InterruptedException
    {
        // here we construct a constant scenario ("Environment scenario") for our tests
        
        // it will be the same for each of our testing scenarios (e.g. sensor failure, measurement error, ...)
        // the same data will be variated in the same order at the same specified scheduled time
        // and will last for the same amount of time (over which, all data will be still and there will be no variations)

        // some variations are constructed ad hoc (on purpose) to allow our test cases the possibility to fail
        
        // e.g. sometimes data will fluctuate around threshold but never overcoming it, 
        // but in a measurement error scenario a sensor might detect it is over the threshold and trigger the alarm
        // which would never happen in a "perfect" scenario

        // the same concept is applied to scenario 4 (openhab delay)
        // openhab alarm will "sound" for [alarmTime] (e.g 60 s) and in that time it cannot be triggered; 
        // after that time it will "rearm" and might sound again (as do real alarms, in fact)
        // we will put some triggers that will on purpose be near that [alarmTime] (e.g. one will surpass threshold 58 s after another for a brief moment (e.g. motion) )
        // in a case where openhab delay is more than 2s, the second alarm will in fact sound (the alarm being rearmed during the delay)
        
        // both will result in a change of Prob. of alarm (which we are evaluating)
        
        List<Room> rooms = ModelDataLoader.getInstance().house.rooms;
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(Integer.MAX_VALUE);
    
    // room 1 living room (THASM)
        // after 12 s -> TRIGGER MOTION 0.91 for 3s     *****
        executorService.schedule( () -> {
                        
            int roomN = 1; // change
            VariationType speed = VariationType.TRIGGER; // change
            DataType type = DataType.MOTION;   // change
            Float targetValue = 0.91f; // change
            
            Message message = null; 
            if (type == DataType.MOTION)
            {
                float motionDuration = 3f; // change if motion
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue, motionDuration);
            }
            else if (type == DataType.AIR_QUALITY)
            {    
                // change if airq
                String airqparam = "CO2";
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, airqparam, speed, targetValue);
            }
            else
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 

            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 12, TimeUnit.SECONDS); //change
        // after 44 s -> INCREASE SMOKE 4f              
        executorService.schedule( () -> {
                        
            int roomN = 1; // change
            VariationType speed = VariationType.INCREASE; // change
            DataType type = DataType.SMOKE;   // change
            Float targetValue = 4f; // change
            
            Message message = null; 
            if (type == DataType.MOTION)
            {
                float motionDuration = 2f; // change if motion
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue, motionDuration);
            }
            else if (type == DataType.AIR_QUALITY)
            {    
                // change if airq
                String airqparam = "CO2";
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, airqparam, speed, targetValue);
            }
            else
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 

            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 44, TimeUnit.SECONDS); //change
        // after 64 s -> INCREASE_FASTEST AIR_QUALITY CO2 5790f
        executorService.schedule( () -> {
                        
            int roomN = 1; // change
            VariationType speed = VariationType.INCREASE_FASTEST; // change
            DataType type = DataType.AIR_QUALITY;   // change
            Float targetValue = 5790f; // change
            
            Message message = null; 
            if (type == DataType.MOTION)
            {
                float motionDuration = 2f; // change if motion
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue, motionDuration);
            }
            else if (type == DataType.AIR_QUALITY)
            {    
                // change if airq
                String airqparam = "CO2";
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, airqparam, speed, targetValue);
            }
            else
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 

            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 64, TimeUnit.SECONDS); //change
        // after 103 s -> TRIGGER MOTION  0.22f for 4f  ******
        executorService.schedule( () -> {
                        
            int roomN = 1; // change
            VariationType speed = VariationType.TRIGGER; // change
            DataType type = DataType.MOTION;   // change
            Float targetValue = 0.22f; // change
            
            Message message = null; 
            if (type == DataType.MOTION)
            {
                float motionDuration = 4f; // change if motion
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue, motionDuration);
            }
            else if (type == DataType.AIR_QUALITY)
            {    
                // change if airq
                String airqparam = "CO2";
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, airqparam, speed, targetValue);
            }
            else
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 

            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 103, TimeUnit.SECONDS); //change
        // after 114 s -> INCREASE AIR_QUALITY CO2 8770f
        executorService.schedule( () -> {
                        
            int roomN = 1; // change
            VariationType speed = VariationType.INCREASE; // change
            DataType type = DataType.AIR_QUALITY;   // change
            Float targetValue = 8770f; // change
            
            Message message = null; 
            if (type == DataType.MOTION)
            {
                float motionDuration = 2f; // change if motion
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue, motionDuration);
            }
            else if (type == DataType.AIR_QUALITY)
            {    
                // change if airq
                String airqparam = "CO2";
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, airqparam, speed, targetValue);
            }
            else
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 

            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 114, TimeUnit.SECONDS); //change
        // after 116 s -> INCREASE_FASTER SMOKE 4.6f    *****
        executorService.schedule( () -> {
                        
            int roomN = 1; // change
            VariationType speed = VariationType.INCREASE_FASTER; // change
            DataType type = DataType.SMOKE;   // change
            Float targetValue = 4.6f; // change
            
            Message message = null; 
            if (type == DataType.MOTION)
            {
                float motionDuration = 2f; // change if motion
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue, motionDuration);
            }
            else if (type == DataType.AIR_QUALITY)
            {    
                // change if airq
                String airqparam = "CO2";
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, airqparam, speed, targetValue);
            }
            else
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 

            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 116, TimeUnit.SECONDS); //change
        // after 116 s -> INCREASE_FASTEST TEMPERATURE 32.8f;
        executorService.schedule( () -> {
                        
            int roomN = 1; // change
            VariationType speed = VariationType.INCREASE_FASTEST ; // change
            DataType type = DataType.TEMPERATURE;   // change
            Float targetValue = 32.8f; // change
            
            Message message = null; 
            if (type == DataType.MOTION)
            {
                float motionDuration = 2f; // change if motion
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue, motionDuration);
            }
            else if (type == DataType.AIR_QUALITY)
            {    
                // change if airq
                String airqparam = "CO2";
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, airqparam, speed, targetValue);
            }
            else
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 

            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 116, TimeUnit.SECONDS); //change
        // after 148 s -> DECREASE SMOKE 2.2f;
        executorService.schedule( () -> {
                        
            int roomN = 1; // change
            VariationType speed = VariationType.DECREASE ; // change
            DataType type = DataType.SMOKE;   // change
            Float targetValue = 2.2f; // change
            
            Message message = null; 
            if (type == DataType.MOTION)
            {
                float motionDuration = 2f; // change if motion
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue, motionDuration);
            }
            else if (type == DataType.AIR_QUALITY)
            {    
                // change if airq
                String airqparam = "CO2";
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, airqparam, speed, targetValue);
            }
            else
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 

            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 148, TimeUnit.SECONDS); //change
        // after 166 s -> TRIGGER MOTION 0.18f 6f
        executorService.schedule( () -> {
                        
            int roomN = 1; // change
            VariationType speed = VariationType.TRIGGER ; // change
            DataType type = DataType.MOTION;   // change
            Float targetValue = 0.18f; // change
            
            Message message = null; 
            if (type == DataType.MOTION)
            {
                float motionDuration = 6f; // change if motion
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue, motionDuration);
            }
            else if (type == DataType.AIR_QUALITY)
            {    
                // change if airq
                String airqparam = "CO2";
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, airqparam, speed, targetValue);
            }
            else
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 

            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 166, TimeUnit.SECONDS); //change
        // after 178 s -> DECREASE_FASTER AIR_QUALITY CO2 6520
        executorService.schedule( () -> {
                            
            int roomN = 1; // change
            VariationType speed = VariationType.DECREASE_FASTER; // change
            DataType type = DataType.AIR_QUALITY;   // change
            Float targetValue = 6520f; // change
            
            Message message = null; 
            if (type == DataType.MOTION)
            {
                float motionDuration = 2f; // change if motion
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue, motionDuration);
            }
            else if (type == DataType.AIR_QUALITY)
            {    
                // change if airq
                String airqparam = "CO2";
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, airqparam, speed, targetValue);
            }
            else
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 

            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 178, TimeUnit.SECONDS); //change
        // after 198 s -> DECREASE TEMPERATURE  11.8f
        executorService.schedule( () -> {
                        
            int roomN = 1; // change
            VariationType speed = VariationType.DECREASE ; // change
            DataType type = DataType.TEMPERATURE;   // change
            Float targetValue = 11.8f; // change
            
            Message message = null; 
            if (type == DataType.MOTION)
            {
                float motionDuration = 2f; // change if motion
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue, motionDuration);
            }
            else if (type == DataType.AIR_QUALITY)
            {    
                // change if airq
                String airqparam = "CO2";
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, airqparam, speed, targetValue);
            }
            else
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 

            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 198, TimeUnit.SECONDS); //change
        // after 267 s -> TRIGGER MOTION  0.85  8f
        executorService.schedule( () -> {
                        
            int roomN = 1; // change
            VariationType speed = VariationType.TRIGGER ; // change
            DataType type = DataType.MOTION;   // change
            Float targetValue = 0.85f; // change
            
            Message message = null; 
            if (type == DataType.MOTION)
            {
                float motionDuration = 8f; // change if motion
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue, motionDuration);
            }
            else if (type == DataType.AIR_QUALITY)
            {    
                // change if airq
                String airqparam = "CO2";
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, airqparam, speed, targetValue);
            }
            else
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 

            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 267, TimeUnit.SECONDS); //change
        // after 293 s -> INCREASE_FASTEST HUMIDITY   54.8f;
        executorService.schedule( () -> {
                        
            int roomN = 1; // change
            VariationType speed = VariationType.INCREASE_FASTEST ; // change
            DataType type = DataType.HUMIDITY;   // change
            Float targetValue = 54.8f; // change
            
            Message message = null; 
            if (type == DataType.MOTION)
            {
                float motionDuration = 2f; // change if motion
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue, motionDuration);
            }
            else if (type == DataType.AIR_QUALITY)
            {    
                // change if airq
                String airqparam = "CO2";
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, airqparam, speed, targetValue);
            }
            else
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 

            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 293, TimeUnit.SECONDS); //change
        // after 310 s -> TRIGGER MOTION    0.6 8f;
        executorService.schedule( () -> {
                        
            int roomN = 1; // change
            VariationType speed = VariationType.TRIGGER ; // change
            DataType type = DataType.MOTION;   // change
            Float targetValue = 0.6f; // change
            
            Message message = null; 
            if (type == DataType.MOTION)
            {
                float motionDuration = 8f; // change if motion
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue, motionDuration);
            }
            else if (type == DataType.AIR_QUALITY)
            {    
                // change if airq
                String airqparam = "CO2";
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, airqparam, speed, targetValue);
            }
            else
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 

            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 310, TimeUnit.SECONDS); //change
       // after 328 s -> INCREASE_FASTER AIR_QUALITY PM2.5  21.2f;
        executorService.schedule( () -> {
                        
            int roomN = 1; // change
            VariationType speed = VariationType.INCREASE_FASTER ; // change
            DataType type = DataType.AIR_QUALITY;   // change
            Float targetValue = 21.2f; // change
            
            Message message = null; 
            if (type == DataType.MOTION)
            {
                float motionDuration = 2f; // change if motion
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue, motionDuration);
            }
            else if (type == DataType.AIR_QUALITY)
            {    
                // change if airq
                String airqparam = "PM2.5";
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, airqparam, speed, targetValue);
            }
            else
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 

            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 328, TimeUnit.SECONDS); //change
        // after 361 s -> INCREASE_FASTEST TEMPERATURE 22.3
        executorService.schedule( () -> {
                        
            int roomN = 1; // change
            VariationType speed = VariationType.INCREASE_FASTEST ; // change
            DataType type = DataType.TEMPERATURE;   // change
            Float targetValue = 22.3f; // change
            
            Message message = null; 
            if (type == DataType.MOTION)
            {
                float motionDuration = 2f; // change if motion
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue, motionDuration);
            }
            else if (type == DataType.AIR_QUALITY)
            {    
                // change if airq
                String airqparam = "CO2";
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, airqparam, speed, targetValue);
            }
            else
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 

            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 361, TimeUnit.SECONDS); //change
        // after 402 s -> INCREASE_FASTER AIR_QUALITY CO 33f
        executorService.schedule( () -> {
                        
            int roomN = 1; // change
            VariationType speed = VariationType.INCREASE_FASTEST; // change
            DataType type = DataType.AIR_QUALITY;   // change
            Float targetValue = 33f; // change
            
            Message message = null; 
            if (type == DataType.MOTION)
            {
                float motionDuration = 2f; // change if motion
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue, motionDuration);
            }
            else if (type == DataType.AIR_QUALITY)
            {    
                // change if airq
                String airqparam = "CO";
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, airqparam, speed, targetValue);
            }
            else
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 

            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 402, TimeUnit.SECONDS); //change
       // after 418 s -> INCREASE_FASTER TEMPERATURE  38.1
        executorService.schedule( () -> {
                        
            int roomN = 1; // change
            VariationType speed = VariationType.INCREASE_FASTER; // change
            DataType type = DataType.TEMPERATURE;   // change
            Float targetValue = 38.1f; // change
            
            Message message = null; 
            if (type == DataType.MOTION)
            {
                float motionDuration = 2f; // change if motion
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue, motionDuration);
            }
            else if (type == DataType.AIR_QUALITY)
            {    
                // change if airq
                String airqparam = "CO2";
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, airqparam, speed, targetValue);
            }
            else
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 

            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 418, TimeUnit.SECONDS); //change
        // after 439 s -> INCREASE_FASTEST HUMIDITY 67.4f
        executorService.schedule( () -> {
                        
            int roomN = 1; // change
            VariationType speed = VariationType.INCREASE_FASTEST ; // change
            DataType type = DataType.HUMIDITY;   // change
            Float targetValue = 67.4f; // change
            
            Message message = null; 
            if (type == DataType.MOTION)
            {
                float motionDuration = 2f; // change if motion
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue, motionDuration);
            }
            else if (type == DataType.AIR_QUALITY)
            {    
                // change if airq
                String airqparam = "CO";
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, airqparam, speed, targetValue);
            }
            else
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 

            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 439, TimeUnit.SECONDS); //change
        // after 500 s -> DECREASE TEMPERATURE  34.7
        executorService.schedule( () -> {
                                
            int roomN = 1; // change
            VariationType speed = VariationType.DECREASE ; // change
            DataType type = DataType.TEMPERATURE;   // change
            Float targetValue = 34.7f; // change
            
            Message message = null; 
            if (type == DataType.MOTION)
            {
                float motionDuration = 2f; // change if motion
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue, motionDuration);
            }
            else if (type == DataType.AIR_QUALITY)
            {    
                // change if airq
                String airqparam = "CO2";
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, airqparam, speed, targetValue);
            }
            else
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 

            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

}, 500, TimeUnit.SECONDS); //change



    // room 0 bathroom (THF)
        // after 13 s -> INCREASE_FASTEST HUMIDITY  76.8f
        executorService.schedule( () -> {
                        
            int roomN = 0; // change
            VariationType speed = VariationType.INCREASE_FASTEST; // change
            DataType type = DataType.HUMIDITY;   // change
            Float targetValue = 76.8f; // change
            
            Message message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 
            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 13, TimeUnit.SECONDS); //change
        // after 24 s -> INCREASE TEMPERATURE  26.4f; 
        executorService.schedule( () -> {
                        
            int roomN = 0; // change
            VariationType speed = VariationType.INCREASE; // change
            DataType type = DataType.TEMPERATURE;   // change
            Float targetValue = 26.4f; // change
            
            Message message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 
            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 24, TimeUnit.SECONDS); //change       
        // after 38 s -> INCREASE_FASTER FLOOD  2.26
        executorService.schedule( () -> {
                
            int roomN = 0; // change
            VariationType speed = VariationType.INCREASE_FASTER ; // change
            DataType type = DataType.FLOOD;   // change
            Float targetValue = 2.26f; // change
            
            Message message = null; 
            if (type == DataType.MOTION)
            {
                float motionDuration = 2f; // change if motion
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue, motionDuration);
            }
            else if (type == DataType.AIR_QUALITY)
            {    
                // change if airq
                String airqparam = "CO2";
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, airqparam, speed, targetValue);
            }
            else
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 

            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 38, TimeUnit.SECONDS); //change 
        // after 44 s -> DECREASE HUMIDITY  48f;
        executorService.schedule( () -> {
                        
            int roomN = 0; // change
            VariationType speed = VariationType.DECREASE; // change
            DataType type = DataType.HUMIDITY;   // change
            Float targetValue = 48f; // change
            
            Message message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 
            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 44, TimeUnit.SECONDS); //change
        // after 95 s -> INCREASE HUMIDITY  67f;
        executorService.schedule( () -> {
                        
            int roomN = 0; // change
            VariationType speed = VariationType.INCREASE; // change
            DataType type = DataType.HUMIDITY;   // change
            Float targetValue = 67f; // change
            
            Message message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 
            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 95, TimeUnit.SECONDS); //change
        // after 114 s -> INCREASE TEMPERATURE  43.1f;
        executorService.schedule( () -> {
                        
            int roomN = 0; // change
            VariationType speed = VariationType.INCREASE; // change
            DataType type = DataType.TEMPERATURE;   // change
            Float targetValue = 43.1f; // change
            
            Message message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 
            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 114, TimeUnit.SECONDS); //change
        // after 166 s -> DECREASE_FASTER TEMPERATURE  28.5f;
        executorService.schedule( () -> {
                        
            int roomN = 0; // change
            VariationType speed = VariationType.DECREASE_FASTER; // change
            DataType type = DataType.TEMPERATURE;   // change
            Float targetValue = 28.5f; // change
            
            Message message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 
            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 166, TimeUnit.SECONDS); //change
        // after 180 s -> INCREASE FLOOD  3.8
        executorService.schedule( () -> {
                        
            int roomN = 0; // change
            VariationType speed = VariationType.INCREASE; // change
            DataType type = DataType.FLOOD;   // change
            Float targetValue = 3.8f; // change
            
            Message message = null; 
            if (type == DataType.MOTION)
            {
                float motionDuration = 2f; // change if motion
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue, motionDuration);
            }
            else if (type == DataType.AIR_QUALITY)
            {    
                // change if airq
                String airqparam = "CO2";
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, airqparam, speed, targetValue);
            }
            else
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 

            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 180, TimeUnit.SECONDS); //change
        // after 198 s -> DECREASE TEMPERATURE  12.8f;
        executorService.schedule( () -> {
                        
            int roomN = 0; // change
            VariationType speed = VariationType.DECREASE; // change
            DataType type = DataType.TEMPERATURE;   // change
            Float targetValue = 12.8f; // change
            
            Message message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 
            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 198, TimeUnit.SECONDS); //change
        // after 235 s -> DECREASE_FASTEST FLOOD  2.0f
        executorService.schedule( () -> {
                
            int roomN = 0; // change
            VariationType speed = VariationType.DECREASE_FASTEST; // change
            DataType type = DataType.FLOOD;   // change
            Float targetValue = 2.0f; // change
            
            Message message = null; 
            if (type == DataType.MOTION)
            {
                float motionDuration = 2f; // change if motion
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue, motionDuration);
            }
            else if (type == DataType.AIR_QUALITY)
            {    
                // change if airq
                String airqparam = "CO2";
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, airqparam, speed, targetValue);
            }
            else
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 

            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 235, TimeUnit.SECONDS); //change   
        // after 250 s -> DECREASE_FASTEST HUMIDITY  34f;
        executorService.schedule( () -> {
                        
            int roomN = 0; // change
            VariationType speed = VariationType.DECREASE_FASTEST; // change
            DataType type = DataType.HUMIDITY;   // change
            Float targetValue = 34f; // change
            
            Message message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 
            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 250, TimeUnit.SECONDS); //change
  
    // the same but shifted in time by 250s so that it ends around the other one in living room
        // after 13 s -> INCREASE_FASTEST HUMIDITY  76.8f
        executorService.schedule( () -> {
                            
            int roomN = 0; // change
            VariationType speed = VariationType.INCREASE_FASTEST; // change
            DataType type = DataType.HUMIDITY;   // change
            Float targetValue = 76.8f; // change
            
            Message message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 
            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 250+13, TimeUnit.SECONDS); //change
        // after 24 s -> INCREASE TEMPERATURE  26.4f; 
        executorService.schedule( () -> {
                        
            int roomN = 0; // change
            VariationType speed = VariationType.INCREASE; // change
            DataType type = DataType.TEMPERATURE;   // change
            Float targetValue = 26.4f; // change
            
            Message message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 
            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 250+24, TimeUnit.SECONDS); //change
        // after 44 s -> DECREASE HUMIDITY  48f;
        executorService.schedule( () -> {
                        
            int roomN = 0; // change
            VariationType speed = VariationType.DECREASE; // change
            DataType type = DataType.HUMIDITY;   // change
            Float targetValue = 48f; // change
            
            Message message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 
            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 250+44, TimeUnit.SECONDS); //change
        // after 95 s -> INCREASE HUMIDITY  67f;
        executorService.schedule( () -> {
                        
            int roomN = 0; // change
            VariationType speed = VariationType.INCREASE; // change
            DataType type = DataType.HUMIDITY;   // change
            Float targetValue = 67f; // change
            
            Message message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 
            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 250+95, TimeUnit.SECONDS); //change
        // after 98 s -> INCREASE_FASTER FLOOD  2.26
        executorService.schedule( () -> {
                
            int roomN = 1; // change
            VariationType speed = VariationType.INCREASE_FASTER ; // change
            DataType type = DataType.FLOOD;   // change
            Float targetValue = 2.26f; // change
            
            Message message = null; 
            if (type == DataType.MOTION)
            {
                float motionDuration = 2f; // change if motion
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue, motionDuration);
            }
            else if (type == DataType.AIR_QUALITY)
            {    
                // change if airq
                String airqparam = "CO2";
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, airqparam, speed, targetValue);
            }
            else
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 

            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 250+98, TimeUnit.SECONDS); //change
        // after 114 s -> INCREASE TEMPERATURE  43.1f;
        executorService.schedule( () -> {
                        
            int roomN = 0; // change
            VariationType speed = VariationType.INCREASE; // change
            DataType type = DataType.TEMPERATURE;   // change
            Float targetValue = 43.1f; // change
            
            Message message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 
            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 250+114, TimeUnit.SECONDS); //change
        // after 166 s -> DECREASE_FASTER TEMPERATURE  28.5;
        executorService.schedule( () -> {
                        
            int roomN = 0; // change
            VariationType speed = VariationType.DECREASE_FASTER; // change
            DataType type = DataType.TEMPERATURE;   // change
            Float targetValue = 28.5f; // change
            
            Message message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 
            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 250+166, TimeUnit.SECONDS); //change
        // after 180 s -> INCREASE FLOOD  3.8
        executorService.schedule( () -> {
                        
            int roomN = 0; // change
            VariationType speed = VariationType.INCREASE; // change
            DataType type = DataType.FLOOD;   // change
            Float targetValue = 3.8f; // change
            
            Message message = null; 
            if (type == DataType.MOTION)
            {
                float motionDuration = 2f; // change if motion
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue, motionDuration);
            }
            else if (type == DataType.AIR_QUALITY)
            {    
                // change if airq
                String airqparam = "CO2";
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, airqparam, speed, targetValue);
            }
            else
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 

            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 250+180, TimeUnit.SECONDS); //change
        // after 198 s -> DECREASE TEMPERATURE  12.8f;
        executorService.schedule( () -> {
                        
            int roomN = 0; // change
            VariationType speed = VariationType.DECREASE; // change
            DataType type = DataType.TEMPERATURE;   // change
            Float targetValue = 12.8f; // change
            
            Message message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 
            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 250+198, TimeUnit.SECONDS); //change
        // after 235 s -> DECREASE_FASTEST FLOOD  2.0f
        executorService.schedule( () -> {
        
            int roomN = 0; // change
            VariationType speed = VariationType.DECREASE_FASTEST; // change
            DataType type = DataType.FLOOD;   // change
            Float targetValue = 2.0f; // change
            
            Message message = null; 
            if (type == DataType.MOTION)
            {
                float motionDuration = 2f; // change if motion
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue, motionDuration);
            }
            else if (type == DataType.AIR_QUALITY)
            {    
                // change if airq
                String airqparam = "CO2";
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, airqparam, speed, targetValue);
            }
            else
                message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 

            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            
            Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
            event.PublishTo(channel, message);

        }, 250+235, TimeUnit.SECONDS); //change   
        // after 250 s -> DECREASE_FASTEST HUMIDITY  34f;
        executorService.schedule( () -> {
                    
        int roomN = 0; // change
        VariationType speed = VariationType.DECREASE_FASTEST; // change
        DataType type = DataType.HUMIDITY;   // change
        Float targetValue = 34f; // change
        
        Message message = new Message(EventType.VARIATE_DATA_REQUEST, type, speed, targetValue); 
        Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
        Channel channel = PubSubManager.getInstance().GetChannel_FromName(rooms.get(roomN).NAME, ChannelType.ROOM); 
        event.PublishTo(channel, message);

    }, 250+250, TimeUnit.SECONDS); //change

    }

}