package safehome_se.communication;

import java.lang.reflect.Type;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class MReq_Json 
{
    private final Gson GSON; 

    public MReq_Json() 
    {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new MReq_Deserializer()); 

        GSON = gsonBuilder.setPrettyPrinting().create();
    }

    public MReq unmarshal(String origin)
    {
        return GSON.fromJson(origin, MReq.class);
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