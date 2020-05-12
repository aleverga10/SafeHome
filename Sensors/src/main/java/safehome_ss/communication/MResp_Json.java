package safehome_ss.communication;

import java.lang.reflect.Type;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class MResp_Json 
{
    private final Gson GSON; 

    public MResp_Json() 
    {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new MResp_Serializer());
        gsonBuilder.registerTypeAdapter(Date.class, new MResp_Deserializer()); 

        GSON = gsonBuilder.setPrettyPrinting().create();
    }

    public String marshal(MResp origin)
    {
        return GSON.toJson(origin); 
    }

    public MResp unmarshal(String origin)
    {
        return GSON.fromJson(origin, MResp.class);
    }

    private class MResp_Serializer implements JsonSerializer<Date> 
    {
        @Override
        public JsonElement serialize(Date timestamp, Type typeOfSrc, JsonSerializationContext context) 
        {
            return new JsonPrimitive(timestamp.getTime());
        }
    }

    private class MResp_Deserializer implements JsonDeserializer<Date> 
    {
        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException 
        {
            if (json == null)
                return null;
            return new Date(json.getAsLong()); 
        }
        
    }
}