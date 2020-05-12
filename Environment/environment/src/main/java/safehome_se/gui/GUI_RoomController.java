package safehome_se.gui;

import java.util.HashMap;
import java.util.Map.Entry;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.util.Callback;

import safehome_se.environment.Data;
import safehome_se.environment.Room;
import safehome_se.environment.Data.DataType;
import safehome_se.environment.Room.DataVariation_Params;


public class GUI_RoomController implements GUI_Controller 
{
    public String room_Name;
    
    public static final String COLUMN_NAME_TYPE = "Type"; 
    public static final String COLUMN_NAME_VALUE = "Current value"; 
    public static final String COLUMN_NAME_VARIATION = "Last variation"; 
    public static final String COLUMN_NAME_TARGET = "Target value"; 

    @FXML
    private Label roomName, lastRequest, lastRequestTime;

    @FXML
    private JFXTreeTableView dataTable; 

    GUI_Loader loader;

    public void showScene()
    {
        // load model values from the model to a map like (fx_id -> value)
        loader = GUI_Loader.getInstance();
        HashMap<String, String> values = (HashMap<String, String>) loader.loadModelData(room_Name);

        // for each value set the corresponding gui label value
        for (Entry<String, String> value : values.entrySet()) 
        {
            setField(value.getKey(), value.getValue());
            // this ^ returns a boolean, true when something was updated, if you want to log or whatever
        }

        loadTable();
    }


    private void loadTable()
    {
        JFXTreeTableColumn<RoomTableData, String> dataTypes = new JFXTreeTableColumn<>(COLUMN_NAME_TYPE); 
        dataTypes.setPrefWidth(85);
        dataTypes.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<RoomTableData, String>, ObservableValue<String>>(){
        
            @Override
            public ObservableValue<String> call(CellDataFeatures<RoomTableData, String> arg0) {
                return arg0.getValue().getValue().dataType;  
            }
        });

