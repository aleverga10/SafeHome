package safehome_ss.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import safehome_ss.sensors.Data.DataType;

public class GUI_SensorSceneController 
{
    String sensorThingUID;
    String sensorsUIDs[]; 

    @FXML
    AnchorPane sensor_rootPane;

    // some of these might be disabled, depending on the model of the sensorthing
    @FXML
    JFXButton sensorTemp_btn, sensorHumd_btn, sensorAirq_btn, sensorSmok_btn, sensorMotn_btn, sensorFlod_btn;

    public ArrayList<JFXButton> btns;
    String[] sensorTypes = {
        DataType.TEMPERATURE.name(), 
        DataType.HUMIDITY.name(), 
        DataType.AIR_QUALITY.name(), 
        DataType.SMOKE.name(), 
        DataType.MOTION.name(), 
        DataType.FLOOD.name()
    }; 

    private int currentSensor;

    private GUI_Loader loader;

    

    public void initialize() 
    {
        loader = GUI_Loader.getInstance();

        // keep track of the toggle buttons, we need them to work properly with navigational arrows
        btns = new ArrayList<>();
        addTbtns();

        // set the correct typed image for each button
        for (JFXButton tbtn : btns) 
        {
            setTButtonsImages(tbtn);    
        }

        currentSensor = -1; 
    }

    private void setTButtonsImages(JFXButton button)
    {
        File url;  
        Image image; 
        String type = ""; 

        // for each type assign the right image resource
        if (button.equals(sensorTemp_btn))
            type = DataType.TEMPERATURE.name(); 
        else if (button.equals(sensorHumd_btn))
            type = DataType.HUMIDITY.name(); 
        else if (button.equals(sensorAirq_btn))
            type = DataType.AIR_QUALITY.name(); 
        else if (button.equals(sensorSmok_btn))
            type = DataType.SMOKE.name(); 
        else if (button.equals(sensorMotn_btn))
            type = DataType.MOTION.name(); 
        else if (button.equals(sensorFlod_btn))
            type = DataType.FLOOD.name(); 
        
        url = new File(GUI_Helper.ICONS.get(type));
        image = new Image(url.toURI().toString());
        ImageView iv = new ImageView(image);
        iv.setFitWidth(32); 
        iv.setFitHeight(32);
        button.setGraphic(iv);
    }

   

    private void addTbtns()
    {
        btns.add(sensorTemp_btn); // 0
        btns.add(sensorHumd_btn); // 1
        btns.add(sensorAirq_btn); // 2
        btns.add(sensorSmok_btn); // 3
        btns.add(sensorMotn_btn); // 4
        btns.add(sensorFlod_btn); // 5
    }

    public void loadSensor(int n, String type) 
    {
        GUI_SensorController controller = (GUI_SensorController) loader.loadScene(GUI_Helper.PAGE_SENSOR, sensor_rootPane);
        controller.dataType = type;
    
        // set background color for selected item only
        for (JFXButton btn : btns) {
            btn.setStyle("-fx-background-color:#f5f5f5");
        }
        btns.get(currentSensor).setStyle("-fx-background-color:#ffcc88");


        // fill data with sensor specific model
        HashMap<String, String> values = (HashMap<String,String>) loader.loadModelData(GUI_Helper.PAGE_SENSOR, sensorThingUID, type);

        // tell the helper this sensor has this UID, to uniquely identify its controller 
        controller.sensorUID = values.get("sensorUID"); 
        GUI_Helper.getInstance().controllers_Sensor.put(controller.sensorUID, controller);

        // add this gui-only value
        //values.put("sensorThingName", this.sensorThingUID); 

        // add the rest of the values
        for (Entry<String,String> value : values.entrySet()) 
        {
            controller.setField(value.getKey(), value.getValue());
            // this ^ returns a boolean, true when something was updated, if you want to log or whatever
        }

    }

    public void prevSensor()
    {     
        nextOrPrevSensor(-1);
    }

    public void nextSensor()
    {
        nextOrPrevSensor(+1);
    }

    private void nextOrPrevSensor(int direction)
    {
        boolean ok = false; 
        while (!ok) 
        {
            currentSensor += 1 * direction; 

            // boundary checks
            if (currentSensor < 0)
                currentSensor = sensorTypes.length -1;

            else if (currentSensor > sensorTypes.length -1)
                currentSensor = 0; 

            // check the current sensor isn't disable, and keep going only if it is disabled
            if (btns.get(currentSensor).isDisable() == false)
                ok = true;
        }
    
        loadSensor(currentSensor, sensorTypes[currentSensor]);
    }

    public void showSensorTemp()
    {
        int index = btns.indexOf(sensorTemp_btn);
        if (currentSensor != index)
        {
            currentSensor = index;
            loadSensor(currentSensor, DataType.TEMPERATURE.name());
        }
        else
            sensorTemp_btn.setStyle("-fx-background-color:#ffcc88");
    }

    public void showSensorHumd()
    {
        int index = btns.indexOf(sensorHumd_btn);
        if (currentSensor != index)
        {
            currentSensor = index;
            loadSensor(currentSensor, DataType.HUMIDITY.name()); 
        }
        else
            sensorHumd_btn.setStyle("-fx-background-color:#ffcc88");
    }

    public void showSensorAirq()
    {
        int index = btns.indexOf(sensorAirq_btn);
        if (currentSensor != index)
        {
            currentSensor = index;
            loadSensor(currentSensor, DataType.AIR_QUALITY.name()); 
        }  
        else
            sensorAirq_btn.setStyle("-fx-background-color:#ffcc88");
    }

    public void showSensorSmok()
    {
        int index = btns.indexOf(sensorSmok_btn);
        if (currentSensor != index)
        {
            currentSensor = index;
            loadSensor(currentSensor, DataType.SMOKE.name()); 
        }
        else
            sensorSmok_btn.setStyle("-fx-background-color:#ffcc88"); 
    }

    public void showSensorMotn()
    {
        int index = btns.indexOf(sensorMotn_btn);
        if (currentSensor != index)
        {
            currentSensor = index;
            loadSensor(currentSensor, DataType.MOTION.name()); 
        }
        else
            sensorMotn_btn.setStyle("-fx-background-color:#ffcc88");
    }

    public void showSensorFlod()
    {
        int index = btns.indexOf(sensorFlod_btn);
        if (currentSensor != index)
        {
            currentSensor = index;
            loadSensor(currentSensor, DataType.FLOOD.name()); 
        }
        else
            sensorFlod_btn.setStyle("-fx-background-color:#ffcc88");
       
    }

}