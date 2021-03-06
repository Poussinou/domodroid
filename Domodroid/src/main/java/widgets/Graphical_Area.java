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
package widgets;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.domogik.domodroid13.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Abstract.common_method;
import Entity.Entity_Room;
import adapter.ArrayAdapterWithIcon;
import database.Cache_management;
import database.DmdContentProvider;
import database.DomodroidDB;
import misc.List_Icon_Adapter;
import misc.tracerengine;

public class Graphical_Area extends Basic_Graphical_zone implements OnLongClickListener {

    private final FrameLayout container = null;
    private final Context context;
    private final int id_area;
    private tracerengine Tracer = null;
    private String mytag = "Graphical_Area";
    private String icon;
    private final Activity Activity;
    private final SharedPreferences params;
    private final Handler widgetHandler;
    private final DomodroidDB domodb;
    private final SharedPreferences.Editor prefEditor;

    public Graphical_Area(SharedPreferences params, tracerengine Trac, Context context, int id, String name_area, String description_area, String icon, int widgetSize, Handler handler) {
        super(Trac, context, id, name_area, description_area, icon, widgetSize, "area", handler);
        FrameLayout myself = this;
        this.Tracer = Trac;
        this.icon = icon;
        this.id_area = id;
        this.context = context;
        this.Activity = (android.app.Activity) context;
        this.params = params;
        this.widgetHandler = handler;
        setOnLongClickListener(this);
        domodb = new DomodroidDB(this.Tracer, this.Activity, params);
        prefEditor = this.params.edit();
        mytag = "Graphical_Area(" + id_area + ")";
    }

