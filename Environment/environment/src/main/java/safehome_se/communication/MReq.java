package safehome_se.communication; 

import java.util.Date;

public class MReq 
    {
        // we need to write a custom implementation of both serializer and deserializer
        // because Date isn't a Json Primitive and Gson wouldn't know how to marshall it
        // there is no need to write anything for other Primitive types (e.g. String)
        // a usual call to the standard serializer will handle it
        public Date timestamp;

        public String sensorUID;
        public String dataType;
        public String reason; 

        public MReq(Date timestamp, String sensorUID, String dataType, String reason)
        {
            this.timestamp = timestamp; 
            this.sensorUID = sensorUID; 
            this.dataType = dataType; 
            this.reason = reason; 
        }
    }