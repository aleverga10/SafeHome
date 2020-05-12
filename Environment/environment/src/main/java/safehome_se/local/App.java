package safehome_se.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import safehome_se.communication.PubNub_Instance;
import safehome_se.environment.Data;
import safehome_se.environment.House;
import safehome_se.environment.Room;
import safehome_se.environment.Data.DataType;
import safehome_se.environment.Room.RoomType;
import safehome_se.gui.ModelDataLoader;
import safehome_se.pubsub.Channel;
import safehome_se.pubsub.Event;
import safehome_se.pubsub.Log;
import safehome_se.pubsub.Message;
import safehome_se.pubsub.PubSubManager;
import safehome_se.pubsub.Channel.ChannelType;
import safehome_se.pubsub.Event.EventType;
import safehome_se.pubsub.Message.VariationType;

/**
 * test
 */
public final class App 
{

    public static void main(String[] args) 
    {
        //test(); 

        // non avevo voglia di farlo a mano

        // testing scenario creation by random numbers (once)
        // result: 
        Random RNG = new Random(); 

        DataType type; 
        VariationType speed;
        Data currentData; 
        Float currentValue; 
        Float variationPercent;
        Float targetValue = 0f; 
        Float motionDuration = null;
        int maxDuration = 10;
        String airqParam = "";
        String roomName; 
        int seconds = 0; 

        Room room1 = new Room("LivingRoom"); 
        room1.Init_RealData(true);
        Room room2 = new Room("Bathroom");
        room2.Init_RealData(true);
        List<Room> rooms = new ArrayList<>();
        rooms.add(room1); 
        rooms.add(room2); 


        for (int i = 0; i < 50; i++)
        {
            airqParam = "";
            motionDuration = null; 

            seconds += RNG.nextInt(maxDuration); // max 20 seconds from one variation to another
            type = DataType.values()[ RNG.nextInt(6) ]; // yields [0..5], 6 types

            int roomN = RNG.nextInt(2);
            roomName = rooms.get( roomN ).NAME; // 2 rooms
            speed = VariationType.values() [ (Math.abs(RNG.nextInt(9) - 3)) ] ; // 6 types (not considering TRIGGER which is motion only), doubling prob of increase

            variationPercent = RNG.nextFloat(); // yields (0..1) i think

            switch (type)
            {
                case AIR_QUALITY: 
                    int airqParamN = RNG.nextInt(4); 

                    switch (airqParamN)
                    {
                        case 0: airqParam = "CO2"; break;
                        case 1: airqParam = "CO"; break;
                        case 2: airqParam = "PM2.5"; break;
                        case 3: airqParam = "TVOC"; break;
                    }
                    break;
                
                case MOTION: 
                    motionDuration = (float) (int) (RNG.nextFloat() * maxDuration); 
                    speed = VariationType.TRIGGER; 
                    targetValue = RNG.nextFloat(); // works from 0.0 to 0.9999 which is good 
                    break;

                default:
                    break;
            }
            
            System.out.println("AFTER " + seconds + "s || ROOM " + roomN + " " + roomName + " || " + speed + " || " + type + " " + airqParam + " of " + variationPercent*100 + "% " + motionDuration);
        }
    }

