package safehome_ss.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import safehome_ss.sensors.Data.DataType;

import java.util.AbstractMap.SimpleEntry;

public class GUI_Helper 
{
    // constants, paths and things
    public static final String SENSORTHINGS_FILEPATH = "src/main/java/safehome_ss/local/devices/"; 
    public static final String GUI_PATH = "src/main/java/safehome_ss/gui/";
    public static final String ICONS_PATH ="src/main/java/safehome_ss/gui/icons/";

    public static final String PAGE_HOME = "HOME";
    public static final String PAGE_MENU = "MENU";
    public static final String PAGE_ADD = "ADD";
    public static final String PAGE_LOG = "LOG";
    public static final String PAGE_SENSORTHINGS_SCENE = "SENSORTHINGS_SCENE";
    public static final String PAGE_SENSORTHING = "SENSORTHING";
    public static final String PAGE_SENSORTHING_MODIFY = "SENSORTHING_MODIFY";
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
            new SimpleEntry<>(PAGE_SENSORTHINGS_SCENE, GUI_PATH + "sensorthingsScene.fxml"),
            new SimpleEntry<>(PAGE_SENSORTHING, GUI_PATH + "sensorthing.fxml"),
            new SimpleEntry<>(PAGE_SENSORTHING_MODIFY, GUI_PATH + "sensorthing_modify.fxml"),
            new SimpleEntry<>(PAGE_SCENARIOSCENE, GUI_PATH + "scenariosScene.fxml")

    ).collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));


    // icon paths
    public static final Map<String, String> ICONS = Stream.of(

            new SimpleEntry<>(DataType.TEMPERATURE.name(), ICONS_PATH + "temperature.png"), 
            new SimpleEntry<>(DataType.HUMIDITY.name(), ICONS_PATH + "humidity.png"),
            new SimpleEntry<>(DataType.AIR_QUALITY.name(), ICONS_PATH + "flow.png"),
            new SimpleEntry<>(DataType.SMOKE.name(), ICONS_PATH + "smoke.png"),
            new SimpleEntry<>(DataType.MOTION.name(), ICONS_PATH + "motion.png"),
            new SimpleEntry<>(DataType.FLOOD.name(), ICONS_PATH + "water.png"),
            new SimpleEntry<>("THING", ICONS_PATH + "camera.png")

    ).collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));


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
        SensorID_ToThingID = new HashMap<>(); 
        controllers_SensorThing = new HashMap<>(); 
        controllers_Sensor = new HashMap<>(); 
    }

    // we can access controllers with these references
    // they are set when the respective fxml is loaded by the FXMLLoader
    
    public GUI_SensorSceneController controller_SensorScene;
    public GUI_LogController controller_Log;
    
    // this is a helper map for ids, which maps sensor ids to their sensor thing (device) ids they are built in (sensor "TEMP_10" -> sensorthing "TH_005")
    // because we sometimes will need to show them and in the model there isn't this data
    public Map<String, String> SensorID_ToThingID; 

    public Map<String, GUI_SensorController> controllers_Sensor; 
    public Map<String, GUI_SensorThingController> controllers_SensorThing;
}