package safehome_se.gui;

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
import safehome_se.environment.Room;

public class GUI_Loader 
{
    // Singleton pattern
    private static GUI_Loader instance = null;

    public static GUI_Loader getInstance() 
    {
        if (instance == null)
            instance = new GUI_Loader();

        return instance;
    }
    
    public String getScenePath(String scene) 
    {
        return  GUI_Helper.PATHS.get(scene.toUpperCase());
    }

    public AnchorPane main_rootPane;
    public Stage mainStage; 

    public Object loadScene(String scene, Pane viewPane) 
    {
        try 
        {
            URL url = new File(getScenePath(scene)).toURI().toURL();
            FXMLLoader fxmlLoader = new FXMLLoader(url);
            Node node = fxmlLoader.load();

            // when we get here, initialize() method has been called,
            // so every value should have been be correctly retrieved from model files
            // and filled into labels or whatever
            Object controller = setController(scene, fxmlLoader);
            viewPane.getChildren().setAll(node);
            
            return controller; 

        } catch (IOException e) 
        {
            e.printStackTrace();
        }

        return null; 
    }

    public void loadMainScene()
    {
        try 
        {
            GUI_Loader loader = GUI_Loader.getInstance();
            URL url = new File(loader.getScenePath(GUI_Helper.PAGE_HOME)).toURI().toURL(); 
            Parent root = FXMLLoader.load(url);
            mainStage.setScene(new Scene(root));
            mainStage.show();
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    private Object setController(String scene, FXMLLoader loader) 
    {
        switch (scene) 
        {
            case GUI_Helper.PAGE_ROOM:
                // this gets saved later in the caller method (roomscene.loadsensor), because we need to set the room id
                return (GUI_RoomController) loader.getController();

            case GUI_Helper.PAGE_LOG:
                return GUI_Helper.getInstance().controller_Log = (GUI_LogController) loader.getController(); 

            case GUI_Helper.PAGE_VARIATE_DATA:
                return (GUI_VariateDataController) loader.getController(); 
                
            default:
                return null; 
        }
    }


    public Map<String, String> loadModelData(String roomName) 
    {
        Map<String, String> values = new LinkedHashMap<>(); 
        
        ModelDataLoader loader = ModelDataLoader.getInstance(); 

        for (Room room : loader.house.rooms) 
        {
            if (room.NAME.equals(roomName))
            { 
                values.put("roomName", room.NAME); 
                values.put("lastRequest", "No request.");
                values.put("lastRequestTime", "No request.");
            }

        }

        return values; 
    }
    
}