/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.safehome_se.internal;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ThingTypeUID;


/**
 * The {@link SafeHome_SEBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Alessandro Verga - Initial contribution
 */
@NonNullByDefault
public class SafeHome_SEBindingConstants {

    private static final String BINDING_ID = "safehome_se";

    // List of all supported models (must be named exactly as in the thing .xml)
    // one type
    public static final String MODEL_T = "model_t"; // temperature
    public static final String MODEL_H = "model_h"; // humidity
    public static final String MODEL_A = "model_a"; // air quality
    public static final String MODEL_S = "model_s"; // smoke
    public static final String MODEL_M = "model_m"; // motion
    public static final String MODEL_F = "model_f"; // flood

    // two type
    public static final String MODEL_TH = "model_th"; // temperature, humidity
    public static final String MODEL_TF = "model_tf"; // temperature, flood
    public static final String MODEL_HA = "model_ha"; // humidity, air quality
    public static final String MODEL_SM = "model_sm"; // motion, smoke

    // thee type
    public static final String MODEL_THF = "model_thf"; // temperature, humidity, flood
    public static final String MODEL_THA = "model_tha"; // temperature, humidity, air quality
    public static final String MODEL_HAS = "model_has"; // humidity, air quality, smoke
    
    
    // more than three
    public static final String MODEL_THASM = "model_thasm"; // temperature, humidity, air quality, smoke, motion
    public static final String MODEL_THASMF = "model_thasmf"; // temperature, humidity, air quality, smoke, motion, flood

    public static final Set<String> SUPPORTED_MODELS = Collections
            .unmodifiableSet(Stream.of(
                MODEL_T, MODEL_H, MODEL_A, MODEL_S, MODEL_M, MODEL_F,
                MODEL_TH, MODEL_TF, MODEL_HA, MODEL_SM,
                MODEL_THF, MODEL_THA, MODEL_HAS,
                MODEL_THASM, MODEL_THASMF)
                .collect(Collectors.toSet()));

    // List of all Thing Type UIDs

    // one type
    public static final ThingTypeUID THING_TYPE_T = new ThingTypeUID(BINDING_ID, MODEL_T);
    public static final ThingTypeUID THING_TYPE_H = new ThingTypeUID(BINDING_ID, MODEL_H);
    public static final ThingTypeUID THING_TYPE_A = new ThingTypeUID(BINDING_ID, MODEL_A);
    public static final ThingTypeUID THING_TYPE_S = new ThingTypeUID(BINDING_ID, MODEL_S);
    public static final ThingTypeUID THING_TYPE_M = new ThingTypeUID(BINDING_ID, MODEL_M);
    public static final ThingTypeUID THING_TYPE_F = new ThingTypeUID(BINDING_ID, MODEL_F);

    // two type
    public static final ThingTypeUID THING_TYPE_TH = new ThingTypeUID(BINDING_ID, MODEL_TH);
    public static final ThingTypeUID THING_TYPE_TF = new ThingTypeUID(BINDING_ID, MODEL_TF);
    public static final ThingTypeUID THING_TYPE_HA = new ThingTypeUID(BINDING_ID, MODEL_HA);
    public static final ThingTypeUID THING_TYPE_SM = new ThingTypeUID(BINDING_ID, MODEL_SM);

    // three type
    public static final ThingTypeUID THING_TYPE_THF = new ThingTypeUID(BINDING_ID, MODEL_THF);
    public static final ThingTypeUID THING_TYPE_THA = new ThingTypeUID(BINDING_ID, MODEL_THA);
    public static final ThingTypeUID THING_TYPE_HAS = new ThingTypeUID(BINDING_ID, MODEL_HAS);
    
