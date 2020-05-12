package safehome_ss.communication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import safehome_ss.gui.ModelDataLoader;
import safehome_ss.local.ScenarioController;
import safehome_ss.local.ScenarioController.ScenarioTypes;
import safehome_ss.pubsub.Channel;
import safehome_ss.pubsub.Log;
import safehome_ss.pubsub.Message;
import safehome_ss.pubsub.Subscriber;
import safehome_ss.sensors.Sensor_Motion;
import safehome_ss.sensors.Sensor_Smoke;
import safehome_ss.sensors.Sensor_Flood;
import safehome_ss.sensors.Data.DataType;

public class OpenHAB_Caller implements Subscriber {
    private final String OPENHAB_API_URL = "http://localhost:8080/rest";
    private final String OPENHAB_API_KEY = "";

    private final String API_METHOD_GET = "GET";
    private final String API_METHOD_PUT = "PUT";
    private final String API_METHOD_POST = "POST";

    private final String API_ITEMS = "/items";
    private final String API_STATE = "/state";

    public Map<String, String> sensorThingUID_toOpenHABName;
    public Map<String, String> sensorUID_toOpenHABName;

    private Map<String, String> params;

    // for testing scenario OPENHAB_DELAY purposes only
    // this gets init by Scenario Controller upon scenario activation
    public ScheduledExecutorService executorService;

    // Singleton pattern
    private static OpenHAB_Caller instance = null;

    public static OpenHAB_Caller getInstance() 
    {
        if (instance == null)
            instance = new OpenHAB_Caller();

        return instance;
    }

    private OpenHAB_Caller() 
    {
        params = new LinkedHashMap<String, String>();
        sensorThingUID_toOpenHABName = new HashMap<String, String>();
        sensorUID_toOpenHABName = new HashMap<String, String>();
    }

    public void storeOpenHABName_sensorThing(String uid, String name) 
    {
        sensorThingUID_toOpenHABName.put(uid, name);
    }

    public void storeOpenHABName_sensor(String uid, String name) 
    {
        sensorUID_toOpenHABName.put(uid, name);
    }

    @Override
    public void SubscribeTo(Channel channel) 
    {
        channel.AddSub(this);
    }

    @Override
    public void ReceiveMessage(Channel channel, Message message) 
    {
        switch (message.EVENT_TYPE) 
        {
            case MEASURE_DONE:
                // this has the side effect of ignoring very early messages that might have been received even before
                // initialization is complete (who cares)
                if (sensorUID_toOpenHABName.get(message.sensorUID) != null) { // call OpenHAB API to inform the runtime
                                                                              // of the new value
                    params.put("itemName", sensorUID_toOpenHABName.get(message.sensorUID));

                    // internally we pass data as complex strings (ie. "25.5 °C"),
                    // but openhab wants only the value (ie. "25.2", as text/plain)
                    // so, let's get the number from the string and cast it back to a string
                    switch (message.DATA_TYPE) 
                    {

                        case TEMPERATURE:
                            params.put("body", String.valueOf(message.response_Data.ToTemperature()));
                            break;

                        case HUMIDITY:
                            params.put("body", String.valueOf(message.response_Data.ToHumidity()));
                            break;

                        case AIR_QUALITY:
                            // air quality needs to update 4 channels (one each airq param), so we call them all now
                            params.put("itemName", sensorUID_toOpenHABName.get(message.sensorUID) + "_CO2");
                            params.put("body", String.valueOf(message.response_Data.ToAirQuality("CO2")));
                            call(API_STATE, API_METHOD_PUT, params);

                            params.put("itemName", sensorUID_toOpenHABName.get(message.sensorUID) + "_CO");
                            params.put("body", String.valueOf(message.response_Data.ToAirQuality("CO")));
                            call(API_STATE, API_METHOD_PUT, params);

                            params.put("itemName", sensorUID_toOpenHABName.get(message.sensorUID) + "_PM25");
                            params.put("body", String.valueOf(message.response_Data.ToAirQuality("PM2.5")));
                            call(API_STATE, API_METHOD_PUT, params);

                            params.put("itemName", sensorUID_toOpenHABName.get(message.sensorUID) + "_TVOC");
                            params.put("body", String.valueOf(message.response_Data.ToAirQuality("TVOC")));
                            call(API_STATE, API_METHOD_PUT, params);

                            break;

                        case SMOKE:
                            // check if the sensor is triggered, if it is, trigger the openhab channel
                            if (((Sensor_Smoke) ModelDataLoader.getInstance().sensors
                                    .get(message.sensorUID)).isTriggered)
                                params.put("body", "OPEN");
                            else
                                params.put("body", "CLOSED");
                            break;

                        case MOTION:
                            // check if the sensor is triggered, if it is, trigger the openhab channel
                            if (((Sensor_Motion) ModelDataLoader.getInstance().sensors
                                    .get(message.sensorUID)).isTriggered)
                                params.put("body", "OPEN");
                            else
                                params.put("body", "CLOSED");
                            break;

                        case FLOOD:
                            // check if the sensor is triggered, if it is, trigger the openhab channel
                            if (((Sensor_Flood) ModelDataLoader.getInstance().sensors
                                    .get(message.sensorUID)).isTriggered)
                                params.put("body", "OPEN");
                            else
                                params.put("body", "CLOSED");
                            break;

                        default:
                            break;
                    }

                    if (params.get("body") != null) 
                    {
                        // air quality has already called to update all its channels
                        if (message.DATA_TYPE != DataType.AIR_QUALITY)
                            call(API_STATE, API_METHOD_PUT, params);

                        // update "last measurement time" field with "now"
                        // params.put("itemName", sensorUID_toOpenHABName.get(message.response_SensorUID)+"_LMT");
                        // LocalDateTime dateTime = LocalDateTime.now();

                        // params.put("body", dateTime.truncatedTo(ChronoUnit.SECONDS).toString());
                        // call(API_STATE, API_METHOD_PUT, params);
                    }

                    params.remove("body");
                    break;
                }

            default:
                break;

        }
    }

