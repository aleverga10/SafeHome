package safehome_se.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import safehome_se.environment.Data;
import safehome_se.environment.DataType_Helper;
import safehome_se.environment.Data.DataType;
import safehome_se.pubsub.Channel;
import safehome_se.pubsub.Event;
import safehome_se.pubsub.Log;
import safehome_se.pubsub.Message;
import safehome_se.pubsub.PubSubManager;
import safehome_se.pubsub.Channel.ChannelType;
import safehome_se.pubsub.Event.EventType;
import safehome_se.pubsub.Message.VariationType;

public class GUI_VariateDataController implements GUI_Controller
{
    @FXML
    private Label roomName, dataType, currentValue, variationValue;
    
    @FXML
    private JFXSlider variationSlider; 

    @FXML
    private JFXTextField targetValue, durationValue; 

    @FXML
    private JFXComboBox<String> parameter; 

    @FXML
    private JFXButton startButton; 

    @FXML
    private FontAwesomeIconView errorIcon, okIcon; 


    public void showScene(String roomName, String dataType, String currentValue)
    {
        setField("roomName", roomName); 
        // this is already as shown_dataType (e.g. "qualità dell'aria")
        setField("dataType", dataType); 
        setField("currentValue", currentValue); 

        // motion variations have trigger instead of variation values, so we don't need slider and things
        // but we still do need the target value (because detector's sensitivity, ie. we might want it to ignore small values)
        if (dataType.equals(GUI_Helper.getInstance().getShownType_fromDataType(DataType.MOTION)))
        {
            variationSlider.setDisable(true);
            
            // this will set the field invisibile AND make it not occupy any space
            // so the gui will resize correctly
            parameter.setVisible(false);
            parameter.setManaged(false);

            durationValue.setVisible(true);
            durationValue.setManaged(true);

        }
        
        // air quality variations have separated parameters which are filled into the combobox
        else 
        {

            // we don't need this in a non-motion variation
            durationValue.setVisible(false);
            durationValue.setManaged(false);

            // set a listener on slider value change that updates the label below 
            variationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                variationValue.setText(getShownVariationValue(newValue.intValue()));
                variationValue.setAccessibleText(GUI_Helper.getInstance().getVariationValue(newValue.intValue()));
            });

            if (dataType.equals(GUI_Helper.getInstance().getShownType_fromDataType(DataType.AIR_QUALITY)))
            {
                parameter.setDisable(false);
                
                // fill combo box with valid param strings
                String[] params = { GUI_Helper.shownParam_CO2, GUI_Helper.shownParam_CO, GUI_Helper.shownParam_PM25, GUI_Helper.shownParam_TVOC }; 
                parameter.setItems(FXCollections.observableArrayList(params));
            }

