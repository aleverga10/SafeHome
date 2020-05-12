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

public class MReq_Json 
{
    private final Gson GSON; 

    public MReq_Json() 
    {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new MReq_Serializer());
        gsonBuilder.registerTypeAdapter(Date.class, new MReq_Deserializer()); 

        GSON = gsonBuilder.setPrettyPrinting().create();
    }

    public String marshal(MReq origin)
    {
        return GSON.toJson(origin); 
    }

    public MReq unmarshal(String origin)
    {
        return GSON.fromJson(origin, MReq.class);
    }

    private class MReq_Serializer implements JsonSerializer<Date> 
    {
        @Override
        public JsonElement serialize(Date timestamp, Type typeOfSrc, JsonSerializationContext context) 
        {
            return new JsonPrimitive(timestamp.getTime());
        }
    }

    private class MReq_Deserializer implements JsonDeserializer<Date> 
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