    private void call(String APIMethod, String requestMethod, Map<String, String> params) 
    {
        try 
        {
            List<Integer> goodResultCodes = new ArrayList<Integer>();
            goodResultCodes.addAll(getResultCodesFor(APIMethod));

            // GET should append params to url before opening the connection with ? ecc
            // but this particular API works a bit differently,
            // because it keeps a folder for each api method
            // so, to get the state of the item "ITEM_26"
            // we would need to have an URL like ".../rest/items/ITEM_26/state" with no explicit GET parameters

            String API_URL = APIMethod;
            API_URL = getExplicitURLFor(APIMethod, params);

            HttpURLConnection connection = openConnection(requestMethod, API_URL);

            // PUT and POST writes parameters here once it has opened the connection
            if ((requestMethod == API_METHOD_PUT) || (requestMethod == API_METHOD_POST))
                writePostParams(connection, params);

            // OpenHAB delay scenario
            // since we cannot act on to the actual website response time
            // we artificially introduce a delay by means of a randomized Thread.sleep
            // the upper value is defined in the ScenarioController class
            if (ScenarioController.getInstance().scenario == ScenarioTypes.OPENHAB_DELAY) 
            {
                long delay = (long) (new Random().nextDouble() * ScenarioController.getInstance().scenarioOD_maxDelay);
                //Log.getInstance().Write(" *** TESTING SCENARIO (OpenHAB Delay) : delaying " + params.get("itemName")+ " for " + delay + "ms *** ");
                // timeout check when it is over a pre-determined value
                if (delay < ScenarioController.getInstance().scenarioOD_timeout)
                    executorService.schedule(new OpenHABDelay_Runnable(connection, goodResultCodes), delay, TimeUnit.MILLISECONDS);
                else
                {
                    ScenarioController.getInstance().timeouts++; 
                    Log.getInstance().Write(" *** TESTING SCENARIO (OpenHAB Delay) : timeoutting " + params.get("itemName")+ " because " + delay + "ms *** ");
                }

            }

            // normal execution (ie. no delay scenario here)
            else 
                getResponse(connection, goodResultCodes); 
            
        }

        catch (IOException e)
        {
            Log.getInstance().Write("OPENHAB_CALLER (ERROR): Error during API request for "+ APIMethod);
            Log.getInstance().Write(e.getMessage());
        }
    }