            // any other data type
            else
            {
                
            }
        }
    }

    @Override
    public boolean setField(String field_FxId, String value) 
    {
        // can't do a switch-case because they need to be constants...
        
        if (field_FxId.equals(roomName.getId()))
        {
            roomName.setText(value);
            return true; 
        }

        if (field_FxId.equals(dataType.getId()))
        {
            dataType.setText(value);
            return true; 
        }

        if (field_FxId.equals(currentValue.getId()))
        {
            currentValue.setText(value);
            return true; 
        }

        return false;
    }

    private String getShownVariationValue(int index)
    {
        return GUI_Helper.shownVariationValues[index + 3]; 
    }

    public void paramChanged(ActionEvent e)
    {
        // save user choice for later, but casting it to a meaningful value
        parameter.setAccessibleText(GUI_Helper.getInstance().getAirqParam_fromShownString(parameter.getValue()));
    }

    public void startVariation(ActionEvent e)
    {
        try 
        {
            // variation of a value
            String room = roomName.getText(); 
            DataType type = GUI_Helper.getInstance().getDataType_fromShownType(dataType.getText()); 
            String airqParam = parameter.getAccessibleText(); 

            VariationType vType;
            Float motionDuration = 0f; 

            // get variation type (e.g. INCREASE FASTER)
            // motion values have TRIGGER only
            if (type == DataType.MOTION)
            {
                vType = VariationType.TRIGGER;
                motionDuration = Float.parseFloat(durationValue.getText());  
            }
            else 
                vType = VariationType.valueOf(variationValue.getAccessibleText());  

            Float target = Float.parseFloat(targetValue.getText()); 
            // variation request check: 
            // is the target value < than current for an increase? 
            // or > for a decrease? 
            if (acceptVariationRequest(type, vType, target, airqParam, motionDuration) == false)
                throw new Exception("Incorrect target value.");

            // create an Event for the internal Pub/Sub to publish on the Room's channel
            // anyone listening on that room channel will receive it and act accordingly
            // for example, the GUI (GraphicalUI class) will update the displayed values in the table
            // the log will add a line
            // and of course the "model" (Room) will update their value by starting a thread (if necessary)
            Event event = new Event(EventType.VARIATE_DATA_REQUEST); 
            
            // air quality, since it has 4 params that can be independently variated, have a special function to call
            // so has motion, with its duration
            Message message;

            switch (type)
            {
                case MOTION:        message = new Message(event.TYPE, type, vType, target, motionDuration); break;
                case AIR_QUALITY:   message = new Message(event.TYPE, type, airqParam, vType, target); break;
                default:            message = new Message(event.TYPE, type, vType, target); break;
            }    

            Channel channel = PubSubManager.getInstance().GetChannel_FromName(room, ChannelType.ROOM); 
            event.PublishTo(channel, message);
            
            variationIsWorking(true);
        }

        catch (Exception ex)
        {
            Log.getInstance().Write("ERROR (VARIATE_DATA_GUI) : Errore nei parametri.");
            Log.getInstance().Write(ex.getMessage());

            variationIsWorking(false);
        }
    }

    private void variationIsWorking(boolean isWorking)
    {
        if (isWorking)
        {    
            okIcon.setOpacity(1);
            errorIcon.setOpacity(0.5f);
        }
        else
        { 
            okIcon.setOpacity(0.5f);
            errorIcon.setOpacity(1);
        }
    }

    private boolean acceptVariationRequest(DataType type, VariationType vType, Float target, String airqParam, Float motionDuration)
    {
        if (type == DataType.MOTION)
        {
            // reject variation if the duration of the movement is not positive (seconds)
            // and also if the target value is negative
            if ((motionDuration <= 0) && (target < 0))
                return false;
            
            return true; 
        }

        // target value threshold checks: only acceptable negative values are for temperatures
        if ((type != DataType.TEMPERATURE) && (target < 0))
            return false;

        // target value threshold checks: flood value work with a minimum value of 2.0 V; 
        // reject all values below that threshold
        if ((type == DataType.FLOOD) && (target < DataType_Helper.FLOOD_SENSOR_MINACCEPTABLEVALUE))
            return false;
        
        // same for smoke 
        if ((type == DataType.SMOKE) && (target < DataType_Helper.SMOKE_SENSOR_MINACCEPTABLEVALUE))
            return false;
        
        // get the current value from the displayed value, cast it to float for comparation with target value
        Data currentData = new Data(type, currentValue.getText()); 
        Float currValue = 0f; 
        switch (type)
        {
            case TEMPERATURE:   currValue = currentData.ToTemperature();  break;
            case HUMIDITY:      currValue = currentData.ToHumidity();   break;
            case AIR_QUALITY:   currValue = currentData.ToAirQuality(airqParam);    break;
            case SMOKE:         currValue = currentData.ToSmoke();  break;
            case FLOOD:         currValue = currentData.ToFlood(); break; 
            default: break;
        }

        // increasing variations
        if ((vType == VariationType.INCREASE) || (vType == VariationType.INCREASE_FASTER) || (vType == VariationType.INCREASE_FASTEST))
        {
            if (target > currValue)
                return true;
        }
        
        // decreasing variations
        if ((vType == VariationType.DECREASE) || (vType == VariationType.DECREASE_FASTER) || (vType == VariationType.DECREASE_FASTEST))
        {
            if (target < currValue)
            return true;
        }

        // in any other case, reject the variation request
        return false; 
    }
}