package safehome_ss.gui;

import java.io.File;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import safehome_ss.sensors.Data.DataType;

public class GUI_SensorController implements GUI_Controller 
{
    public String sensorUID, dataType;

    @FXML
    private Label sensorThingName;

    @FXML
    private Label sensorName;

    @FXML
    private Label sensorType;

    @FXML
    private HBox refreshTimeBox; 

    @FXML
    private Label refreshTime; 

    @FXML
    private ImageView sensorTypeImage; 

    @FXML
    private Label sensorStatus;

    @FXML
    private Label measuredData;

    @FXML
    private Label measuredCO2, measuredCO, measuredPM25, measuredTVOC; 

    @FXML 
    private VBox airqualityBox; 

  
    @Override
    public boolean setField(String field_FxId, String value) 
    {
        // can't do a switch-case because they need to be constants...

        if (field_FxId.equals(sensorThingName.getId()))
        {
            sensorThingName.setText(value);
            return true; 
        }
        
        if (field_FxId.equals(sensorName.getId()))
        {
            // stackoverflow.com/questions/21083945/how-to-avoid-not-on-fx-application-thread-currentthread-javafx-application-th
            Platform.runLater( () -> { sensorName.setText(value); });
            return true; 
        }

        if (field_FxId.equals(sensorType.getId()))
        {
            sensorType.setText(value);
            
            // get the right image for this datatype
            File file = new File(GUI_Helper.ICONS.get(this.dataType));
            Image image = new Image(file.toURI().toString());
            sensorTypeImage.setImage(image);

            // check on datatype is of air quality type
            // could also go elsewhere but it's here for coherency reasons
            if (this.dataType.equals(DataType.AIR_QUALITY.name()))
            {
                airqualityBox.setVisible(true);
                measuredData.setVisible(false);
            }
         
            // motion sensors do not have refresh times, so set all fields invisible
            if (this.dataType.equals(DataType.MOTION.name()))
            {
                refreshTimeBox.setVisible(false);
            }

            return true; 
        }

        if (field_FxId.equals(measuredData.getId()))
        {
            String v = value; // need to do this otherwise code in "Platform.runLater breaks apparently ???"
            if (value.contains("@") && value.contains("*")) // this is the "initial value" keycode
                if (dataType.equals(DataType.AIR_QUALITY.name()))
                    v = "CO2: N/A - CO: N/A - PM2.5: N/A - TVOC: N/A";
                else
                    v = "N/A"; // special GUI initial value
            update_measuredData(v);
            return true;
        }

        if (field_FxId.equals(sensorStatus.getId()))
        {
            sensorStatus.setText(value);
            return true; 
        }

        if (field_FxId.equals(refreshTime.getId()))
        {
            refreshTime.setText(value);
            return true; 
        }

        return false; 
    }

    public void update_measuredData(String value) 
    {
        if (dataType.equals(DataType.AIR_QUALITY.name())) 
        {
            // if this is an air quality sensor we need to set all the four labels
            // let's divide the data into tokens to display them all separately
            // since a valid AIRQ value is like "CO2: 1500 ppm - CO: 5 ppm - PM2.5: 6.5 ppm - TVOC: 7000 ppb"
            // we can use " - " as the tokenizer
            String[] tokens = value.split(" - ");
            
            // stackoverflow.com/questions/21083945/how-to-avoid-not-on-fx-application-thread-currentthread-javafx-application-th
            Platform.runLater(new Runnable()
            {
                @Override
                public void run() {
                    measuredCO2.setText(tokens[0]); 
                } 
                
            });
            
            Platform.runLater(new Runnable()
            {
                @Override
                public void run() {
                    measuredCO.setText (tokens[1]); 
                } 
                
            });

            Platform.runLater(new Runnable()
            {
                @Override
                public void run() {
                    measuredPM25.setText(tokens[2]); 
                } 
                
            });

            Platform.runLater(new Runnable()
            {
                @Override
                public void run() {
                    measuredTVOC.setText(tokens[3]); 
                } 
                
            });
        }
        else
        {
            // stackoverflow.com/questions/21083945/how-to-avoid-not-on-fx-application-thread-currentthread-javafx-application-th
            Platform.runLater(new Runnable()
            {
                @Override
                public void run() {
                    measuredData.setText(value);
                } 
                
            });
        }
    }

}