package safehome_ss.local;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import safehome_ss.communication.PubNub_Instance;
import safehome_ss.pubsub.*;
import safehome_ss.pubsub.Channel.ChannelType;
import safehome_ss.pubsub.Event.EventType;
import safehome_ss.sensors.Data.*;
import safehome_ss.sensors.*;

//import safehome_simulatedenvironment.Battery.BATTERY_STATUS;

public class App 
{
    public synchronized static void main(String[] args) throws Exception 
    { 
        /* **** TEST: scenario failure (nextfloat < threshold) **** *
        Random RNG = new Random(); 
        float n; 
        int c = 0;
        
        for (int j= 0; j < 10; j++)
    {   
        c = 0;  
        for (int i = 0; i < 420; i++)
        {    
            n = RNG.nextFloat();
            if (n < 0.005)
                c++;
        }
        System.out.println("RESULTS: "+c);
    }
        */

        /* **** TEST: scenario 2 gaussian */
        Random RNG = new Random(); 
        double z, n; 
        float acc = 0.4f;
        int c = 0;
        for (int j= 0; j < 10; j++)
        {   
            c = 0; 
            for (int i = 0; i < 1000; i++)
            {
                z = RNG.nextGaussian();
                n = z * (acc / 3); 

                if (n > acc)
                {
                  System.out.println(n);
                     c++;
                }
           }
        System.out.println(c);
        }
        

        /* **** TEST: scenario 4 delay *
        Random RNG = new Random(); 
        float n; 
        long delay, maxDelay = 4000, desired = 200; 
        int c = 0;
        for (int i = 0; i < 100; i++)
        {
            n = RNG.nextFloat();
            delay = BigDecimal.valueOf(n * maxDelay).longValue(); 
            if (delay >= desired)
                c++;
        }
        // 95% of 10000 = 9500; 5% = 500

        System.out.println("RESULTS: "+ (100-c));
        */

        /* **** TEST: scenario 3 alarm fail  *
        Random RNG = new Random(); 
        float n; 
        float causedbySystem = 0.33f; 
        float causedbyOthers_1 = 0.2f, causedbyOthers_2 = 0.05f, causedbyOthers_3 = 0.02f; 
        int c = 0, cbs = 0;
        int totalCalls = 800;
        float pFailure = 0.1f;

        for (int i = 0; i < totalCalls; i++)
        {
            n = RNG.nextFloat();
            if (n < pFailure) 
            {
                float p_cbs = RNG.nextFloat();

                if (p_cbs < causedbySystem)
                    cbs++;
                c++;
            }
        }
        float effectiveP = ( ((float) cbs) / ((float) c) )* 100; 
        // 95% of 10000 = 9500; 5% = 500

        int expFC = (int) Math.round(totalCalls * pFailure); 
        System.out.println("TOTAL EXPECTED FAILED CALLS: "+ expFC);
        System.out.println("REAL FAILED CALLS: "+ (c));
        System.out.println("EXPECTED ALARM PROB.: "+ ((float) (totalCalls - expFC)) / totalCalls * 100);
        System.out.println("REAL ALARM PROB.: "+ ((float)(totalCalls - c)) / totalCalls * 100);
        
        System.out.println("CALLS CAUSED BY SYSTEM: "+ (cbs));
        System.out.println("EXPECTED CAUSED BY SYSTEM PROB.: "+ causedbySystem);
        System.out.println("REAL CAUSED BY SYSTEM PROB.: "+ effectiveP);
        //System.out.println("REAL FAILED CALLS * EXPECTED CBS [%] "+ ((float) c * causedbySystem) / expFC * 100);

        */

        /* **** TEST: Gaussian measurement **** *
        Random rng = new Random(); 
        float stdDev = 0.2f; 
        float measure = 20f;

        double n = 0; 
        double g = 0; 

        int j = 0,k = 0,l= 0,m = 0; 
        int bj = 0,bk = 0,bl= 0,bm = 0; 
        BigDecimal bd; 

        for (int i = 0; i < 1000; i++) 
        {   
            g = rng.nextGaussian(); 
            n = g * stdDev + measure;
            //System.out.print(n + " ");

            if ((n > measure + 3* stdDev) || (n < measure - 3* stdDev))
                j++; 

            else if ((n > measure + 2* stdDev) || (n < measure - 2* stdDev))
                k++;    
        

            else if ((n > measure + 1* stdDev) || (n < measure - 1* stdDev))
                l++; 
        
            else
                m++; 

                
            bd = new BigDecimal(n).setScale(2, RoundingMode.HALF_UP); 
            n = bd.doubleValue(); 

            if ((n > measure + 3* stdDev) || (n < measure - 3* stdDev))
                bj++; 

            else if ((n > measure + 2* stdDev) || (n < measure - 2* stdDev))
                bk++;    
        

            else if ((n > measure + 1* stdDev) || (n < measure - 1* stdDev))
                bl++; 
        
            else
                bm++;
            
        }

        System.out.println("DOUBLE: ");
        System.out.println(" ZERO TO ONE "+ m);
        System.out.println(" ONE TO TWO "+ l);
        System.out.println(" TWO TO THREE " + k); 
        System.out.println(" MORE THAN THREE " + j); 

        System.out.println(""); 
        System.out.println("BIG DECIMAL TO DOUBLE: ");
        System.out.println(" ZERO TO ONE "+ bm);
        System.out.println(" ONE TO TWO "+ bl);
        System.out.println(" TWO TO THREE " + bk); 
        System.out.println(" MORE THAN THREE " + bj); 

        System.out.println(""); 
        System.out.println("ERROR 0-1 " +(m - bm)); 
        System.out.println("ERROR 1-2 " +(l - bl)); 
        System.out.println("ERROR 2-3 " +(k - bk)); 
        System.out.println("ERROR 3+ " +(j - bj)); 
        /*
        test(); 
        */
    }

