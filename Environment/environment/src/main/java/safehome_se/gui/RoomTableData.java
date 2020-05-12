package safehome_se.gui;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RoomTableData extends RecursiveTreeObject<RoomTableData>
    {
        public StringProperty dataType; 
        public StringProperty dataValue; 
        public StringProperty variationInProgress; 
        public StringProperty targetValue; 

        public RoomTableData(String dataType, String dataValue, String variationInProgress, String targetValue)
        {   
            this.dataType = new SimpleStringProperty(dataType); 
            this.dataValue = new SimpleStringProperty(dataValue); 
            this.variationInProgress = new SimpleStringProperty(variationInProgress); 
            this.targetValue = new SimpleStringProperty(targetValue); 
        };
    }