package safehome_se.gui;

import com.jfoenix.controls.JFXTextArea;

import javafx.application.Platform;
import javafx.fxml.FXML;
import safehome_se.pubsub.Log;

public class GUI_LogController 
{
    @FXML 
    JFXTextArea logTextArea; 

    public void log(String line)
    {
        Platform.runLater( () -> logTextArea.appendText(line) );
    }

    public void write_pastLog()
    {
        Log.getInstance().Write_PastLog();
    }
}