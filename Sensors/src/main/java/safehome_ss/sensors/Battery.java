package safehome_ss.sensors;

import static java.util.concurrent.TimeUnit.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.Observable;

public class Battery extends Observable
{
    private final ScheduledExecutorService EXECUTOR_SERVICE;

    public float percentage;

 //   private final float MIN_BATTERYPERCENT = 3f;
 //   private final float MAX_BATTERYPERCENT = 100f;
    private final int BATTERY_LENGHT_SECONDS;
    private int battery_length_remaining_seconds;

    private final int CHARGING_RATE = 1;//5000;
    private final int DISCHARGING_RATE = 1;//2500;

    public enum BatteryStatus { CREATED, CHARGING, DISCHARGING, FULL, EMPTY };

    public BatteryStatus batteryStatus;

    private Battery_Runnable charging, discharging;
    private volatile ScheduledFuture<?> future; 

    // constructor
    public Battery(float currentPercentage, int max) 
    {
        EXECUTOR_SERVICE = Executors.newScheduledThreadPool(2);

        this.BATTERY_LENGHT_SECONDS = max;
        percentage = currentPercentage;

        battery_length_remaining_seconds = BATTERY_LENGHT_SECONDS * (int) currentPercentage / 100;

        batteryStatus = BatteryStatus.CREATED;

        charging = new Battery_Runnable(true);
        discharging = new Battery_Runnable(false);
        future = null; 
    }

    public void StartCharging() 
    {
        if (this.batteryStatus != BatteryStatus.CHARGING) 
        {
            if (future != null) future.cancel(true);

            charging = new Battery_Runnable(true);
            future = EXECUTOR_SERVICE.scheduleAtFixedRate(charging, 1, 1, SECONDS);
            this.batteryStatus = BatteryStatus.CHARGING;
            
            setChanged();
            notifyObservers(batteryStatus);
        }
    }

    public void StartDischarging() 
    {
        if (this.batteryStatus != BatteryStatus.DISCHARGING) 
        {
            if (future != null) future.cancel(true);

            discharging = new Battery_Runnable(false);
            future = EXECUTOR_SERVICE.scheduleAtFixedRate(discharging, 1, 1, SECONDS);
            this.batteryStatus = BatteryStatus.DISCHARGING;
            
            setChanged();
            notifyObservers(batteryStatus);
        }
    }

    public void Shutdown() 
    {
        EXECUTOR_SERVICE.shutdownNow();
    }

    private class Battery_Runnable implements Runnable 
    {
        private boolean isCharging;

        public Battery_Runnable(boolean isCharging) 
        {
            this.isCharging = isCharging;
        }

        @Override
        public void run() 
        {
            if (this.isCharging) 
            {
                if (battery_length_remaining_seconds < BATTERY_LENGHT_SECONDS)
                    battery_length_remaining_seconds += 1 * CHARGING_RATE; 

                else 
                {
                    battery_length_remaining_seconds = BATTERY_LENGHT_SECONDS; 
                    percentage = 100f; 
                    batteryStatus = BatteryStatus.FULL; 

                    setChanged();
                    notifyObservers(batteryStatus);
                }
            }
            else 
            {
                if (battery_length_remaining_seconds > 0)
                    battery_length_remaining_seconds -= 1 * DISCHARGING_RATE; 

                else 
                {
                    battery_length_remaining_seconds = 0; 
                    percentage = 0f; 
                    batteryStatus = BatteryStatus.EMPTY; 

                    setChanged();
                    notifyObservers(batteryStatus);
                }
            }
            percentage = (float)battery_length_remaining_seconds / (float)BATTERY_LENGHT_SECONDS * 100; 
        }

    }
}
