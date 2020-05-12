package safehome_ss.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import safehome_ss.sensors.Sensor;
import safehome_ss.sensors.Data.DataType;
import safehome_ss.sensors.Sensor.SensorStatus;
import safehome_ss.sensors.Sensor_Thing.Sensor_Thing_Status;

public class GUI_SensorThingController implements GUI_Controller 
{
    public String sensorThing_UID; 

    // valid model names are a combination of these basic sensors names (e.g. "THA", "THS", "TSM", "T", "HSM", "THASM", ...)
    // basic sensors names (T = temperature only, H = humidity only, ...)
    private final char MODEL_T = 'T'; 
    private final char MODEL_H = 'H'; 
    private final char MODEL_A = 'A'; 
    private final char MODEL_S = 'S'; 
    private final char MODEL_M = 'M';
    private final char MODEL_F = 'F'; 

    @FXML
    private ImageView sensorThingImage;

    @FXML
    private Label sensorThingName, sensorThingModel, sensorThingUID, sensorThingPlacedIn;

    @FXML
    private JFXButton show_TempSensor, show_HumdSensor, show_AirqSensor, show_SmokSensor, show_MotnSensor,
            show_FlodSensor;

    @FXML
    private JFXToggleButton sensorThingPower_Tbtn;

    @FXML
    private Label label_TempSensor, label_HumdSensor, label_AirqSensor, label_SmokSensor, label_MotnSensor, label_FlodSensor;

    ArrayList<JFXButton> showButtons; 
    ArrayList<Label> labels; 

    @FXML
    private JFXButton showScene_modifySensorThing;

    GUI_Loader loader;

    public void showScene()
    {
        // load model values from the model to a map like (fx_id -> value)
        loader = GUI_Loader.getInstance();
        HashMap<String, String> values = (HashMap<String, String>) loader.loadModelData(GUI_Helper.PAGE_SENSORTHING, sensorThing_UID, null);

        // disable all jfx "show sensor" buttons by default, we'll enable them once we parse the model value
        // we also will need this array
        showButtons = new ArrayList<>(); 
        showButtons.add(show_TempSensor); 
        showButtons.add(show_HumdSensor); 
        showButtons.add(show_AirqSensor); 
        showButtons.add(show_MotnSensor); 
        showButtons.add(show_SmokSensor); 
        showButtons.add(show_FlodSensor); 
        
        for (JFXButton jfxButton : showButtons) {
            jfxButton.setDisable(true);    
            setButtonsImages(jfxButton);
        }

        // same thing for their shown labels
        labels = new ArrayList<>(); 
        labels.add(label_TempSensor); 
        labels.add(label_HumdSensor);
        labels.add(label_AirqSensor);
        labels.add(label_MotnSensor);
        labels.add(label_SmokSensor);
        labels.add(label_FlodSensor);
        
        for (Label label : labels)
        {
            label.setDisable(true);
        }

        // for each value set the corresponding gui label value
        // we also parse the model value here, so we will enable some buttons
        for (Entry<String, String> value : values.entrySet()) 
        {
            setField(value.getKey(), value.getValue());
            // this ^ returns a boolean, true when something was updated, if you want to log or whatever
        }

        // assign power togglebutton value to this thing's model status
        setPowerTbtn(ModelDataLoader.getInstance().sensorThings.get(this.sensorThing_UID).status);
    }

    private void setButtonsImages(JFXButton button)
    {
        File url;  
        Image image; 
        String type = ""; 

        // for each type assign the right image resource
        if (button.equals(show_TempSensor))
            type = DataType.TEMPERATURE.name(); 
        else if (button.equals(show_HumdSensor))
            type = DataType.HUMIDITY.name(); 
        else if (button.equals(show_AirqSensor))
            type = DataType.AIR_QUALITY.name(); 
        else if (button.equals(show_SmokSensor))
            type = DataType.SMOKE.name(); 
        else if (button.equals(show_MotnSensor))
            type = DataType.MOTION.name(); 
        else if (button.equals(show_FlodSensor))
            type = DataType.FLOOD.name(); 
        
        url = new File(GUI_Helper.ICONS.get(type));
        image = new Image(url.toURI().toString());
        ImageView iv = new ImageView(image);
        iv.setFitWidth(32); 
        iv.setFitHeight(32);
        button.setGraphic(iv);
    }

    public String getSensorThingUID()
    {
        return this.sensorThingUID.getText(); 
    }

