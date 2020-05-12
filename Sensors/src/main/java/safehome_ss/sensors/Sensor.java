package safehome_ss.sensors;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import safehome_ss.local.ScenarioController;
import safehome_ss.local.ScenarioController.ScenarioTypes;
import safehome_ss.local.ScenarioController.SensorBiasType;
import safehome_ss.pubsub.Channel;
import safehome_ss.pubsub.Event;
import safehome_ss.pubsub.Log;
import safehome_ss.pubsub.Message;
import safehome_ss.pubsub.PubSubManager;
import safehome_ss.pubsub.Subscriber;
import safehome_ss.pubsub.Channel.ChannelType;
import safehome_ss.pubsub.Event.EventType;
import safehome_ss.sensors.Data.DataType;

public abstract class Sensor implements Subscriber 
{
    public String UID; 
    public String NAME; 
    public DataType TYPE; 
    public String type_asShown; 
    public float refreshTime; 
    
    protected Float measuredValue; 
    public Data measuredData; 

    public final Random RNG; 

    public enum SensorStatus { OFFLINE, STARTED, ONLINE, FAILURE; }
    public SensorStatus status; 

    // scenario related fields

    // sensor failure
    private Float sensorFailure_maxProbability; // [0..1], actual assignment in ScenarioController
    protected Float motionProbCorrectionFactor = 0.565f; // derived from Binomial random values calculations to have the same failure prob. of the others despite doing less measures

    // measurement error
    protected Float ACCURACY; 
    protected double bias = 0; // this equals -> measuredValue - realValue (21.3 °C - 21.2 °C = 0.1 °C of error)
    public SensorBiasType biasType; 
    
    protected float bias_randomTyped_probability; // sometimes a random malfunctioning happens and the sensor outputs a value which is way more wrong than "usual gaussian" error
    protected float bias_randomMaxValue ; // how much wrong it is (in % on the value) 
    public int randomErrors = 0; 

    /*
    public int correctCalls = 0; // ie. "working normally"
    public int negativeFalses = 0; //ie. "should have triggered the alarm but did not" (data > threshold; data + error < threshold)
    public int positiveFalses = 0; //ie. "should not have triggered the alarm but did" (data < threshold; data + error > threshold)
    public int falses_causedByRandomErrors = 0; // ie. 
    */

    // thread related fields
    private final ScheduledExecutorService EXECUTOR_SERVICE; 

    public Sensor(String id, String name, DataType type)
    {
        this.TYPE = type;
        this.measuredData = new Data(this.TYPE); 
        
        this.RNG = new Random(); 

        this.EXECUTOR_SERVICE = (ScheduledExecutorService) Executors.newScheduledThreadPool(1);
        // we don't need to assign other fields here since this is called only to built a temporary object
        // which will be filled with real config data once we've read them from the file
    }

    public void Start(Sensor_Thing.Sensor_Thing_Status Thing_Status)
    {       
        // set sensor status to STARTED when his device (thing) is ON 
        // and when this sensor is not FAILED 
        // (ie. failing is permanent until .txt file is changed)         
        if ((Thing_Status == Sensor_Thing.Sensor_Thing_Status.ON) && (this.status != SensorStatus.FAILURE))
        {    
            changeStatusEvent(SensorStatus.STARTED);
        }
    }

    public void Arm()
    {
        if (this.status == SensorStatus.STARTED)
        {
            changeStatusEvent(SensorStatus.ONLINE);
        }

        
    }

    public void Shutdown()
    {
        changeStatusEvent(SensorStatus.OFFLINE); 
    }

    public void SubscribeTo(Channel channel) 
    {
        channel.AddSub(this);
    }  

    public void ReceiveMessage(Channel channel, Message message) 
    {
        if (channel.TYPE == ChannelType.SENSOR) 
        {
            switch (message.EVENT_TYPE)
            {
                case MEASURE_RESPONSE: 
                    // status check because user might have turned the whole device off in the meantime
                    if ((this.status == SensorStatus.ONLINE) && (Measure_FailureCheck() == false))
                    {   
                        Measure(message.response_Data);
                        FinalizeMeasure();
                    }
                    break; 
                
                case SENSOR_FAILURE: 
                    // start performing "the failure check" every measurement using this as the max probability
                    this.sensorFailure_maxProbability = message.scenario_Failure_Pmax; 
                    break;
                
                case MEASUREMENT_ERROR:
                    // start performing "measurement error" testing by using this as the error bias type (e.g gaussian / random error)
                    this.biasType = message.biasType; 
                    // and this as the random error probability and the error (on % of the actual value) 
                    this.bias_randomTyped_probability = message.maxRandomError_Probability; 
                    this.bias_randomMaxValue = message.maxRandomError; 
                    break;
                default: break;  
            }


        } 
    }

    protected void FinalizeMeasure()
    {
         //tell everyone listening on my channel i "measured" this data
         Event event = new Event(EventType.MEASURE_DONE); 
         Message storedMessage = new Message(EventType.MEASURE_DONE, this.TYPE, this.UID, this.measuredData);
         Channel channel = PubSubManager.getInstance().GetChannel_FromName(this.UID, ChannelType.SENSOR); 
         event.PublishTo(channel, storedMessage);
    }

