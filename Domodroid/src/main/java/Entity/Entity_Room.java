/*
 * This file is part of Domodroid.
 * 
 * Domodroid is Copyright (C) 2011 Pierre LAINE, Maxime CHOFARDET
 * 
 * Domodroid is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * Domodroid is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Domodroid. If not, see <http://www.gnu.org/licenses/>.
 */
package Entity;


import android.app.Activity;
import android.content.SharedPreferences;

import database.DomodroidDB;
import misc.tracerengine;

public class Entity_Room {
    private int area_id;
    private String description;
    private int id;
    private String name;
    private final Activity activity;
    private tracerengine Tracer = null;
    private final SharedPreferences params;


    public Entity_Room(SharedPreferences params, tracerengine Trac, Activity activity, int area_id, String description, int id, String name) {
        this.area_id = area_id;
        this.description = description;
        this.id = id;
        this.name = name;
        this.Tracer = Trac;
        this.activity = activity;
        this.params = params;
    }


    public int getArea_id() {
        return area_id;
    }


    public void setArea_id(int area_id) {
        this.area_id = area_id;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getIcon_name() {
        String iconName = "unknow";
        DomodroidDB domodb = new DomodroidDB(Tracer, activity, params);
        domodb.owner = "entity_room";
        try {
            iconName = domodb.requestIcons(id, "room").getValue();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return iconName;
    }

}
