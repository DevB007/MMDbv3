package com.example.deveshwar.mmdbv3;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit.converter.GsonConverter;

/**
 * Created by Deveshwar on 11-12-2015.
 */
public class Util {

    private static GsonConverter sGsonConverter;

    public static GsonConverter getGsonConverter() {
        if (sGsonConverter != null) return sGsonConverter;

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new JsonDeserializer() {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                    @Override
                    public Date deserialize(final JsonElement json, final Type typeOfT,
                                            final JsonDeserializationContext context)
                            throws JsonParseException {
                        try {
                            return df.parse(json.getAsString());
                        } catch (ParseException e) {
                            return null;
                        }
                    }
                })
                .create();

        sGsonConverter = new GsonConverter(gson);
        return sGsonConverter;
    }

    private Util() {
        // Fully static API, do not allow creating new instances of this class.
    }

}
