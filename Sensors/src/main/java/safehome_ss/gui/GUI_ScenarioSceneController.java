package safehome_ss.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import safehome_ss.local.ScenarioController;
import safehome_ss.local.ScenarioController.ScenarioTypes;
import safehome_ss.local.ScenarioController.SensorBiasType;
import safehome_ss.pubsub.Log;

public class GUI_ScenarioSceneController
{

    @FXML
    private JFXTextField param /*, param2 */; 

    @FXML
    private JFXComboBox<String> scenario, biasType; 

    @FXML
    private JFXButton startButton; 

    @FXML
    private FontAwesomeIconView errorIcon, okIcon; 


    public void initialize()
    {   
        param.setVisible(false);
        biasType.setVisible(false);
        //param2.setVisible(false);

        // fill combo boxes with valid param strings
        String[] biases = { SensorBiasType.GAUSSIAN.toString(), SensorBiasType.RANDOM.toString()}; 
        biasType.setItems(FXCollections.observableArrayList(biases));
        
        String[] scenarios = { ScenarioTypes.SENSOR_FAILURE.toString(), ScenarioTypes.MEASUREMENT_ERROR.toString(), ScenarioTypes.ALARM_FAILURE.toString(), ScenarioTypes.OPENHAB_DELAY.toString()}; 
        scenario.setItems(FXCollections.observableArrayList(scenarios));
    }

    public void startScenario(ActionEvent e)
    {
        try 
        {
            float[] params = new float[2]; 
            
            if (param.isVisible())
                params[0]= Float.parseFloat(param.getText());
            
            /*
            // when it is hidden its value is null and would throw an exception
            if (param2.isVisible())
                params[1] = Float.parseFloat(param2.getText());
            */

            String[] strings = { biasType.getValue() };
            if (acceptScenarioRequest(params) == false) 
                throw new Exception();
            
            // activate the selected scenario with the inputted params
            ScenarioController.getInstance().ActivateScenario(ScenarioTypes.valueOf(scenario.getValue()), params, strings);
            scenarioIsWorking(true);
        }
        catch (Exception ex)
        {
            Log.getInstance().Write("ERROR (SCENARIOSCENE_GUI) : Errore nei parametri.");
            Log.getInstance().Write(ex.getMessage());
            scenarioIsWorking(false);
        }
    }

    private void scenarioIsWorking(boolean isWorking)
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

    private boolean acceptScenarioRequest(float[] params)
    {
        switch(scenario.getValue())
        {
            case "SENSOR_FAILURE":      return (params[0] > 0); 
            case "MEASUREMENT_ERROR":   
                 /* return ((params[0] > 0) && (params[1] > 0)) ; */
                 if (biasType.getValue() == "GAUSSIAN")
                    return true;
                 if (biasType.getValue() == "RANDOM")
                    return (params[0] > 0); 

            case "OPENHAB_DELAY":      return (params[0] > 0); 
        }
        return false;
    }

    @FXML
    void scenarioChanged(ActionEvent event) 
    {
        switch (scenario.getValue())
        {
            case "SENSOR_FAILURE": 
                param.setVisible(true);
                biasType.setVisible(false);
                //param2.setVisible(false);

                param.setPromptText("Sensor Failure Probability");
                break; 

            case "MEASUREMENT_ERROR": 
                biasType.setVisible(true);
                biasType.setValue("GAUSSIAN");
                //param2.setVisible(false);
                param.setVisible(false);
                param.setPromptText("Random Bias Error Probability");
                //param2.setPromptText("Random Bias Error Max value");
                break;

            case "OPENHAB_DELAY":
                param.setVisible(true);
                biasType.setVisible(false);
                //param2.setVisible(false);

                param.setPromptText("Max delay in ms");
                break;

            default: 
                param.setVisible(true);
                biasType.setVisible(false);
                //param2.setVisible(false);
                break;
        }
    }

    @FXML
    void biasTypeChanged(ActionEvent event)
    {
        switch (biasType.getValue())
        {
            case "GAUSSIAN": 
                param.setVisible(false);
                break;
            case "RANDOM":  
                param.setVisible(true);
                break;
            default: 
                break;
        }
    }

}