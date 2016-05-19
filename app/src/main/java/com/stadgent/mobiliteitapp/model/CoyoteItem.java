package com.stadgent.mobiliteitapp.model;

import com.stadgent.mobiliteitapp.R;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by floriangoeteyn on 14-Apr-16.
 */
public abstract class CoyoteItem extends Item {

    public abstract ItemType.CoyoteType.SubType getSubtype();


    @Override
    public abstract ItemType.CoyoteType getType();

    public class CoyoteAlert extends CoyoteItem {

        private String id;
        private String type_id;
        private double lat;
        private double lng;
        private String heading;
        private String speed_limit;
        private String road_name;
        private String first_declaration;
        private String last_confirmation;
        private String confirmations;
        private String length;
        private String type_lbl;

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getTitle() {
            return "Coyote - " + getType().toString();
        }

        @Override
        public String getDescription() {
            String description = "";
            description+= getSubtype()+" ";
            if (road_name != null && !road_name.equals(""))
                description += "op " + road_name;
            return description;
        }

        @Override
        public String getDetails() {
            String details="";
            double _length = Double.parseDouble(length);
            if(_length>0) {
                String lengte;
                if (_length > 1000)
                    lengte = new DecimalFormat("#.#").format(_length/1000.0f) + "km";
                else
                    lengte = length + "m";
                details+="lengte: "+lengte+" | ";
            }

            return details +" | bevestigingen: "+confirmations;
        }

        @Override
        public Date getDate() {

            //DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZ yyyy", Locale.ENGLISH);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            try {
                return df.parse(this.first_declaration);
            } catch (ParseException e) {
                return new Date();
            }
        }

        @Override
        public Location getLocation() {
            return new Location(lng, lat);
        }

        @Override
        public int getIcon() {

            switch (getSubtype()) {
                case ACCIDENT:
                    return R.drawable.coyote_accident;
                case BOUCHON_AUTOMATIQUE: case BOUCHON_UTILISATEUR:
                    return R.drawable.coyote_jam;
                case INCIDENT:
                case INCIDENT_TRAFIC:
                case OBSTACLE:
                case DANGER:
                case VISIBILITE_REDUITE:
                case ROUTE_GLISSANTE:
                case CHAUSSEE_ABIMEE:
                    return R.drawable.coyote_incident;
                case RETRECISSEMENT:
                    return R.drawable.coyote_retricement;
                default:
                    return R.drawable.coyote_icon;
            }
        }


        @Override
        public ItemType.CoyoteType.SubType getSubtype() {
            return ItemType.CoyoteType.SubType.valueOfSubtype(type_lbl);
        }

        @Override
        public ItemType.CoyoteType getType() {
            switch (getSubtype()) {
                case ACCIDENT:
                    return ItemType.CoyoteType.ACCIDENT;
                case BOUCHON_AUTOMATIQUE: case BOUCHON_UTILISATEUR:
                    return ItemType.CoyoteType.JAM;
                case INCIDENT:
                case INCIDENT_TRAFIC:
                case OBSTACLE:
                case DANGER:
                case VISIBILITE_REDUITE:
                case ROUTE_GLISSANTE:
                case CHAUSSEE_ABIMEE:
                    return ItemType.CoyoteType.HAZARD;
                case RETRECISSEMENT:
                    return ItemType.CoyoteType.RETRECISSEMENT;
                default:
                    return ItemType.CoyoteType.NO_TYPE;
            }
        }

        @Override
        public int getColor() {
            switch (getType()) {
                case ACCIDENT:
                    return R.color.accident;
                case JAM:
                    return R.color.jam;
                case HAZARD:
                    return R.color.weatherhazard;
                case RETRECISSEMENT:
                    return R.color.roadclosed;
                case NO_TYPE:
                default:
                    return R.color.notype;
            }
        }
    }

    public class CoyoteTravelAlert extends CoyoteItem{

        private double normal_time;
        private double real_time;
        private double diff_time;
        private String road;
        private String id;
        private String date;
        private List<List<Geometry>> geometries;


        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getTitle() {
            return "Coyote - Vertraging";
        }

        @Override
        public String getDescription() {
            return Math.round(diff_time/60) + " min. vertraging op "+road;
        }

        @Override
        public String getDetails() {
            return "vertraging: "+ Math.round(diff_time/60) + " minuten";
        }

        @Override
        public Date getDate() {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            try {
                return df.parse(this.date);
            } catch (ParseException e) {
                return new Date();
            }
        }

        @Override
        public List<Location> getLine() {
            List<Location> line = new ArrayList<>();
            for(Geometry geo : geometries.get(0)){
                line.add(new Location(geo.lng, geo.lat));
            }

            return line;
        }

        @Override
        public int getIcon() {
            return R.drawable.coyote_jam;
        }

        @Override
        public ItemType.CoyoteType.SubType getSubtype() {
            return ItemType.CoyoteType.SubType.BOUCHON_AUTOMATIQUE;
        }

        @Override
        public ItemType.CoyoteType getType() {
            return ItemType.CoyoteType.JAM;
        }

        @Override
        public int getColor() {
            return R.color.jam;
        }


        class Geometry{
            private double lng;
            private double lat;
        }

    }


}
