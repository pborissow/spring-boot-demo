package com.example.demo.utils;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.*;
import java.text.*;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

//******************************************************************************
//**  JSONMapper
//******************************************************************************
/**
 *   Custom Jackson ObjectMapper used to format dates and skip nulls when
 *   serializing objects to json
 *
 ******************************************************************************/

public class JSONMapper extends ObjectMapper {

    private static SimpleModule s;
    static {
        s = new SimpleModule();
        s.addSerializer(OffsetDateTime.class, new JsonSerializer<OffsetDateTime>(){

            public void serialize(OffsetDateTime value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {
                jgen.writeString(value.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            }

            public Class<OffsetDateTime> handledType() {
                return OffsetDateTime.class;
            }
        });

        s.addSerializer(Date.class, new JsonSerializer<Date>(){
            private final TimeZone tz = TimeZone.getTimeZone("UTC");

            public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
                df.setTimeZone(tz);
                jgen.writeString(df.format(value));
            }

            public Class<Date> handledType() {
                return Date.class;
            }
        });


    }

    public JSONMapper(){
        super();
        super.registerModule(s);
        super.setSerializationInclusion(Include.NON_NULL);
    }
}
