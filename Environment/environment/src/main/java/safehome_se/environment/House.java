package safehome_se.environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import safehome_se.environment.Data.DataType;

public class House 
{
    public List<Room> rooms; 
    public Map<String, String> uidToRooms; 
   
    public House()
    {
        this.rooms = new ArrayList<Room>(); 
        this.uidToRooms = new HashMap<String, String>(); 
    }

    public House(List<Room> rooms)
    {
        this.rooms = new ArrayList<Room>(); 
        this.rooms.addAll(rooms); 
        this.uidToRooms = new HashMap<String, String>(); 
    }

    public void addRoom(Room room)
    {
        this.rooms.add(room); 
    }

    public String GetUID_FromRoom(String roomName, DataType sensorType)
    {
        String prefix = ""; 
        switch (sensorType)
        {
            case TEMPERATURE:   prefix = "TEMP"; break;
            case HUMIDITY:      prefix = "HUMD"; break;
            case AIR_QUALITY:   prefix = "AIRQ"; break;
            case SMOKE:         prefix = "SMOK"; break;
            case MOTION:        prefix = "MOTN"; break;
            case FLOOD:         prefix = "FLOD"; break;
            default: break;
            
        }
     

        for(Map.Entry<String, String> entry : uidToRooms.entrySet())
        {
            if (entry.getValue() == roomName)
                if (entry.getKey().startsWith(prefix))
                    return entry.getKey(); 
        }

        return null; 
    }

    public Room getRoom_fromRoomName(String roomName)
    {
        for (Room room : rooms) {
            if (room.NAME.toUpperCase().equals(roomName.toUpperCase()))
                return room; 
        }

        return null; 
    }
}