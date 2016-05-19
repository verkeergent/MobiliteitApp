package com.stadgent.mobiliteitapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by floriangoeteyn on 06-Apr-16.
 */
public class User {
    private String _id;
    private String username;
    private boolean isAdmin;
    private List<Permission> permissions;

    public String getId() {
        return _id;
    }

    public User(String username){
        this.username=username;
    }

    public String getUsername() {
        return username;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public List<PermissionItem> getPermissions(){
        List<PermissionItem> permissionlist=new ArrayList<>();
        for(Permission p: permissions){
            if(p.allowed)
            permissionlist.add(p.items);
        }
        return permissionlist;
    }

    private class Permission{
        PermissionItem items;
        boolean allowed;

    }

    enum PermissionItem{
        wazeItems, twitterItems, mailItems, parkingItems, coyoteItems, vgsItems
    }

}
