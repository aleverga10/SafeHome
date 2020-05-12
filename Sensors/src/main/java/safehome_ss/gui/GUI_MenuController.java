package safehome_ss.gui;

import java.io.IOException;

import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;

public class GUI_MenuController {
    private GUI_Loader helper;

    @FXML
    private JFXButton mainMenu_Home, mainMenu_SensorThings, mainMenu_Log, mainMenu_Scenario;


    public void initialize() {
        helper = GUI_Loader.getInstance();
    }

    /*
    public void load_homeScene() {
        try 
        {
            helper.loadMainScene();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
    */
    public void load_scenariosScene() 
    {
        helper.loadScene(GUI_Helper.PAGE_SCENARIOSCENE, helper.main_rootPane);
    }

    public void load_logScene() 
    {
        GUI_LogController logController = (GUI_LogController) helper.loadScene(GUI_Helper.PAGE_LOG, helper.main_rootPane);
        logController.write_pastLog();
    }

    public void load_sensorThingsScene()
    {
        helper.loadScene(GUI_Helper.PAGE_SENSORTHINGS_SCENE, helper.main_rootPane); 
    }
}