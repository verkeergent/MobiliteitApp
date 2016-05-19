package com.stadgent.mobiliteitapp.model;

import com.stadgent.mobiliteitapp.R;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by floriangoeteyn on 15-Feb-16.
 */
public abstract class WazeItem extends Item {

    public abstract ItemType.WazeType getType();
    public abstract ItemType.WazeType.SubType getSubtype();

    public abstract ItemType.WazeType.RoadType getRoadType();


    public class WazeItemAlert extends WazeItem {
        private String country;
        private String city;
        private int reportRating;
        private int reliability;
        private String type;
        private String uuid;
        private String jamUuid;
        private int roadType;
        private int magvar;
        private String subtype;
        private String street;
        private String imageUrl;
        private String reportDescription;
        private Location location;
        private Long pubMillis;
        private boolean removed=false;

        private List<Location> line;
        private int length;



        @Override
        public String getId() {
            return uuid;
        }

        @Override
        public String getTitle() {
            return "Waze melding - " + getType();
        }

        @Override
        public String getDescription() {
            String description = "";
            if (getSubtype() != null && !getSubtype().toString().equals(""))
                description += getSubtype().toString()+" ";
            if(reportDescription!=null)
                description += reportDescription+" ";
            if (street != null && !street.equals(""))
                description += "op de " + street+" ";
            if (city != null && !city.equals(""))
                description += "in " + city;

            return description;
        }

        @Override
        public String getDetails() {
            String details = "";
            details += "reliability: " + reliability + " | ";
            details += "rating: "+reportRating;
            if (getRoadType() != null && !getRoadType().toString().equals("")) {
                details+=" | straat type: " + getRoadType().toString();
            }
            return details;
        }

        @Override
        public Date getDate() {
            return new Date(pubMillis);
        }

        @Override
        public  ItemType.WazeType getType() {
            return ItemType.WazeType.valueOfWazeType(type);
        }

        @Override
        public int getColor() {
            switch (getType()) {
                case ACCIDENT:
                    return R.color.accident;
                case JAM:
                    return R.color.jam;
                case ROAD_CLOSED:
                    return R.color.roadclosed;
                case WEATHERHAZARD:
                    return R.color.weatherhazard;
                case NO_TYPE:
                default:
                    return R.color.notype;
            }
        }

        @Override
        public ItemType.WazeType.SubType getSubtype() {
            return ItemType.WazeType.SubType.valueOfSubtype(subtype);
        }

        @Override
        public ItemType.WazeType.RoadType getRoadType() {
            return ItemType.WazeType.RoadType.valueOfRoadType(roadType);
        }

        @Override
        public Location getLocation() {
            return location;
        }

        @Override
        public int getIcon(){
            switch (getType()) {
                case ACCIDENT:
                    return R.drawable.waze_accident;
                case JAM:
                    switch (getSubtype()){
                        case JAM_HEAVY_TRAFFIC:case JAM_STAND_STILL_TRAFFIC:
                            return R.drawable.waze_traffic_heavy;
                        case JAM_MODERATE_TRAFFIC:
                            return R.drawable.waze_traffic_medium;
                        case JAM_LIGHT_TRAFFIC:
                            return R.drawable.waze_traffic_light;
                        default:
                            return R.drawable.waze_traffic;
                    }
                case ROAD_CLOSED:
                    return R.drawable.waze_roadclosed;
                case WEATHERHAZARD:
                    switch(getSubtype()){
                        case HAZARD_ON_SHOULDER: case HAZARD_ON_SHOULDER_CAR_STOPPED:
                            return R.drawable.waze_onshoulder;
                        case HAZARD_ON_ROAD_CONSTRUCTION:
                            return R.drawable.waze_construction;
                        default:
                            return R.drawable.waze_hazard;
                    }
                default:
                    return R.drawable.waze_hazard;
            }
        }

        @Override
        public List<Location> getLine() {
            return line;
        }

        public String getJamUuid() {
            return jamUuid;
        }

        @Override
        public boolean isRemoved() {
            return removed;
        }
    }

    public class WazeItemJam extends WazeItem {
        private String country;
        private String city;
        private String type;
        private String uuid;
        private int roadType;
        private String street;
        private List<Location> line;
        private int level;
        private double speed;
        private int length;
        private int delay;
        private String startNode;
        private String endNode;
        private List<Location> turnLine;
        private String turnType;
        private String blockingAlertUuid;
        private Long pubMillis;


        @Override
        public String getTitle() {
            return "Waze gedetecteerd - "+getType();
        }

        @Override
        public String getDescription() {
            String description = "";
            if (getSubtype() != null && !getSubtype().toString().equals(""))
                description += getSubtype().toString() + " ";
            if (street != null && !street.equals(""))
                description += "op de " + street + " ";
            if (startNode != null && !startNode.equals(""))
                description += "van " + startNode + " ";
            if (endNode != null && !endNode.equals(""))
                description += "tot " + endNode + " ";
            if (city != null && !city.equals(""))
                description += "in " + city;
                        if(speed>0)
                            description+=" ("+String.format("%.1f", speed) + " km/u)";

            return description;
        }

        @Override
        public String getDetails() {
            String details = "";
            if(speed>0)
                details += "snelheid: " + String.format("%.1f", speed) + " km/u | ";
            if(length>0) {
                String lengte;
                if(length>1000)
                    lengte = new DecimalFormat("#.#").format(length/1000.00f)+"km";
                else
                    lengte=length+"m";
                details += "lengte: " + lengte;
            }
            if (getRoadType() != null && !getRoadType().toString().equals("")) {
                details+=" | straat type: " + getRoadType().toString();
            }
            return details;
        }

        @Override
        public Date getDate() {
            return new Date(pubMillis);
        }

        @Override
        public String getId() {
            return uuid;
        }

        @Override
        public ItemType.WazeType getType() {

            if(blockingAlertUuid!=null&&!blockingAlertUuid.equals("")){
                return ItemType.WazeType.ROAD_CLOSED;
            }else {
                return ItemType.WazeType.JAM;
            }
        }

        @Override
        public int getColor() {
            return R.color.jam;
        }

        @Override
        public ItemType.WazeType.SubType getSubtype() {
            if(speed>0&&speed<5)
                return ItemType.WazeType.SubType.JAM_HEAVY_TRAFFIC;
            else if(speed>=5)
                return ItemType.WazeType.SubType.JAM_MODERATE_TRAFFIC;
            else
                return ItemType.WazeType.SubType.ROAD_CLOSED_EVENT;
        }

        @Override
        public ItemType.WazeType.RoadType getRoadType() {
            return ItemType.WazeType.RoadType.valueOfRoadType(roadType);
        }

        @Override
        public int getIcon(){
            if(getType().equals(ItemType.WazeType.ROAD_CLOSED))
                return R.drawable.waze_roadclosed;
            else
                switch (getSubtype()){
                    case JAM_HEAVY_TRAFFIC:case JAM_STAND_STILL_TRAFFIC:
                        return R.drawable.waze_traffic_heavy;
                    case JAM_MODERATE_TRAFFIC:
                        return R.drawable.waze_traffic_medium;
                    case JAM_LIGHT_TRAFFIC:
                        return R.drawable.waze_traffic_light;
                    default:
                        return R.drawable.waze_traffic;
                }
        }


        @Override
        public List<Location> getLine() {
            return line;
        }

        public int getLength() {
            return length;
        }

        public String getBlockingAlertUuid() {
            return blockingAlertUuid;
        }
    }


}