    private void loadSensorScene() 
    {
        GUI_Loader loader = GUI_Loader.getInstance();
        GUI_SensorSceneController controller = (GUI_SensorSceneController) loader.loadScene(GUI_Helper.PAGE_SENSORSCENE, loader.main_rootPane);
        
        // tell the controller of the sensor scene this.ID, in order to show the supported sensors' data only
        controller.sensorThingUID = this.sensorThing_UID;
        
        // disable the same sensor buttons which are disabled in this scene
        for (JFXButton jfxButton : showButtons) 
        {
            if (jfxButton.isDisable())
                controller.btns.get(showButtons.indexOf(jfxButton)).setDisable(true);
        }

    }

    public void show_TemperatureData() 
    {
        loadSensorScene();
        GUI_Helper.getInstance().controller_SensorScene.showSensorTemp();
    }

    public void show_HumidityData() 
    {
        loadSensorScene();
        GUI_Helper.getInstance().controller_SensorScene.showSensorHumd();
    }

    public void show_AirQualityData() 
    {
        loadSensorScene();
        GUI_Helper.getInstance().controller_SensorScene.showSensorAirq();
    }

    public void show_SmokeData() 
    {
        loadSensorScene();
        GUI_Helper.getInstance().controller_SensorScene.showSensorSmok();
    }

    public void show_MotionData() 
    {
        loadSensorScene();
        GUI_Helper.getInstance().controller_SensorScene.showSensorMotn();
    }

    public void show_FloodData() 
    {
        loadSensorScene();
        GUI_Helper.getInstance().controller_SensorScene.showSensorFlod();
    }

    public void show_ModifySensorThing() 
    {
        GUI_Loader loader = GUI_Loader.getInstance();
        loader.loadScene(GUI_Helper.PAGE_SENSORTHING_MODIFY, loader.main_rootPane);
    }

    @Override
    public boolean setField(String field_FxId, String value) 
    {
        // can't do a switch-case because they need to be constants...
/*
        if (field_FxId.startsWith("sensorUID_")) 
        {
            // this helper map associates sensors contained inside this Thing with this ThingID itself
            GUI_Helper.getInstance().SensorID_ToThingID.put(value, getSensorThingUID()); 
        }
*/
        // from here on we set labels and other graphical things 

        if (field_FxId.equals(sensorThingUID.getId()))
        {
            sensorThingUID.setText(value);
            return true; 
        }

        if (field_FxId.equals(sensorThingName.getId()))
        {
            sensorThingName.setText(value);
            return true; 
        }

        if (field_FxId.equals(sensorThingModel.getId()))
        {
            sensorThingModel.setText(value);

            // disable some JFXButtons if this sensor does not support that type of measures
            for (char c : value.toCharArray()) 
            {
                switch(c)
                {
                    case MODEL_T: show_TempSensor.setDisable(false); label_TempSensor.setDisable(false); break;
                    case MODEL_H: show_HumdSensor.setDisable(false); label_HumdSensor.setDisable(false); break;
                    case MODEL_A: show_AirqSensor.setDisable(false); label_AirqSensor.setDisable(false); break;
                    case MODEL_S: show_SmokSensor.setDisable(false); label_SmokSensor.setDisable(false); break;
                    case MODEL_M: show_MotnSensor.setDisable(false); label_MotnSensor.setDisable(false); break;
                    case MODEL_F: show_FlodSensor.setDisable(false); label_FlodSensor.setDisable(false); break;
                    default: break; 
                }
            }
            return true; 
        }

        if (field_FxId.equals(sensorThingImage.getId()))
        {
            //sensorThingImage.setImage(arg0);
            return true; 
        }

        if (field_FxId.equals(sensorThingPlacedIn.getId()))
        {
            sensorThingPlacedIn.setText(value);
            return true; 
        }

        return false;
    }

    public String getThingName()
    {
        return sensorThingName.getText();
    }

    public void power()
    {
        // turn OFFLINE all sensors, plus this thing
        if (sensorThingPower_Tbtn.isSelected() == false)
            ModelDataLoader.getInstance().sensorThings.get(this.sensorThing_UID).Shutdown();

        // turn ONLINE all sensors, plus this thing
        else
           ModelDataLoader.getInstance().sensorThings.get(this.sensorThing_UID).Start();
        
        setPowerTbtn(ModelDataLoader.getInstance().sensorThings.get(this.sensorThing_UID).status);
    }

    private void setPowerTbtn(Sensor_Thing_Status value)
    {
        if (value == Sensor_Thing_Status.ON)
            sensorThingPower_Tbtn.setSelected(true);
        else
            sensorThingPower_Tbtn.setSelected(false);
    }
}