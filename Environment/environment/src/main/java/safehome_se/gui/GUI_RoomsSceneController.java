package safehome_se.gui;

import javafx.fxml.FXML;

import javafx.scene.layout.AnchorPane;


public class GUI_RoomsSceneController 
{
    @FXML
    AnchorPane room_rootPane; 

    private int currentRoom;
    private Integer maxRooms;
    private final int DEFAULT_ROOM = 0;

    public void initialize()
    {
        loadDefaultRoom();
        maxRooms = ModelDataLoader.getInstance().house.rooms.size();  

    }

    public void loadRoom(int n) 
    {
        GUI_RoomController controller = (GUI_RoomController) GUI_Loader.getInstance()
                                                    .loadScene(GUI_Helper.PAGE_ROOM, room_rootPane);

        controller.room_Name = ModelDataLoader.getInstance().house.rooms.get(currentRoom).NAME; 
        GUI_Helper.getInstance().controllers_Room.put(controller.room_Name.toUpperCase(), controller); 
        controller.showScene();
    }

    private void loadDefaultRoom() 
    {
        currentRoom = DEFAULT_ROOM;
        loadRoom(currentRoom);
    }

    public void prevRoom()
    {     
        currentRoom--; 
        if (currentRoom < 0)
            currentRoom = maxRooms - 1; 

        loadRoom(currentRoom);
    }

    public void nextRoom()
    {
        currentRoom++; 
        if (currentRoom > maxRooms - 1)
            currentRoom = 0;
        
        loadRoom(currentRoom);   
    }

 
}