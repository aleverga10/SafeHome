package safehome_se.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import safehome_se.environment.Data.DataType;
import safehome_se.pubsub.Message.VariationType;

import java.util.AbstractMap.SimpleEntry;

public class GUI_Helper 
{
    // constants, paths and things
    public static final String ROOMS_FILEPATH = "environment/src/main/java/safehome_se/local/rooms/"; 
    public static final String GUI_PATH = "environment/src/main/java/safehome_se/gui/";

    public static final String PAGE_HOME = "HOME";
    public static final String PAGE_MENU = "MENU";
    public static final String PAGE_ADD = "ADD";
    public static final String PAGE_LOG = "LOG";
    public static final String PAGE_ROOMSSCENE = "ROOMS_SCENE";
    public static final String PAGE_ROOM = "ROOM";
    public static final String PAGE_VARIATE_DATA = "VARIATE_DATA";
    public static final String PAGE_SENSORSCENE = "SENSORSCENE";
    public static final String PAGE_SENSOR = "SENSOR";
    public static final String PAGE_SCENARIOSCENE = "SCENARIOSCENE";

    public static final Map<String, String> PATHS = Stream.of(

            new SimpleEntry<>(PAGE_HOME, GUI_PATH + "main.fxml"), 
            new SimpleEntry<>(PAGE_MENU, GUI_PATH + "menu.fxml"),
            new SimpleEntry<>(PAGE_ADD, GUI_PATH + "sensorthing_modify.fxml"),
            new SimpleEntry<>(PAGE_LOG, GUI_PATH + "log.fxml"),
            new SimpleEntry<>(PAGE_SENSORSCENE, GUI_PATH + "sensorScene.fxml"),
            new SimpleEntry<>(PAGE_SENSOR, GUI_PATH + "sensor.fxml"),
            new SimpleEntry<>(PAGE_ROOMSSCENE, GUI_PATH + "roomsScene.fxml"),
            new SimpleEntry<>(PAGE_ROOM, GUI_PATH + "room.fxml"),
            new SimpleEntry<>(PAGE_VARIATE_DATA, GUI_PATH + "variateData.fxml"),
            new SimpleEntry<>(PAGE_SCENARIOSCENE, GUI_PATH + "scenariosScene.fxml")

    ).collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));


    // shown strings

    // data types
    public static final String shownType_Temp = "Temperature"; 
    public static final String shownType_Humd = "Humidity"; 
    public static final String shownType_Airq = "Air quality"; 
    public static final String shownType_Smok = "Smoke detector"; 
    public static final String shownType_Motn = "Motion detector";
    public static final String shownType_Flod = "Water leakage";  

    // airq params
    public static final String shownParam_CO2 = "CO2"; 
    public static final String shownParam_CO = "CO"; 
    public static final String shownParam_PM25 = "PM 2.5"; 
    public static final String shownParam_TVOC = "TVOC"; 

    // variation values
    public static final String shownVariation_Decrease = "DECREASE"; 
    public static final String shownVariation_Increase = "INCREASE"; 
    public static final String shownVariation_None = "NO VARIATION"; 
    public static final String shownVariation_Level1 = ""; 
    public static final String shownVariation_Level2 = "FAST"; 
    public static final String shownVariation_Level3 = "VERY FAST"; 
    public static final String shownVariation_Trigger = "DETECTED"; 
    public static final String[] shownVariationValues = 
    {
        shownVariation_Decrease + " " + shownVariation_Level3, 
        shownVariation_Decrease + " " + shownVariation_Level2, 
        shownVariation_Decrease + " " + shownVariation_Level1, 
        shownVariation_None, 
        shownVariation_Increase + " " + shownVariation_Level1, 
        shownVariation_Increase + " " + shownVariation_Level2,
        shownVariation_Increase + " " + shownVariation_Level3,
        shownVariation_Trigger 
    }; 



    //Singleton pattern
    private static GUI_Helper instance = null; 
    public static GUI_Helper getInstance()
    {
        if (instance == null)
            instance = new GUI_Helper();

        return instance; 
    }

    private GUI_Helper()
    {
//        SensorID_ToThingID = new HashMap<>(); 
        controllers_Room = new HashMap<>(); 
//        controllers_Sensor = new HashMap<>(); 
    }


    public DataType getDataType_fromShownType(String shownType)
    {
        switch (shownType)
        {
            case shownType_Temp: return DataType.TEMPERATURE; 
            case shownType_Humd: return DataType.HUMIDITY; 
            case shownType_Airq: return DataType.AIR_QUALITY; 
            case shownType_Smok: return DataType.SMOKE; 
            case shownType_Motn: return DataType.MOTION;
            case shownType_Flod: return DataType.FLOOD; 
            default:             return null; 
        }
    }

    public String getShownType_fromDataType(DataType type)
    {
        switch (type)
        {
            case TEMPERATURE:   return shownType_Temp; 
            case HUMIDITY:      return shownType_Humd; 
            case AIR_QUALITY:   return shownType_Airq;
            case SMOKE:         return shownType_Smok; 
            case MOTION:        return shownType_Motn;
            case FLOOD:         return shownType_Flod; 
            default:            return ""; 
        }
    }

    public String getShownType_fromAirqParam(String param)
    {
        switch (param)
        {
            case "CO2":     return shownParam_CO2; 
            case "CO":      return shownParam_CO; 
            case "PM2.5":   return shownParam_PM25; 
            case "TVOC":    return shownParam_TVOC; 
            default:        return ""; 
        }
    }

    public String getAirqParam_fromShownString(String shown)
    {
        switch (shown)
        {
            case shownParam_CO2:    return "CO2"; 
            case shownParam_CO:    return "CO"; 
            case shownParam_PM25:    return "PM2.5"; 
            case shownParam_TVOC:    return "TVOC"; 
            default:                return "";
        }
    }

    public String getVariationValue(int index)
    {
        switch (index)
        {
            case -3: return VariationType.DECREASE_FASTEST.name(); 
            case -2: return VariationType.DECREASE_FASTER.name(); 
            case -1: return VariationType.DECREASE.name(); 

            case +1: return VariationType.INCREASE.name(); 
            case +2: return VariationType.INCREASE_FASTER.name(); 
            case +3: return VariationType.INCREASE_FASTEST.name(); 

            default: return "NONE"; 
        }
    }

    public String getVariationValue_asShownString(VariationType vType)
    {
        switch (vType)
        {
            case DECREASE_FASTEST:  return shownVariationValues[0]; 
            case DECREASE_FASTER:   return shownVariationValues[1];
            case DECREASE:          return shownVariationValues[2];
            case INCREASE:          return shownVariationValues[4];
            case INCREASE_FASTER:   return shownVariationValues[5];
            case INCREASE_FASTEST:  return shownVariationValues[6];
            case TRIGGER:           return shownVariationValues[7];
            default:                return shownVariationValues[3];
        }
    }

    // we can access controllers with these references
    // they are set when the respective fxml is loaded by the FXMLLoader
    public GUI_LogController controller_Log;  
    public Map<String, GUI_RoomController> controllers_Room; 
}