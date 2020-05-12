package safehome_ss.local;


import java.util.concurrent.Executors;

import safehome_ss.communication.OpenHAB_Caller;
import safehome_ss.pubsub.Event;
import safehome_ss.pubsub.Log;
import safehome_ss.pubsub.Message;
import safehome_ss.pubsub.PubSubManager;
import safehome_ss.pubsub.Event.EventType;


public class ScenarioController 
{
    public enum ScenarioTypes { NONE, SENSOR_FAILURE, MEASUREMENT_ERROR, ALARM_FAILURE, OPENHAB_DELAY };
    public enum SensorBiasType { GAUSSIAN, RANDOM }; 
    public SensorBiasType biasType; 
    public ScenarioTypes scenario; 

    // scenario 1: sensor failure
    public float scenarioSF_failureProbability; 
    // probability of a sensor failing is randomized and checked to be less or equal than this value
    // if this happens, the sensor will be put OFFLINE and will not measure anything anymore

    // scenario 2: measurement error
    // sometimes a random malfunctioning happens and the sensor outputs a value which is way more wrong than "usual gaussian" error
    // "how much wrong" it is, determined by [randomMaxValue] value (e.g 0.5f -> 50% of the value)
    public float scenarioME_randomBiasProbability; 
    public float scenarioME_randomMaxValue = 0.35f; // 35% of the actual value (e.g. for 20.0 °C is +/- 7 °C)

    // scenario 4: openhab delay (delay is artificially induced, we cannot act on the website)
    // we would like the system's response time to be under [responseTime_desired] 95% of the time (requirements?)
    // when this scenario is active a delay is randomzied in the range [0..maxDelay]
    // so we can check how much % of the times our requirement is met
    public long scenarioOD_maxDelay; // ms
    public long scenarioOD_timeout = 4000;// ms
    public int timeouts; 

    // Singleton pattern
    private static ScenarioController instance = null;

    public static ScenarioController getInstance() 
    {
        if (instance == null)
            instance = new ScenarioController();

        return instance;
    }

    private ScenarioController()
    {
        this.scenario = ScenarioTypes.NONE; 
    }
    
    public void ActivateScenario_SensorFailure()
    {
        this.scenario = ScenarioTypes.SENSOR_FAILURE; 

        // init openhab scenario related items' state
        OpenHAB_Caller.getInstance().ActivateScenario(scenario);

        // generate a broadcast message where we tell all sensors to start performing "the failure check"  
        Event event = new Event(EventType.SENSOR_FAILURE); 
        Message message = new Message(scenarioSF_failureProbability);
        PubSubManager.getInstance().Broadcast(event, message); 

        Log.getInstance().Write(" *** TESTING SCENARIO ("+ this.scenario + ") pMax = "+ scenarioSF_failureProbability);
    }

    public void ActivateScenario_MeasurementError(SensorBiasType biasType)
    {
        this.scenario = ScenarioTypes.MEASUREMENT_ERROR;
        this.biasType = biasType;  

        // generate a broadcast message where we tell all sensors to start having bias errors of this type
        // ie. a gaussian bias error or a random bias error 
        Event event = new Event(EventType.MEASUREMENT_ERROR);
        Message message = new Message(biasType, scenarioME_randomBiasProbability, scenarioME_randomMaxValue);
        PubSubManager.getInstance().Broadcast(event, message);

        Log.getInstance().Write(" *** TESTING SCENARIO ("+ this.scenario + ") biastype = "+ biasType + " pMax =" + scenarioME_randomBiasProbability);
    }

    public void ActivateScenario_AlarmFailure()
    {
        // this scenario is not implemented in this system, but it is directly in OpenHAB 
        // where the alarm call takes place
        // refer to .rules files to see how it is implemented

        // here we just generate an event that will change OpenHAB item "Scenario"'s' state
        // to activate the testing scenario checks
        this.scenario = ScenarioTypes.ALARM_FAILURE; 
        OpenHAB_Caller.getInstance().ActivateScenario(scenario);

        Log.getInstance().Write(" *** TESTING SCENARIO ("+ this.scenario + ") on OpenHAB");
    }

    public void ActivateScenario_OpenHABDelay()
    {
        // the scenario will be checked upon calling openhab
        // we artificially increase the web app response time
        // since we cannot act on the actual website response time
        // for example by means of a Thread.sleep
        // upon calling we will check this value and execute the scenario
        this.scenario = ScenarioTypes.OPENHAB_DELAY; 
        timeouts = 0; 

        Log.getInstance().Write(" *** TESTING SCENARIO ("+ this.scenario + ") maxDelay = " + scenarioOD_maxDelay);
    }

    public void ActivateScenario(ScenarioTypes scenario, float[] params, String[] strings)
    {
        switch (scenario)
        {
            case SENSOR_FAILURE: 
                scenarioSF_failureProbability = params[0]; 
                ActivateScenario_SensorFailure();
                break;
            
            case MEASUREMENT_ERROR: 
                scenarioME_randomBiasProbability = params[0];
                //scenarioME_randomMaxValue = params[1];
                ActivateScenario_MeasurementError(SensorBiasType.valueOf(strings[0]));
                break;

            case OPENHAB_DELAY:
                scenarioOD_maxDelay = (long) params[0];
                // init the thread executor service with a very long number so that we can create any number of concurrent threads we want
                OpenHAB_Caller.getInstance().executorService = Executors.newScheduledThreadPool(Integer.MAX_VALUE);
                ActivateScenario_OpenHABDelay();
                break;
            default: break;
        }
    }
}