    public void Schedule_AutoMeasure()
    {
        // auto measures happen every [refreshTime] seconds
        EXECUTOR_SERVICE.scheduleAtFixedRate(
            // lambda function instead of run()
            () -> 
            {
                if (this.status == SensorStatus.ONLINE)
                {
                    Event event = new Event(EventType.MEASURE_AUTOLOOP); 
                    Message message = new Message(EventType.MEASURE_AUTOLOOP, this.TYPE, this.UID);
                    Channel channel = PubSubManager.getInstance().GetChannel_FromName(this.UID, ChannelType.SENSOR);
                    event.PublishTo(channel, message); 
                }
                else
                {
                    // we wanted a measure but the sensor isn't online
                    // perhaps its thing (device) has dead batteries, or is failed, or whatever
                    // we have a reliability issue here
                }
            },    
            (long) refreshTime, (long) refreshTime, TimeUnit.SECONDS);
    }

    private boolean Measure_FailureCheck()
    {
        // testing scenario "sensor failure" checks here
        // basically, there's a probability this sensor will fail 
        // we randomize a number [0..1] and check it against this max probability, happens if n <= pMax
        if (ScenarioController.getInstance().scenario == ScenarioTypes.SENSOR_FAILURE)
        {
            // measurement failure check
            float n = RNG.nextFloat(); // [0..1) uniformly distributed

            // motion sensor work a bit differently, in the sense they do not "poll" the environment for a data,
            // instead they are delivered data when trigger happen; to take this into account we need to artificially increase its 
            // failure probability, or it will almost never fail (compared to the others)
            if (this.TYPE == DataType.MOTION)
                n = n * motionProbCorrectionFactor;

            if (n < sensorFailure_maxProbability)
            {
                // failed, returns true
                changeStatusEvent(SensorStatus.FAILURE);
  //              failures++; 
                return true;
            }   
        }
        // in any other case, return false means "perform normal measurement"
        //changeStatusEvent(SensorStatus.ONLINE);
//        correctMeasurements++; 

        // update every subscriber's (mainly GUI and Log) about the updated counts of correct and failed measurements
       // updateScenarioResults();

        return false; 
    }

    private void changeStatusEvent(SensorStatus newStatus)
    {
        this.status = newStatus; 

        // this is used to inform every subscriber to this sensor's channel (mainly GUI, Log)
        // that a change in its status happened and they need to react
        // yes, this class will also receive this message but it will be ignored
        Event event = new Event(EventType.SENSOR_STATUS_UPDATE); 
        Message message = new Message(this.UID, newStatus); 
        Channel channel = PubSubManager.getInstance().GetChannel_FromName(this.UID, ChannelType.SENSOR); 
        event.PublishTo(channel, message);
    }

    protected void Check_MeasurementError(Float currentValue)
    {
        if (ScenarioController.getInstance().scenario == ScenarioTypes.MEASUREMENT_ERROR)
        {
            // we want to introduce a measurement error being gaussian or random
            switch (this.biasType)
            {
                case GAUSSIAN:  this.bias = RNG.nextGaussian() * this.ACCURACY / 3; break; // yields [-ACC, +ACC] 99.5% of the times (3 because 99.5% is 3 * sigma);
                case RANDOM:    

                    // init in case of no error happening during this measurement
                    bias = 0; 

                    // check p of a random error happening
                    float n = RNG.nextFloat(); // [0..1) uniformly distributed

                     // motion sensor work a bit differently, in the sense they do not "poll" the environment for a data,
                    // instead they are delivered data when trigger happen; to take this into account we need to artificially increase its 
                    // failure probability, or it will almost never fail (compared to the others)
                    if (this.TYPE == DataType.MOTION)
                        n = n * motionProbCorrectionFactor;

                    if (n < bias_randomTyped_probability)
                    {
                        int sign = RNG.nextBoolean() == true ? +1 : -1; //yields -1 or +1

                        // perform error bias calculations

                        // motion types have % values so there is a special cases for them
                        // in that rare case they will output maxValue, eg. 35% (triggering the alarm once)
                        if (this.TYPE == DataType.MOTION)
                            bias = currentValue + sign * bias_randomMaxValue; 
                        else
                            bias = sign * (currentValue * bias_randomMaxValue); 

                        // yields (-1 or +1) * MAX % of current value (e.g. -1 * 35% of 20 °C = 13°C )
                        randomErrors++; 
                        Log.getInstance().Write("******************** RANDOM ERROR for "+this.UID + " ON "+ currentValue +" BIAS "+ bias+ " **************** ");
                    }

                    break; 

                default: break;
            }
        }
        else // normal measurement are "perfect", aside from rounding
            this.bias = 0;
    }

    /*
    protected boolean updateScenarioResults()
    {
        // this is used to inform every subscriber to this sensor's channel (mainly GUI, Log)
        // about the updated counts of correct and failed measurements
        // yes, this class will also receive this message but it will be ignored
        Event event = new Event(EventType.SCENARIO_RESULTS_UPDATE); 
        Message message;
        switch (ScenarioController.getInstance().scenario)
        {
            case ALARM_FAILURE:         message = new Message(correctMeasurements, failures); break;
            case MEASUREMENT_ERROR:     message = new Message(correctCalls, positiveFalses, negativeFalses); break;
            default:                    return false;   // ie. don't do anything (this should be unreachable code)
        }
         
        Channel channel = PubSubManager.getInstance().GetChannel_FromName(this.UID, ChannelType.SENSOR); 
        event.PublishTo(channel, message);
        return true; 
    }
*/
    protected abstract void Measure(Data realData);
    protected abstract boolean IsTriggered(); 

}