    private void test()
    {
        PubNub_Instance.getInstance(); 

        /* **** TEST: add some sensor things, create their channels and start their work
        */
        List<Sensor_Thing> sensor_Things = new ArrayList<Sensor_Thing>();
        SensorThingsFactory stf = SensorThingsFactory.getInstance(); 
        
        sensor_Things.add(stf.CreateSensorThing("TH")); 
        sensor_Things.add(stf.CreateSensorThing("HAS")); 
        sensor_Things.add(stf.CreateSensorThing("TM")); 

        PubSubManager pubSubManager = PubSubManager.getInstance(); 
        
        // TEST: alarm
        //pubSubManager.CreateChannel("ALARM", ChannelType.ALARM);

        for (Sensor_Thing sensor_Thing : sensor_Things) 
        {
            for (Sensor sensor : sensor_Thing.sensors) 
            {
                pubSubManager.CreateChannel(sensor.UID, ChannelType.SENSOR);
                Channel sch = pubSubManager.GetChannel_FromName(sensor.UID, ChannelType.SENSOR); 
                
                //concretizations implement Subscriber, so...
                switch (sensor.TYPE)
                {
                    case TEMPERATURE:   ((Sensor_Temperature)   sensor).SubscribeTo(sch); break;
                    case HUMIDITY:      ((Sensor_Humidity)      sensor).SubscribeTo(sch); break;
                    case AIR_QUALITY:   ((Sensor_AirQuality)    sensor).SubscribeTo(sch); break;
                    case SMOKE:         ((Sensor_Smoke)         sensor).SubscribeTo(sch); break;
                    case MOTION:        ((Sensor_Motion)        sensor).SubscribeTo(sch); break;
                    case FLOOD:         ((Sensor_Flood)         sensor).SubscribeTo(sch); break;
                    /*
                    case PRESENCE:      ((Sensor_Presence)      sensor).SubscribeTo(sch); break; 
                    case CAMERA:        ((Sensor_Camera)         sensor).SubscribeTo(sch); break;
                    */
                    default: break;
                }
            }    
        }

        /* **** TEST: start sensors and their refresh job ****
        */
        for (Sensor_Thing sensor_Thing : sensor_Things) 
        {
            sensor_Thing.Start();    
        }
        
        Log.getInstance().Write("READY.");
        
        /* **** TEST: PubNub messaging test             
        
        PubNub_Instance pubnub = PubNub_Instance.getInstance();
        Map<String,String> messageMap = new HashMap<String, String>(); 

        messageMap.put("key0", "value0"); 
        messageMap.put("key1", "value1"); 
        messageMap.put("key2", "value2"); 
        messageMap.put("key3", "value3"); 

        pubnub.Publish_PubNubMessage(messageMap); 
        //System.out.println("MESSAGE SENT.");
        */
        
        /* **** TEST: create a house with some rooms ****
        List<Room> rooms = new ArrayList<Room>(); 
        
        rooms.add(new Room("Living Room", 2)); 
        rooms.add(new Room("Bedroom", 1)); 
        rooms.add(new Room("Bathroom",1)); 
        rooms.add(new Room("Kitchen", 1)); 
        rooms.add(new Room("Corridor", 1)); 

        */

        /* **** TEST: add a temp sensor in each room (in each sensor real object, actually) ****
        rooms.get(0).sensors.get(0).Add_Sensor(DataType.TEMPERATURE);
 //       rooms.get(0).sensors.get(1).Add_Sensor(DataType.AIR_QUALITY);
 //       rooms.get(1).sensors.get(0).Add_Sensor(DataType.HUMIDITY);
 //       rooms.get(2).sensors.get(0).Add_Sensor(DataType.AIR_QUALITY);
        rooms.get(3).sensors.get(0).Add_Sensor(DataType.TEMPERATURE);
 //       rooms.get(4).sensors.get(0).Add_Sensor(DataType.SMOKE);

        //TEST: channel creation, for each sensor create a SensorChannel and for each room a RoomChannel
        PubSubManager pubSubManager = PubSubManager.getInstance(); 
        pubSubManager.CreateChannel("ALARM", ChannelType.ALARM);

        */

        /*
        for (Room room : rooms) 
        {
            for (Sensor_Thing sensor_ROb : room.sensors)
            {
                for (Sensor sensor : sensor_ROb.sensors)
                {
                    pubSubManager.CreateChannel(sensor.UID, ChannelType.SENSOR);
                    Channel sch = pubSubManager.GetChannel_FromName(sensor.UID, ChannelType.SENSOR); 

                    //concretizations implement Subscriber, so...
                    switch (sensor.TYPE)
                    {
                        case TEMPERATURE:   ((Sensor_Temperature)   sensor).SubscribeTo(sch); break;
                        case HUMIDITY:      ((Sensor_Humidity)      sensor).SubscribeTo(sch); break;
                        case AIR_QUALITY:   ((Sensor_AirQuality)    sensor).SubscribeTo(sch); break;
                        case SMOKE:         ((Sensor_Smoke)         sensor).SubscribeTo(sch); break;
                        case MOTION:        ((Sensor_Motion)        sensor).SubscribeTo(sch); break;
                        /*
                        case PRESENCE:      ((Sensor_Presence)      sensor).SubscribeTo(sch); break; 
                        case CAMERA:        ((Sensor_Camera)         sensor).SubscribeTo(sch); break;
                        //
                        default: break;
                    }

                    
                }

            }
        }
        
        
        //room list gets COPIED into the object's value, so we can dispose of this.rooms if we wanted
        //Measurer.getInstance().OperateIn_House(house);
        //house.Start_SensorRObs();

        */

        /* **** VARIATE DATA TEST ****
        //TEST: decrease temperature in room 0 (Living Room), decrement level = 2 / 3  (e.g. window open in winter) to a target temp of 18.00°C
        Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
        Message message = new Message(event.TYPE, DataType.TEMPERATURE, VariationType.INCREASE, 25.00f); 
        Channel channel = pubSubManager.GetChannel_FromName(rooms.get(0).NAME); 
        event.PublishTo(channel, message);

        event = new Event(EventType.VARIATE_DATA_REQUEST); 
        message = new Message(event.TYPE, DataType.TEMPERATURE, VariationType.DECREASE_FASTEST, 18.00f); 
        channel = pubSubManager.GetChannel_FromName(rooms.get(3).NAME); 
        event.PublishTo(channel, message);
        */

        /* **** BATTERY TEST ****
        System.out.println("TEST: battery");
        Battery b = new Battery(50, 100); 

        System.out.println("TEST: battery created. " + b.percentage +"%");

        b.StartDischarging();

        System.out.println("TEST: battery started discharging. ");
        
        while (true)
        { 
            //funziona ma solo se non lo lascio andare in automatico... se lo interrompo ad esempio con un read va ???
            System.in.read();
            //System.out.println("TEST: " + b.batteryStatus + " -- " + b.percentage + "%" );
            if ( (b.percentage < 40f) )
            {

                System.out.println("TEST: battery started charging. ");
                b.StartCharging();
            }

            if ( (b.percentage > 70f))
            {
                System.out.println("TEST: battery stopped charging and started discharging. ");
                b.StartDischarging();
            }
            //prova a vedere se puoi stoppare la carica e rimetterla tipo al 50%
        }
        */
    }
}