    // more than three
    public static final ThingTypeUID THING_TYPE_THASM = new ThingTypeUID(BINDING_ID, MODEL_THASM);
    public static final ThingTypeUID THING_TYPE_THASMF = new ThingTypeUID(BINDING_ID, MODEL_THASMF);


    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES = Collections.unmodifiableSet(
            Stream.of(
                THING_TYPE_T, THING_TYPE_H, THING_TYPE_A, THING_TYPE_S, THING_TYPE_M, THING_TYPE_F,
                THING_TYPE_TH, THING_TYPE_TF, THING_TYPE_HA, THING_TYPE_SM,
                THING_TYPE_THF, THING_TYPE_THA, THING_TYPE_HAS,
                THING_TYPE_THASM, THING_TYPE_THASMF)
                    .collect(Collectors.toSet()));

    // List of all Channel ids (must be named exactly as in the thing .xml, formatted as "channel_group#channel_name")
    //public static final String CHANNEL_1 = "channel1";
    //questo usa channel1 non come type-id ma come id, forse devi copiare quello? sarebbe tipo temperature_sensor#curr_temperature

    // channel group ids
    public static final String CHANNELGROUP_TEMPSENSOR = "TemperatureSensor";
    public static final String CHANNELGROUP_HUMDSENSOR = "HumiditySensor";
    public static final String CHANNELGROUP_AIRQSENSOR = "AirQualitySensor";
    public static final String CHANNELGROUP_SMOKSENSOR = "SmokeSensor";
    public static final String CHANNELGROUP_MOTNSENSOR = "MotionSensor";
    public static final String CHANNELGROUP_FLODSENSOR = "FloodSensor"; 

    public static final String CHANNEL_LMT = "#LastMeasurementTime";

    public static final String CHANNEL_TEMPSENSOR_CURRENTTEMPERATURE = CHANNELGROUP_TEMPSENSOR + "#CurrentTemperature";
    public static final String CHANNEL_TEMPSENSOR_LASTMEASUREMENTTIME = CHANNELGROUP_TEMPSENSOR + CHANNEL_LMT;

    public static final String CHANNEL_HUMDSENSOR_CURRENTHUMIDITY = CHANNELGROUP_HUMDSENSOR + "CurrentRelativeHumidity";
    public static final String CHANNEL_HUMDSENSOR_LASTMEASUREMENTTIME = CHANNELGROUP_HUMDSENSOR + CHANNEL_LMT;

    public static final String CHANNEL_AIRQSENSOR_CURRENTCO2 = CHANNELGROUP_AIRQSENSOR + "#CurrentAirQCO2";
    public static final String CHANNEL_AIRQSENSOR_CURRENTCO = CHANNELGROUP_AIRQSENSOR + "#CurrentAirQCO";
    public static final String CHANNEL_AIRQSENSOR_CURRENTPM25 = CHANNELGROUP_AIRQSENSOR + "#CurrentAirQPM25";
    public static final String CHANNEL_AIRQSENSOR_CURRENTTVOC = CHANNELGROUP_AIRQSENSOR + "#CurrentAirQTVOC";
    public static final String CHANNEL_AIRQSENSOR_LASTMEASUREMENTTIME = CHANNELGROUP_AIRQSENSOR + CHANNEL_LMT;

    public static final String CHANNEL_SMOKSENSOR_CURRENTSMOKE = CHANNELGROUP_SMOKSENSOR + "#CurrentSmoke";
    public static final String CHANNEL_SMOKSENSOR_LASTMEASUREMENTTIME = CHANNELGROUP_SMOKSENSOR + CHANNEL_LMT;

    public static final String CHANNEL_MOTNSENSOR_CURRENTMOTION = CHANNELGROUP_MOTNSENSOR + "#CurrentMotion";
    public static final String CHANNEL_MOTNSENSOR_LASTMEASUREMENTTIME = CHANNELGROUP_MOTNSENSOR + CHANNEL_LMT;

    public static final String CHANNEL_FLODSENSOR_CURRENTFLOOD = CHANNELGROUP_FLODSENSOR + "#CurrentFlood";
    public static final String CHANNEL_FLODSENSOR_LASTMEASUREMENTTIME = CHANNELGROUP_FLODSENSOR + CHANNEL_LMT;
}