    private static void test()
    {
        PubNub_Instance.getInstance(); 

        /* **** TEST: House creation ****
        */        
        List<Room> rooms = new ArrayList<Room>(); 
        rooms.add(new Room("Salotto", RoomType.LIVING_ROOM)); 
        rooms.add(new Room("Cucina", RoomType.KITCHEN)); 
        rooms.add(new Room("Corridoio", RoomType.CORRIDOR)); 
        rooms.add(new Room("Bagno", RoomType.BATHROOM)); 
        rooms.add(new Room("Camera da letto", RoomType.BEDROOM)); 

        // skip neighboring rooms assignment 
        
        House house = new House(rooms); 

        /* **** TEST: room <-> sensor pairs; **** 
        // c'è bisogno di un messaggio PN "INIT" iniziale in cui si specifica per ogni sensore dove è piazzato e cosa ha all'interno
        // forse può venire da openhab ?? noi sappiamo dalla sitemap com'è fatta la casa e dal things quali sensori abbiamo inserito, no?
        */
        Map<String, String> uidToRooms = new HashMap<String, String>(); 
        String[] defaultUIDs = {"TEMP_01", "HUMD_02", "HUMD_03", "AIRQ_04", "SMOK_05", "TEMP_06", "MOTN_07"}; 
        //TH in the kitchen
        uidToRooms.put(defaultUIDs[0], house.rooms.get(1).NAME); //T of TH
        uidToRooms.put(defaultUIDs[1], house.rooms.get(1).NAME); //H of TH
        //HAS in the bathroom
        uidToRooms.put(defaultUIDs[2], house.rooms.get(3).NAME); //H of HAS
        uidToRooms.put(defaultUIDs[3], house.rooms.get(3).NAME); //A of HAS
        uidToRooms.put(defaultUIDs[4], house.rooms.get(3).NAME); //S of HAS
        //TM in the living room
        uidToRooms.put(defaultUIDs[5], house.rooms.get(0).NAME); //T of TM
        uidToRooms.put(defaultUIDs[6], house.rooms.get(0).NAME); //M of TM
        
        for (Map.Entry<String, String> entry : uidToRooms.entrySet()) 
        {
            Log.getInstance().Write("LOG (HOUSE): Placed sensor "+entry.getKey() + " in room "+entry.getValue());    
        }

        // pair sensors uids to the room they're placed in, and tell the Measurer to operate in a house built like this
        // the Measurer is informed because it will forward this info to the PNinstance, who in turn will deliver every measure request 
        // from a specific sensor to the correct room that specific sensor is placed in
        house.uidToRooms.putAll(uidToRooms);
        ModelDataLoader.getInstance().house = house;

        // init all rooms using default values (e.g. "20.00 °C", ...)
        // might want to keep this option true in the release, but might want to change some internal values
        Boolean useDefaultValues = true;
        for (Room room : house.rooms) 
            room.Init_RealData(useDefaultValues); 
        
            
        // channel creation
        PubSubManager pubSubManager = PubSubManager.getInstance(); 
        for (Room room : house.rooms) 
        {    
            pubSubManager.CreateChannel(room.NAME, ChannelType.ROOM);
            room.SubscribeTo(pubSubManager.GetChannel_FromName(room.NAME, ChannelType.ROOM));
        }

        Log.getInstance().Write("READY.");

        /* **** VARIATE DATA TEST ****
        */
        Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
        Message message = new Message(event.TYPE, DataType.TEMPERATURE, VariationType.INCREASE, 25.00f); 
        Channel channel = pubSubManager.GetChannel_FromName(rooms.get(1).NAME, ChannelType.ROOM); 
        event.PublishTo(channel, message);
/*
        event = new Event(EventType.VARIATE_DATA_REQUEST); 
        message = new Message(event.TYPE, DataType.TEMPERATURE, VariationType.DECREASE_FASTEST, 18.00f); 
        channel = pubSubManager.GetChannel_FromName(rooms.get(3).NAME, ChannelType.ROOM); 
        event.PublishTo(channel, message);

        event = new Event(EventType.VARIATE_DATA_REQUEST); 
        message = new Message(event.TYPE, DataType.MOTION, VariationType.TRIGGER, 0.91f, 5f); 
        channel = pubSubManager.GetChannel_FromName(rooms.get(0).NAME, ChannelType.ROOM); 
        event.PublishTo(channel, message);
*/
        event = new Event(EventType.VARIATE_DATA_REQUEST); 
        message = new Message(event.TYPE, DataType.SMOKE, VariationType.INCREASE_FASTEST, 8f); 
        channel = pubSubManager.GetChannel_FromName(rooms.get(3).NAME, ChannelType.ROOM); 
        event.PublishTo(channel, message);
/*
        event = new Event(EventType.VARIATE_DATA_REQUEST); 
        message = new Message(event.TYPE, DataType.HUMIDITY, VariationType.INCREASE, 50f); 
        channel = pubSubManager.GetChannel_FromName(rooms.get(1).NAME, ChannelType.ROOM); 
        event.PublishTo(channel, message);

        event = new Event(EventType.VARIATE_DATA_REQUEST); 
        message = new Message(event.TYPE, DataType.AIR_QUALITY, "CO", VariationType.INCREASE_FASTEST, 8f); 
        channel = pubSubManager.GetChannel_FromName(rooms.get(3).NAME, ChannelType.ROOM); 
        event.PublishTo(channel, message);
*/
        event = new Event(EventType.VARIATE_DATA_REQUEST);
        message = new Message(event.TYPE, DataType.FLOOD, VariationType.INCREASE_FASTER, 2.8f);
        channel = pubSubManager.GetChannel_FromName(rooms.get(1).NAME, ChannelType.ROOM); 
        event.PublishTo(channel, message);
        //*/
    }
}
