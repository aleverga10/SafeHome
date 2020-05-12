package safehome_se.gui;

import java.io.File;
import java.net.URL;

import com.jfoenix.controls.JFXDrawer;
/*
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
*/
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

/**
 * GUI_Controller
 */
public class GUI_MainController 
{
    @FXML
    private AnchorPane rootPane; 
/*
    @FXML
    private JFXHamburger mainMenu_Hamburger;
*/   
    @FXML
    private JFXDrawer mainMenu;

    public void initialize() 
    {
        GUI_Loader loader = GUI_Loader.getInstance();
        loader.main_rootPane = rootPane; 
        
        // load menu 
        try 
        {   
            URL url = new File(loader.getScenePath(GUI_Helper.PAGE_MENU)).toURI().toURL(); 
            AnchorPane menu = FXMLLoader.load(url);
            mainMenu.setSidePane(menu);
            mainMenu.open();
        } 
        catch (Exception e) {     e.printStackTrace();    }
/*
        // hamburger
        HamburgerSlideCloseTransition menuAnimation = new HamburgerSlideCloseTransition(mainMenu_Hamburger);
        menuAnimation.setRate(-1);
        mainMenu_Hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, 
            (event) -> 
            { 
                // jfx hamburger animation
                menuAnimation.setRate(menuAnimation.getRate() *-1);
                menuAnimation.play();

                // effect
                if (mainMenu.isClosed())
                    mainMenu.open();
                else
                    mainMenu.close();
            }

        );
*/        
        // originally there was a different home page 
        // but it has been eliminated at the very last moment
        // in favor of Rooms Scene, so this is the fix that allows it
        loader.loadScene(GUI_Helper.PAGE_ROOMSSCENE, rootPane);
        
    }
}