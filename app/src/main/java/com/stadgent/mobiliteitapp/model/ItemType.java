package com.stadgent.mobiliteitapp.model;

/**
 * Created by floriangoeteyn on 18-Apr-16.
 */
public interface ItemType {


    enum VGSType implements ItemType{
        VGS_ALERT
    }

    enum ParkingType implements ItemType{
        PARKING_ALERT
    }
    enum TwitterType implements ItemType{
        TWITTER_TWEET, TWITTER_MENTION
    }


    enum CoyoteType implements ItemType{

        RETRECISSEMENT("Afsluiting"), HAZARD("Risico"), ACCIDENT("Ongeval"), JAM("File"), NO_TYPE("");


        String type;

        CoyoteType(String type){
            this.type=type;
        }


        public static CoyoteType valueOfCoyoteType(String s){
            try{
                return CoyoteType.valueOf(s.replaceAll(" ", "_").toUpperCase());
            }
            catch (Exception e){
                return NO_TYPE;
            }
        }

        @Override
        public String toString(){
            return type;
        }


        public enum SubType implements ItemType {

            INCIDENT("Incident"), ACCIDENT("Ongeval"), BOUCHON_UTILISATEUR("File - gemeld"), BOUCHON_AUTOMATIQUE("File - gedetecteerd"), RETRECISSEMENT("Wegversmalling"), INCIDENT_TRAFIC("Incident"),
            OBSTACLE("Obstakel"), DANGER("Gevaar"), VISIBILITE_REDUITE("Beperkt zicht"), ROUTE_GLISSANTE("Gladde weg"), CHAUSSEE_ABIMEE("Beschadigd wegdek"), NO_TYPE("No type found");

            String type;

            SubType(String type){
                this.type=type;
            }

            public static SubType valueOfSubtype(String s){
                try{
                    return SubType.valueOf(s.replaceAll(" ", "_").toUpperCase());
                }
                catch (Exception e){
                    return NO_TYPE;
                }
            }

            @Override
            public String toString(){
                return type;
            }
        }


    }

    enum WazeType implements ItemType{

        ROAD_CLOSED("Afsluiting"), WEATHERHAZARD("Risico"), ACCIDENT("Ongeval"), JAM("File"), NO_TYPE("");
        String type;

        WazeType(String type){
            this.type=type;
        }


        public static WazeType valueOfWazeType(String s){
            try{
                return WazeType.valueOf(s);
            }
            catch (Exception e){
                return NO_TYPE;
            }
        }

        @Override
        public String toString(){
            return type;
        }

        public enum SubType implements ItemType{
            HAZARD_ON_ROAD ("Probleem op de baan"), HAZARD_ON_SHOULDER ("Probleem op de vluchtstrook"), HAZARD_WEATHER ("Weerrisico"), HAZARD_ON_ROAD_OBJECT ("Object op de baan"),
            HAZARD_ON_ROAD_POT_HOLE("kuil op de baan"), HAZARD_ON_ROAD_ROAD_KILL("Dier op de baan"), HAZARD_ON_SHOULDER_CAR_STOPPED("Wagen op de pechstrook"),
            HAZARD_ON_SHOULDER_ANIMALS("Dier op de pechtstrook"), HAZARD_ON_SHOULDER_MISSING_SIGN("Missend bord"), HAZARD_WEATHER_FOG("Mist"), HAZARD_WEATHER_HAIL("Hagel"),
            HAZARD_WEATHER_HEAVY_RAIN("Zware regen"), HAZARD_WEATHER_HEAVY_SNOW("Zware sneeuw"), HAZARD_WEATHER_FLOOD("Overstroming"), HAZARD_WEATHER_MONSOON("Zware overstroming"),
            HAZARD_WEATHER_TORNADO("Tornado"), HAZARD_WEATHER_HEAT_WAVE("Hittegolf"),HAZARD_WEATHER_HURRICANE("Orkaan"), HAZARD_WEATHER_FREEZING_RAIN("Ijzel"),
            HAZARD_ON_ROAD_LANE_CLOSED("Afgesloten"), HAZARD_ON_ROAD_OIL("Oliegevaar"), HAZARD_ON_ROAD_ICE("Ijzel"), HAZARD_ON_ROAD_CONSTRUCTION("Werken"), HAZARD_ON_ROAD_CAR_STOPPED("Stilstaande wagen"),

            JAM_HEAVY_TRAFFIC("Zware file"), JAM_STAND_STILL_TRAFFIC("Stilstaande file"), JAM_LIGHT_TRAFFIC("Lichte file"),JAM_MODERATE_TRAFFIC("Middelmatige File"),

            ACCIDENT_MINOR("Licht accident"), ACCIDENT_MAJOR("Zwaar accident"),

            ROAD_CLOSED_CONSTRUCTION("Wegenwerken"),ROAD_CLOSED_EVENT("Afsluiting"), ROAD_CLOSED_HAZARD("Gevaar"),

            NO_TYPE("");

            String type;

            SubType(String type){
                this.type=type;
            }

            public static SubType valueOfSubtype(String s){
                try{
                    return SubType.valueOf(s);
                }
                catch (Exception e){
                    return NO_TYPE;
                }
            }

            @Override
            public String toString(){
                return type;
            }
        }

        public enum RoadType implements ItemType{
            STREET("Straat"), PRIMARYSTREET("Primaire straat"),FREEWAY("Snelweg"), RAMP("Oprit"), TRAIL("Aardeweg"), PRIMARY("Primaire straat"), SECONDARY("Straat"),
            FOURBYFOURROAD("Aardeweg"), FERRY("Rond punt"), WALKWAY("Voetpad"), PEDESTRIAN("Voetpad"), EXIT("Afrit"), STAIRWAY("Trap"), PRIVATEROAD("Privéweg"), RAILROAD("Spoorweg"),
            RUNWAY("Landingsbaan"), PARKINGLOT("Parking"), SERVICEROAD("Dienstweg"), NO_TYPE("");


            String type;
            RoadType(String type){
                this.type=type;
            }

            public static RoadType valueOfRoadType(int i){
                try{
                    switch (i) {

                        case 1: return STREET;

                        case 2: return PRIMARYSTREET;

                        case 3: return FREEWAY;
                        case 4: return RAMP;

                        case 5: return TRAIL;
                        case 6: return PRIMARY;
                        case 7: return SECONDARY;
                        case 8: case 14: return FOURBYFOURROAD;
                        case 15: return FERRY;
                        case 9:return WALKWAY;
                        case 10: return PEDESTRIAN;
                        case 11: return EXIT;
                        case 16: return STAIRWAY;
                        case 17: return PRIVATEROAD;
                        case 18: return RAILROAD;
                        case 19: return RUNWAY;
                        case 20: return PARKINGLOT;
                        case 21: return SERVICEROAD;
                        default: return NO_TYPE;
                    }

                }
                catch (Exception e){
                    return NO_TYPE;
                }
            }

            @Override
            public String toString() {
                return type;
            }
        }

    }

}
