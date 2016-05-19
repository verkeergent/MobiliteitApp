package com.stadgent.mobiliteitapp.model;

import com.stadgent.mobiliteitapp.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by floriangoeteyn on 15-Feb-16.
 */
public class TwitterItem extends Item {
    private String id_str;
    private Coordinates coordinates;
    private TwitterUser user;
    private TwitterItem retweeted_status;
    private String text;
    private String created_at;
    private String in_reply_to_status_id_str;
    private TwitterEntities entities;
    private boolean isMention = false;



    @Override
    public String getId() {
        return id_str;
    }

    @Override
    public String getTitle() {
        return "Twitter - "+user.getName();
    }

    @Override
    public String getDescription() {
        text = text.replaceAll("&amp;","&");
        return text;
    }

    @Override
    public String getDetails() {
        if(retweeted_status==null)
            return getDescription();
        else
            return retweeted_status.getDescription();
    }

    @Override
    public Date getDate() {
        DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZ yyyy", Locale.ENGLISH);
            try {
                return df.parse(this.created_at);
            } catch (ParseException e) {
                return new Date();
            }
    }

    @Override
    public ItemType getType() {
        if(isMention)
            return ItemType.TwitterType.TWITTER_MENTION;
        else
            return ItemType.TwitterType.TWITTER_TWEET;
    }

    @Override
    public int getColor() {
        return R.color.twitter;
    }

    @Override
    public int getIcon(){
        if(isMention)
            return R.drawable.twitter_icon_mention;
        else
            return R.drawable.twitter_icon_tweet;
    }

    @Override
    public Location getLocation() {
        if(coordinates!=null&&coordinates.coordinates!=null&&coordinates.coordinates.size()>=2)
            return new Location(coordinates.coordinates.get(0), coordinates.coordinates.get(1));
        else
            return null;
    }

    public String getMediaUrl() {
        String mediaUrl = "";
        if(entities!=null&&entities.getMedia()!=null&&entities.getMedia().size()>0){
            mediaUrl = entities.getMedia().get(0).getMedia_url();
        }
        return mediaUrl;
    }

    public TwitterUser getUser(){
        return user;
        /*if(retweeted_status==null)
            return user;
        else
            return retweeted_status.user;*/
    }

    public String getUserImageUrl() {
        if(retweeted_status==null)
            return user.getProfile_image_url();
        else
            return retweeted_status.user.getProfile_image_url();
    }

    public String getInReplyToStatusId(){
        return in_reply_to_status_id_str;
    }


    private class TwitterEntities {
        List<TwitterMedia> media;

        public List<TwitterMedia> getMedia() {
            return media;
        }

        private class TwitterMedia {

            private String id_string;
            private String media_url;

            public String getId_string() {
                return id_string;
            }

            public String getMedia_url() {
                return media_url;
            }
        }
    }

    public class TwitterUser {
        private String id_str;
        private String name;
        private String screen_name;
        private String profile_image_url;

        public String getName() {
            return name;
        }
        public String getScreenname() {
            return screen_name;
        }

        public String getProfile_image_url() {
            return profile_image_url;
        }

        public String getId() {
            return id_str;
        }
    }

    private class Coordinates{
        private String type;
        private List<Double> coordinates;
    }




}
