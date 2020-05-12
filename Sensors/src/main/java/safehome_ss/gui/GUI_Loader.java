package safehome_ss.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import safehome_ss.sensors.Sensor;
import safehome_ss.sensors.Sensor_Thing;
import safehome_ss.sensors.Data.DataType;

public class GUI_Loader {
    // Singleton pattern
    private static GUI_Loader instance = null;

    public static GUI_Loader getInstance() {
        if (instance == null)
            instance = new GUI_Loader();

        return instance;
    }

    public AnchorPane main_rootPane;
    public Stage mainStage;

    public Object loadScene(String scene, Pane viewPane) {
        try {
            URL url = new File(getScenePath(scene)).toURI().toURL();
            FXMLLoader fxmlLoader = new FXMLLoader(url);
            Node node = fxmlLoader.load();

            // when we get here, initialize() method has been called,
            // so every value should have been be correctly retrieved from model files
            // and filled into labels or whatever
            Object controller = setController(scene, fxmlLoader);
            viewPane.getChildren().setAll(node);

            return controller;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void loadMainScene() throws IOException
    {
        GUI_Loader loader = GUI_Loader.getInstance();
        URL url = new File(loader.getScenePath(GUI_Helper.PAGE_HOME)).toURI().toURL(); 
        FXMLLoader fxmlLoader = new FXMLLoader(url); 
        //Parent root = FXMLLoader.load(url);
        Parent root = fxmlLoader.load();

        //GUI_Helper.getInstance().controller_Main = (GUI_MainController) fxmlLoader.getController();
        //GUI_Helper.getInstance().controller_Main.showSideMenu();

        mainStage.setTitle("Safehome: Sensors");
        Scene scene = new Scene(root); 
        mainStage.setScene(scene);
        //loader.mainScene = scene;
        //primaryStage.setScene(new Scene(new AnchorPane()));
        mainStage.show();
    }

    public String getScenePath(String scene) 
    {
        return  GUI_Helper.PATHS.get(scene.toUpperCase());
    }

    private Object setController(String scene, FXMLLoader loader) 
    {
        switch (scene) 
        {
            case GUI_Helper.PAGE_LOG: 
                return GUI_Helper.getInstance().controller_Log = (GUI_LogController) loader.getController(); 
                
            case GUI_Helper.PAGE_SENSORSCENE:
                return GUI_Helper.getInstance().controller_SensorScene = (GUI_SensorSceneController) loader.getController();

            
            //case GUI_Helper.PAGE_SENSORTHINGS_SCENE:
            //    return GUI_Helper.getInstance().controller_SensorThingsScene = (GUI_SensorThingSceneController) loader.getController();

            case GUI_Helper.PAGE_SENSORTHING:
                // this gets saved later in the caller method (sensorthing_scene.loadsensor), because we need to set the sensorThing id
                return (GUI_SensorThingController) loader.getController();

            case GUI_Helper.PAGE_SENSOR:
                // this gets saved later in the caller method (sensor_scene.loadsensor), because we need to set the sensor id
                return (GUI_SensorController) loader.getController();

            default:
                return null; 
        }
    }

    public Map<String, String> loadModelData(String scene, String sensorThingUID, String sensorType) 
    {
        Map<String, String> values = new LinkedHashMap<>(); 

        ModelDataLoader loader = ModelDataLoader.getInstance(); 

        if (scene == GUI_Helper.PAGE_SENSORTHING)
        {
            Sensor_Thing thing = loader.sensorThings.get(sensorThingUID);
            values.put("sensorThingUID", thing.uid); 
            values.put("sensorThingName", thing.name);
            values.put("sensorThingModel", thing.model);
            values.put("sensorThingPlacedIn", thing.isPlacedIn);
        }

        else if (scene == GUI_Helper.PAGE_SENSOR)
        {
            for (Sensor sensor : loader.sensorThings.get(sensorThingUID).sensors)
            {
                if (sensor.TYPE.name().equals(sensorType))
                {
                    values.put("sensorUID", sensor.UID);
                    String thingUid = GUI_Helper.getInstance().SensorID_ToThingID.get(sensor.UID);
                    String name = GUI_Helper.getInstance().controllers_SensorThing.get(thingUid).getThingName();
                    values.put("sensorThingName", name); 
                    values.put("sensorName", sensor.NAME);
                    values.put("sensorType", sensor.type_asShown);
                    values.put("sensorStatus", sensor.status.toString());
                    values.put("refreshTime", String.valueOf(sensor.refreshTime));
                    values.put("measuredData", sensor.measuredData.value);
                }
            }
        }

        return values; 
    }
}