    private List<Integer> getResultCodesFor(String APIMethod)
    {
        List<Integer> codeList = new ArrayList<>(); 

        switch (APIMethod)
        {
            case API_STATE: 
                codeList.add(202); // returned by PUT

            default: codeList.add(200); // always add 200s
        }

        return codeList; 
    }

    private String getExplicitURLFor(String APIMethod, Map<String, String> params)
    {
        String relativeURL = ""; 

        switch (APIMethod)
        {
            
            case API_ITEMS: 
                // append "/items/[itemName]" if any, or only "/items"
                if (params.get("itemName") != null)
                    relativeURL = OPENHAB_API_URL + API_ITEMS + "/" + params.get("itemName");
                else
                    relativeURL = OPENHAB_API_URL + API_ITEMS; 
                break;

            
            case API_STATE: 
                // append "/items/[itemName]/state"
                relativeURL = OPENHAB_API_URL + API_ITEMS + "/" + params.get("itemName") + APIMethod; 
                break; 
            

            default: break; 
        }

        return relativeURL; 
    }

    private HttpURLConnection openConnection(String method, String API_URL) throws IOException 
    {
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) (url.openConnection());
        
        connection.setRequestProperty("Accept", "application/json,text/plain");
        connection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
        connection.setRequestMethod(method);
        return connection;
    }

    private void writePostParams(HttpURLConnection connection, Map<String, String> params) throws IOException 
    {
        connection.setDoOutput(true);   
        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream()); 
        outputStream.write(buildString(connection.getURL(), params).getBytes());
        outputStream.flush();
        outputStream.close(); 
    }

    private String buildString(URL url, Map<String, String> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) 
        {
            // ignore some params because they are already part of the URL
            if (!url.getPath().contains(entry.getValue()))
            {
                //result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                //result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                //result.append("&");
            }
        }
   
        String resultString = result.toString();

        if (resultString.length() > 0)
            return resultString.substring(0, resultString.length()); 
        else
            return resultString;
    }


    private void getResponse(HttpURLConnection connection, List<Integer> goodResultCodes) throws IOException
    {
        StringBuilder response_builder = new StringBuilder(); 
        Integer HttpResponseCode = Integer.valueOf(connection.getResponseCode()); 
    
        // may contain 200, 202, ecc.
        if (goodResultCodes.contains(HttpResponseCode))
        {
            /*
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8")); 

            boolean isEnded = false; 
            while (!isEnded)
            {
                String line = reader.readLine(); 

                if (line == null)
                    isEnded = true; 
                else
                    response_builder.append(line +"\n");                
            }
            
            reader.close();

            return response_builder.toString(); 
            */
        }

        // bad result (e.g. 404, 400, ...)
        else
        {
            Log.getInstance().Write("OPENHAB_CALLER (BAD REQUEST) code "+HttpResponseCode);
            Log.getInstance().Write(connection.getResponseMessage());
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8")); 
            boolean isEnded = false; 
            while (!isEnded)
            {
                String line = reader.readLine(); 

                if (line == null)
                    isEnded = true; 
                else
                    response_builder.append(line +"\n");                
            }

            reader.close();
            //return response_builder.toString(); 
        }
    }




    public void ActivateScenario(ScenarioTypes scenario)
    {
        // update this item, which will trigger a rule to init all correct scenario's related items
        params.put("itemName", "Scenario"); 
        params.put("body", scenario.toString()); 
        call(API_STATE, API_METHOD_PUT, params);
    }


    private class OpenHABDelay_Runnable implements Runnable
    {
        HttpURLConnection connection; 
        List<Integer> goodResultCodes;

        private OpenHABDelay_Runnable(HttpURLConnection connection, List<Integer> goodResultCodes)
        {
            this.connection = connection; 
            this.goodResultCodes = goodResultCodes;
        }

        @Override
        public void run() 
        {
            try
            {
                Log.getInstance().Write("OPENHAB_DELAY SCENARIO: posting now for "+ connection.getURL());
                getResponse(connection, goodResultCodes);
            }
            catch (IOException e)
            {
                Log.getInstance().Write("OPENHAB_DELAY SCENARIO (ERROR): "+e.getMessage());
            }
        }
        
    }
}