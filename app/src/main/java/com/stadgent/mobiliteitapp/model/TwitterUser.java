package com.stadgent.mobiliteitapp.model;

/**
 * Created by floriangoeteyn on 15-Mar-16.
 */
public class TwitterUser {
    private String id_str;
    private String name;
    private String screen_name;
    private String profile_image_url;

    public TwitterUser(String name, String profile_image_url) {
        this.name = name;
        this.profile_image_url = profile_image_url;
    }

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
