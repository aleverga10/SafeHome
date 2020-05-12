package safehome_se.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import safehome_se.communication.PubNub_Instance;
import safehome_se.environment.House;
import safehome_se.environment.Room;
import safehome_se.environment.Data.DataType;
import safehome_se.pubsub.Log;
import safehome_se.pubsub.PubSubManager;
import safehome_se.pubsub.Channel.ChannelType;


public class GUI_Main extends Application {

    GUI_Helper helper;
    PubSubManager pubSubManager;

    @Override
    public void start(Stage primaryStage) throws Exception {
        initInstances();
        load();
        loadGUI(primaryStage);
    }

    public static void main(String[] args) {
        launch();
        // close everything including threads
        Platform.exit();
        System.exit(0);
    }

    private void initInstances() {
        helper = GUI_Helper.getInstance();
        PubNub_Instance.getInstance();
        pubSubManager = PubSubManager.getInstance();
    }

    private void load() {
        // temp
        boolean useDefaultValues = false;

        ModelDataLoader.getInstance().house = new House();

        File path = new File(GUI_Helper.ROOMS_FILEPATH);
        String[] files = path.list();

        for (String file : files) {
            LinkedHashMap<String, String> values = (LinkedHashMap<String, String>) loadModel(file);
            fillModel(values, useDefaultValues);
        }
    }

    private Map<String, String> loadModel(String roomName) {
        // load model values from a config file to a map like (fx_id -> value)
        // for the corresponding scene
        Map<String, String> values = new LinkedHashMap<>();

        File roomFile = new File(GUI_Helper.ROOMS_FILEPATH + roomName);

        // read
        Scanner reader;
        try {
            reader = new Scanner(roomFile);

            while (reader.hasNextLine()) {
                String line = reader.nextLine();

                // some lines are basically readme lines,
                // we can safely ignore those lines
                if (!line.startsWith("*")) {
                    // we have encountered a line like (sensor) "value"
                    // where value is a sensor type (e.g. TEMPERATURE)
                    if (line.startsWith("(")) {
                        // we can safely return when we've found this line
                        // we've already read all data we wanted
                        reader.close();
                        return values;
                    }

                    if (line.startsWith("[")) {
                        // that means, we've found the right data for this room
                        // pattern for sensor data is -> [paramID] "value"
                        String param = line.substring(1, line.indexOf("]"));
                        String value = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));

                        values.put(param, value);
                    }
                }
            }

            reader.close();
        } catch (FileNotFoundException e) {
            // file not found
            e.printStackTrace();
        }

        return values;
    }

    private void fillModel(Map<String, String> values, boolean useDefaultValues) {
        Room room = new Room(values.get("roomName"));

        String rowIndex = "", rowType = "";

        // since sensors are oblivious to the device (aka sensor thing) they work inside of,
        // someone somewhere has to keep a table that maps each sensor to the room it is placed in
        // that is what happens here
        for (Entry<String, String> entry : values.entrySet()) {
            if (entry.getKey().startsWith("sensorUID_PlacedHere_")) {
                ModelDataLoader.getInstance().house.uidToRooms.put(entry.getValue(), room.NAME);
                Log.getInstance().Write("added " + entry.getValue() + " to " + room.NAME);
            }

            else if ((!useDefaultValues) && (entry.getKey().startsWith("dataRow_"))) {
                rowType = entry.getValue();
                rowIndex = entry.getKey().substring(entry.getKey().length() - 1);
            }

            else if ((!useDefaultValues) && (entry.getKey().equals("dataCell_" + rowIndex)))
                room.Init_RealData(DataType.valueOf(rowType), entry.getValue());
        }

        if (useDefaultValues)
            room.Init_RealData(true);

        createPubSubChannel(room);

        ModelDataLoader.getInstance().house.addRoom(room);
    }

    private void createPubSubChannel(Room room) {
        pubSubManager.CreateChannel(room.NAME, ChannelType.ROOM);
        room.SubscribeTo(pubSubManager.GetChannel_FromName(room.NAME, ChannelType.ROOM));
    }

    private void loadGUI(Stage primaryStage) throws IOException {
        GUI_Loader loader = GUI_Loader.getInstance();
        URL url = new File(loader.getScenePath(GUI_Helper.PAGE_HOME)).toURI().toURL();
        //URL url = new File(loader.getScenePath(GUI_Helper.PAGE_ROOMSSCENE)).toURI().toURL();
        Parent root = FXMLLoader.load(url);

        primaryStage.setTitle("SafeHome: Environment");
        primaryStage.setScene(new Scene(root));

        loader.mainStage = primaryStage;

        primaryStage.show();
    }
}