package com.stadgent.mobiliteitapp.utitlity;

import android.util.Log;

import com.stadgent.mobiliteitapp.model.CoyoteItem;
import com.stadgent.mobiliteitapp.model.Item;
import com.stadgent.mobiliteitapp.model.ItemType;
import com.stadgent.mobiliteitapp.model.ParkingItem;
import com.stadgent.mobiliteitapp.model.TwitterItem;
import com.stadgent.mobiliteitapp.model.VGSItem;
import com.stadgent.mobiliteitapp.model.WazeItem;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by floriangoeteyn on 02-Mar-16.
 */

//klasse om de prioriteit van een item te bepalen
public abstract class Priority {

    public static int getPriority(Item item) {

        int priority;

        if (item instanceof WazeItem) {
            ItemType.WazeType type = ((WazeItem) item).getType();
            switch (type) {
                case NO_TYPE:
                    priority = 99;
                    break;
                case ACCIDENT:
                    priority = 100;
                    break;
                case WEATHERHAZARD:
                    switch ((ItemType.WazeType.SubType) item.getSubtype()) {
                        case HAZARD_ON_SHOULDER:
                        case HAZARD_ON_SHOULDER_CAR_STOPPED:
                        case HAZARD_ON_SHOULDER_ANIMALS:
                        case HAZARD_ON_SHOULDER_MISSING_SIGN:
                            priority = 15;
                            break;
                        default:
                            priority = 70;
                            break;
                    }
                    break;
                case ROAD_CLOSED:
                    priority = 5;
                    break;
                case JAM:

                    if (((WazeItem) item).getSubtype() == ItemType.WazeType.SubType.JAM_HEAVY_TRAFFIC || ((WazeItem) item).getSubtype() == ItemType.WazeType.SubType.JAM_STAND_STILL_TRAFFIC)
                        if(item instanceof WazeItem.WazeItemAlert)
                            priority = 45;
                        else
                            priority = 60;
                    else if (((WazeItem) item).getSubtype() == ItemType.WazeType.SubType.JAM_LIGHT_TRAFFIC)
                        priority = 25;
                    else
                        priority = 40;
                    break;
                default:
                    priority = 99;
                    break;
            }

        } else if (item instanceof CoyoteItem) {
            ItemType.CoyoteType type = ((CoyoteItem) item).getType();
            switch (type) {
                case NO_TYPE:
                    priority = 99;
                    break;
                case ACCIDENT:
                    priority = 100;
                    break;
                case HAZARD:
                    switch(((CoyoteItem)item).getSubtype()){
                        case OBSTACLE:
                        case DANGER:
                        case VISIBILITE_REDUITE:
                        case CHAUSSEE_ABIMEE:
                        case ROUTE_GLISSANTE:
                            priority = 70;
                            break;
                        case INCIDENT:
                        case INCIDENT_TRAFIC:
                            priority = 20;
                            break;
                        default:
                            priority=99;
                    }
                    break;
                case JAM:
                    switch(((CoyoteItem)item).getSubtype()) {
                        case BOUCHON_UTILISATEUR:
                            priority = 25;
                            break;
                        case BOUCHON_AUTOMATIQUE:
                            priority = 60;
                            break;
                        default:
                            priority=99;
                    }
                    break;
                case RETRECISSEMENT:
                    priority = 35;
                    break;
                default:
                    priority = 99;
                    break;
            }

        } else if (item instanceof TwitterItem) {

            if (item.getType()==ItemType.TwitterType.TWITTER_MENTION)
                priority = 95;
            else
                priority = 50;

        } else if (item instanceof ParkingItem) {
            priority = 85;
        } else if (item instanceof VGSItem) {
            priority = 80;
        } else {
            priority = 99;
        }
        return priority;
    }


    /**
     * ----Waze-----
     * accident                         100
     * gedetecteerde file               60
     * standstill/zware file            45
     * gemiddelde file                  40
     * lichte file                      20
     * hazard                           70
     * on shoulder                      15
     * road closed                      5
     *
     *
     * ---Coyote-----
     * accident                         100
     * bouchon utilisateur              20
     * bouchon automatique              60
     * incident                         30
     * retrecissement                   35
     * route glissante                  70
     * obstacle                         70
     * danger                           70
     * visibilite reduite               70
     * travels                          65
     *
     *
     * ---Parking-----
     * available capacity < 11%         85
     *
     *
     * ---Twitter-----
     * mentions                         95
     * tweets                           50
     *
     *
     *---VGS---
     * nieuw scenario                   80
     *
     */



    /*
     Accidenten (Waze/Coyote)   100
     Twitter mentions   95
     Parkings   85
     VGS    80
     Waze hazards/Coyote hazards (hazard_on_road_... / route glissante, danger, obstacle, visibilite reduite)   70
     Coyote traveltimes 65
     Waze & Coyote gedetecteerde files (>250 m, snelheid <10km/u)   60
     Twitter eigen tweets (voorbije 4u)   50
     Waze gemeld zware/standstill files 45
     Waze gemeld middelmatige files 40
     Coyote wegversmalling  35
     Coyote incident    30
     Waze lichte files, Coyote gemeld files     20
     Waze on shoulder   15
     Waze wegenwerken   5
     */


}
