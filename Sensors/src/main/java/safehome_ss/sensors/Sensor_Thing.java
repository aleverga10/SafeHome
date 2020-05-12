package safehome_ss.sensors;

import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import safehome_ss.pubsub.Log;
import safehome_ss.sensors.Battery.BatteryStatus;
import safehome_ss.sensors.Data.DataType;
import safehome_ss.sensors.Sensor.SensorStatus;

public class Sensor_Thing implements Observer {
    public Battery battery;
    public String uid;
    public String name;
    public String model;
    public String isPlacedIn;

    public final float BATTERY_ALERT_LIMIT = 5f;

    public enum Sensor_Thing_Status {
        ON,
        OFF,
        UNKNOWN,
        BATTERY_ALERT
    };

    public Sensor_Thing_Status status;

    public Set<Sensor> sensors;

    public Sensor_Thing(String uid, String name, Battery b, List<DataType> sensorsToHave) {
        this.name = name;
        this.uid = uid;
        this.battery = b;
        this.sensors = new HashSet<Sensor>();

        for (DataType type : sensorsToHave) {
            Add_Sensor(type);
        }

        status = Sensor_Thing_Status.OFF;
    }

    public void Start() 
    {
        /* BATTERY GETS REMOVED FOR NOW 
        // check on battery status
        BatteryAlert_Check(this.battery.percentage);
        if (this.status != Sensor_Thing_Status.BATTERY_ALERT) {
            this.status = Sensor_Thing_Status.ON;

            this.battery.StartDischarging();
        */
        this.status = Sensor_Thing_Status.ON;
            // start every sensor attached to this sensor real object (RoB)
            for (Sensor sensor : sensors) {
                sensor.Start(this.status);
                sensor.Arm();

                // start an AutoLoop thread for every sensor but motion
                if (sensor.TYPE != DataType.MOTION)
                    sensor.Schedule_AutoMeasure();
            }
        /*}

        else {
            Log.getInstance().Write("SENSOR OBJECT " + name + ": cannot start. Reason: battery too low.");
        }
        */
    }

    public void Shutdown() {
        this.status = Sensor_Thing_Status.OFF;
        this.battery.Shutdown();

        for (Sensor sensor : sensors) {
            sensor.Shutdown();
        }
        Log.getInstance().Write("SENSOR OBJECT " + name + ": shut down.");
    }

    public void StartCharging() {
        this.battery.StartCharging();
        Log.getInstance().Write("SENSOR OBJECT " + name + ": is charging.");
    }

    // TO DO si potrebbe fare un evento che monitora la this.battery.percentage e vede che quando è sopra il suo limite
    // minimo setta lo status a ON
    // così non devo farglielo chiamare dalla classe battery che è il figlio e non si fa

    /*
     * tecnicamente gli eventi si fanno così:
     * - una interfaccia listener con il metodo astratto "handle_evento"
     * - una classe che implementa questa interfaccia e definisce il suo "handle_evento"
     * - la classe che solleva l'evento ha una lista di listeners (List<Interfaccia> listeners = new
     * ArrayList<Interfaccia>(); )
     * - da qualche parte ha anche un metodo che viene eseguito con "foreach l in listeners do l.handle_evento()" che
     * solleva l'evento per tutti
     */
    public void BatteryAlert_Check(float percentage) {
        if (percentage <= BATTERY_ALERT_LIMIT) {
            Log.getInstance().Write("SENSOR OBJECT " + name + ": BATTERY ALERT ");
            this.status = Sensor_Thing_Status.BATTERY_ALERT;
        }
    }

    private void Add_Sensor(DataType type) {
        this.sensors.add(SensorsFactory.getInstance().CreateSensor(type));
    }

    public boolean HasSensor_Type(DataType type) {
        for (Sensor sensor : sensors) {
            if (sensor.TYPE == type)
                return true;
        }

        return false;
    }

    public boolean HasSensor_UID(String sensorUID) {
        for (Sensor sensor : sensors) {
            if (sensor.UID == sensorUID)
                return true;
        }

        return false;
    }

    @Override
    public void update(Observable o, Object arg) 
    {
        // this is called whenever this device's Battery status changes
        // arg is the new status

        // we can use it to update this device's sensors status and perform reliability checks and so on
        BatteryStatus batteryStatus = (BatteryStatus) arg; 

        // if battery ran out, we have to inform all our sensors they need to change their statuses to "offline"
        // and stop providing measures 
        if (batteryStatus == BatteryStatus.EMPTY)
        {
            for (Sensor sensor : sensors) 
            {
                sensor.status = SensorStatus.OFFLINE;     
            }
        }

    }
}

