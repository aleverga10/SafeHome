package safehome_se.gui;

import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;


public class GUI_MenuController 
{   
    private GUI_Loader helper; 

    @FXML 
    private JFXButton mainMenu_Home; 

    @FXML 
    private JFXButton mainMenu_Rooms; 

    @FXML 
    private JFXButton mainMenu_Log; 

    @FXML 
    private JFXButton mainMenu_Scenario;


    public void initialize()
    {
        helper = GUI_Loader.getInstance(); 
    }
    /*
    public void load_homeScene() 
    {
        helper.loadMainScene(); 
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

    public void load_roomsScene()
    {
        helper.loadScene(GUI_Helper.PAGE_ROOMSSCENE, helper.main_rootPane);
    }
}