    public boolean onLongClick(View v) {
        final AlertDialog.Builder list_type_choice = new AlertDialog.Builder(getContext());
        final String[] String_list_action = new String[]{context.getString(R.string.change_icon), context.getString(R.string.rename),
                context.getString(R.string.delete), context.getString(R.string.move_up), context.getString(R.string.move_down)};
        final Integer[] Integer_list_action_icon = new Integer[]{R.drawable.ic_rounded_corner_black, R.drawable.ic_description_black,
                R.drawable.ic_delete_black, R.drawable.ic_arrow_upward_black, R.drawable.ic_arrow_downward_black};
        ListAdapter adapter = new ArrayAdapterWithIcon(context, String_list_action, Integer_list_action_icon);
        list_type_choice.setTitle(R.string.Area_longclic_menu_title);
        list_type_choice.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        do_action(String_list_action[item]);
                        dialog.cancel();
                    }
                }
        );

        list_type_choice.show();
        return false;
    }

    private void do_action(String action) {
        if (action.equals(context.getString(R.string.delete))) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle(context.getString(R.string.Delete_feature_title) + " " + name);
            alert.setMessage(R.string.Delete_feature_message);
            alert.setPositiveButton(R.string.reloadOK, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog_customname, int whichButton) {
                    DomodroidDB domodb = new DomodroidDB(Tracer, Activity, params);
                    domodb.owner = "Widgets_Manager.loadRoomWidgets";
                    Tracer.v(mytag, "load widgets for area " + id_area);
                    Entity_Room[] listRoom = domodb.requestRoom(id_area);

                    for (Entity_Room room : listRoom) {
                        Tracer.get_engine().remove_one_things(room.getId(), "room");
                        Tracer.get_engine().remove_one_place_type_in_Featureassociation(room.getId(), "room");
                        Tracer.get_engine().remove_one_icon(room.getId(), "room");
                    }

                    Tracer.get_engine().remove_one_things(id_area, "area");
                    Tracer.get_engine().remove_one_place_type_in_Featureassociation(id_area, "area");
                    Tracer.get_engine().remove_one_icon(id_area, "area");
                    // #76
                    prefEditor.putString("AREA_LIST", domodb.request_json_Area().toString());
                    prefEditor.putString("ROOM_LIST", domodb.request_json_Room().toString());
                    prefEditor.putString("ICON_LIST", domodb.request_json_Icon().toString());
                    prefEditor.putString("FEATURE_LIST_association", domodb.request_json_Features_association().toString());
                    common_method.save_params_to_file(Tracer, prefEditor, mytag, getContext());
                    // recheck cache element to remove those no more need.
                    Cache_management.checkcache(Tracer, Activity);
                    //Refresh the view
                    Bundle b = new Bundle();
                    b.putBoolean("refresh", true);
                    Message msg = new Message();
                    msg.setData(b);
                    widgetHandler.sendMessage(msg);
                }
            });
            alert.setNegativeButton(R.string.reloadNO, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog_customname, int whichButton) {
                    Tracer.v(mytag, "delete Canceled.");
                }
            });
            alert.show();
        } else if (action.equals(context.getString(R.string.rename))) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle(context.getString(R.string.Rename_title) + " " + name);
            alert.setMessage(R.string.Rename_message);
            // Set an EditText view to get user input
            final EditText input = new EditText(getContext());
            alert.setView(input);
            alert.setPositiveButton(R.string.reloadOK, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog_customname, int whichButton) {
                    String result = input.getText().toString();
                    Tracer.get_engine().descUpdate(id_area, result, "area");
                    //#76
                    prefEditor.putString("AREA_LIST", domodb.request_json_Area().toString());
                    common_method.save_params_to_file(Tracer, prefEditor, mytag, getContext());
                    TV_name.setText(result);
                }
            });
            alert.setNegativeButton(R.string.reloadNO, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog_customname, int whichButton) {
                    Tracer.v(mytag, "Customname Canceled.");
                }
            });
            alert.show();
        } else if (action.equals(context.getString(R.string.change_icon))) {
            final AlertDialog.Builder list_icon_choice = new AlertDialog.Builder(getContext());
            List<String> list_icon = new ArrayList<>();
            String[] fiilliste;
            fiilliste = context.getResources().getStringArray(R.array.icon_area_array);
            Collections.addAll(list_icon, fiilliste);
            final CharSequence[] char_list_icon = list_icon.toArray(new String[list_icon.size()]);
            list_icon_choice.setTitle(context.getString(R.string.Wich_ICON_message) + " " + name);
            List_Icon_Adapter adapter = new List_Icon_Adapter(Tracer, getContext(), fiilliste, fiilliste);
            list_icon_choice.setAdapter(adapter, null);
            list_icon_choice.setSingleChoiceItems(char_list_icon, -1,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            ListView lw = ((AlertDialog) dialog).getListView();
                            Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());
                            icon = checkedItem.toString();
                            ContentValues values = new ContentValues();
                            //type = area, room, feature
                            values.put("name", "area");
                            //icon is the name of the icon wich will be select
                            values.put("value", icon);
                            //reference is the id of the area, room, or feature
                            int reference = 0;
                            reference = id_area;
                            values.put("reference", reference);
                            context.getContentResolver().insert(DmdContentProvider.CONTENT_URI_UPDATE_ICON_NAME, values);
                            // #76
                            prefEditor.putString("ICON_LIST", domodb.request_json_Icon().toString());
                            common_method.save_params_to_file(Tracer, prefEditor, mytag, getContext());
                            change_this_icon(icon);
                            dialog.cancel();
                        }
                    }
            );
            AlertDialog alert_list_icon = list_icon_choice.create();
            alert_list_icon.show();
        } else if (action.equals(context.getString(R.string.move_down))) {
            Tracer.d(mytag, "moving down");
            Tracer.get_engine().move_one_area(id_area, 0, "area", "down");
            prefEditor.putString("AREA_LIST", domodb.request_json_Area().toString());
            common_method.save_params_to_file(Tracer, prefEditor, mytag, getContext());
            common_method.refresh_the_views(widgetHandler);
        } else if (action.equals(context.getString(R.string.move_up))) {
            Tracer.d(mytag, "moving up");
            Tracer.get_engine().move_one_area(id_area, 0, "area", "up");
            prefEditor.putString("AREA_LIST", domodb.request_json_Area().toString());
            common_method.save_params_to_file(Tracer, prefEditor, mytag, getContext());
            common_method.refresh_the_views(widgetHandler);
        }
    }
}