        JFXTreeTableColumn<RoomTableData, String> dataValues = new JFXTreeTableColumn<>(COLUMN_NAME_VALUE); 
        dataValues.setPrefWidth(100);
        dataValues.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<RoomTableData, String>, ObservableValue<String>>(){
        
            @Override
            public ObservableValue<String> call(CellDataFeatures<RoomTableData, String> arg0) {
                return arg0.getValue().getValue().dataValue;  
            }
        });

        JFXTreeTableColumn<RoomTableData, String> variationsInProgress = new JFXTreeTableColumn<>(COLUMN_NAME_VARIATION); 
        variationsInProgress.setPrefWidth(120);
        variationsInProgress.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<RoomTableData, String>, ObservableValue<String>>(){
        
            @Override
            public ObservableValue<String> call(CellDataFeatures<RoomTableData, String> arg0) {
                return arg0.getValue().getValue().variationInProgress;  
            }
        });
        
        JFXTreeTableColumn<RoomTableData, String> targetValues = new JFXTreeTableColumn<>(COLUMN_NAME_TARGET); 
        targetValues.setPrefWidth(110);
        targetValues.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<RoomTableData, String>, ObservableValue<String>>(){
        
            @Override
            public ObservableValue<String> call(CellDataFeatures<RoomTableData, String> arg0) {
                return arg0.getValue().getValue().targetValue;  
            }
        });

        // thanks youtube tutorial
        JFXTreeTableColumn<RoomTableData, Void> variateBtns = new JFXTreeTableColumn<>(""); 
        variateBtns.setPrefWidth(135);
        variateBtns.setCellFactory(new Callback<TreeTableColumn<RoomTableData, Void>, TreeTableCell<RoomTableData, Void>>(){

            @Override
            public TreeTableCell<RoomTableData, Void> call(TreeTableColumn<RoomTableData, Void> arg0) {
                final TreeTableCell<RoomTableData, Void> cell = new TreeTableCell<RoomTableData, Void>(){

                    private final JFXButton btn = new JFXButton(" VARIATE DATA "); 
                    {
                        btn.setOnAction((ActionEvent event) -> 
                        {
                            RoomTableData data = getTreeTableView().getTreeItem(getIndex()).getValue(); 
                            GUI_VariateDataController controller = (GUI_VariateDataController) loader.loadScene(GUI_Helper.PAGE_VARIATE_DATA, loader.main_rootPane);  
                            controller.showScene(room_Name, data.dataType.get(), data.dataValue.get());
                        });

                        btn.setStyle("-fx-background-color:#ff6600; -fx-text-fill:#fff; -jfx-button-type: FLAT;");
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty)
                            setGraphic(null);
                        else
                            setGraphic(btn);
                    }
                }; 
                return cell; 
            }

        }); 
       
        // fill table with columns and row data loaded from a file
        final TreeItem<RoomTableData> root = new RecursiveTreeItem<RoomTableData>(loadTableData(), RecursiveTreeObject::getChildren); 
        dataTable.getColumns().addAll(dataTypes, dataValues, variationsInProgress, targetValues, variateBtns); 
        dataTable.setRoot(root);
        dataTable.setShowRoot(false);
    }

    private ObservableList<RoomTableData> loadTableData()
    {
        Room room = ModelDataLoader.getInstance().house.getRoom_fromRoomName(room_Name); 
        ObservableList<RoomTableData> data = FXCollections.observableArrayList(); 
        for (Data roomData : room.realData) 
        {
            // check there are variations in progress on this data type
            // if there arent, show default values like "NO" and "/"
            // if there are, get their values and show them as valid strings
            DataVariation_Params variation_Params = room.variations_InProgress.get(roomData.TYPE); 
            String variationString = "NONE"; 
            String targetString  = "/"; 
            if (variation_Params != null)
            {    
                variationString = GUI_Helper.getInstance().getVariationValue_asShownString(room.variations_InProgress.get(roomData.TYPE).variationType);
                targetString = room.variations_InProgress.get(roomData.TYPE).targetValue.toString();
            }

            // this adds to the table a value like ("Temperatura", "20.0000 °C", "NO", "/") 
            data.add(new RoomTableData(GUI_Helper.getInstance().getShownType_fromDataType(roomData.TYPE), roomData.value, variationString, targetString));  
        }

        return data; 
    }


    @Override
    public boolean setField(String field_FxId, String value) 
    {
        
        if (field_FxId.equals(roomName.getId()))
        {
            roomName.setText(value);
            return true; 
        }

        // stackoverflow.com/questions/21083945/how-to-avoid-not-on-fx-application-thread-currentthread-javafx-application-th
        if (field_FxId.equals(lastRequest.getId()))
        {
            Platform.runLater(new Runnable()
            {

                @Override
                public void run() {
                    lastRequest.setText(value); 
                } 
                
            });
            return true; 
        }
        
        // stackoverflow.com/questions/21083945/how-to-avoid-not-on-fx-application-thread-currentthread-javafx-application-th
        if (field_FxId.equals(lastRequestTime.getId()))
        {
            Platform.runLater(new Runnable()
            {

                @Override
                public void run() {
                    lastRequestTime.setText(value);
                } 
                
            });
            
            return true; 
        }

        return false;
    }

    public void setTableValue(DataType type, String column, String value)
    {
        for (int i = 0; i < dataTable.getCurrentItemsCount(); i++)
        {   
            TreeItem<RoomTableData> entry = dataTable.getTreeItem(i); 
            String shownType = entry.getValue().dataType.get(); 
            if (GUI_Helper.getInstance().getDataType_fromShownType(shownType).equals(type))
            {
                setTableValue(entry.getValue(), column, value); 
            }
        }
    }

    private void setTableValue(RoomTableData entry, String column, String value)
    {
        switch (column)
        {
            case COLUMN_NAME_VALUE: entry.dataValue.set(value); break; 
            case COLUMN_NAME_VARIATION: entry.variationInProgress.setValue(value); break;
            case COLUMN_NAME_TARGET: entry.targetValue.setValue(value); break; 
            default: break; 
        }
        
    }
}