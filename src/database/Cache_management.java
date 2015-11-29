package database;

import misc.tracerengine;
import widgets.Entity_Feature;
import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Cache_management {
	private static WidgetUpdate WU_widgetUpdate = null;
	public static String mytag="Cache_management";
	private static tracerengine Tracer = null;
	private static SharedPreferences sharedparams;
	private static Activity context;
	private static float api_version;

	public static void checkcache(tracerengine Trac, Activity Context){
		// Change UrlAccess to make cache more light.
		// 1st need to change when this urlupdate his create.
		// 2nd need to check if this entity_feature exist somewhere (in feature_map or feature_assotiation)
		// 3rd add it in path only if it is the case.
		// So when a user will remove it from association or map it will be removed from cache
		// And when it will be add, it will get back in cache. 
		Tracer = Trac;
		context = Context;
		sharedparams= PreferenceManager.getDefaultSharedPreferences(context);
		api_version=sharedparams.getFloat("API_VERSION", 0);
		String urlUpdate="";
		if (api_version <=0.6f){
			DomodroidDB db = new DomodroidDB(Tracer, context);
			int[] listFeature_Association = db.requestAllFeatures_association();
			Entity_Feature[] listFeature = db.requestFeatures();
			urlUpdate = sharedparams.getString("URL","1.1.1.1")+"stats/multi/";
			Tracer.i(mytag, "urlupdate= "+urlUpdate);
			int compteur=0;
			for (Entity_Feature feature : listFeature) {
				for (int i=0;i<listFeature_Association.length;i++) {
					if (feature.getId()==listFeature_Association[i]){
						if (!feature.getState_key().equals("")){
							urlUpdate = urlUpdate.concat(feature.getDevId()+"/"+feature.getState_key()+"/");
							compteur=compteur+1;
						}
					}

				}			
			}
			Tracer.v(mytag,"prepare UPDATE_URL items="+String.valueOf(compteur));
			Tracer.i(mytag, "urlupdate= "+urlUpdate);
			SharedPreferences.Editor prefEditor=sharedparams.edit();
			prefEditor.putString("UPDATE_URL", urlUpdate);
			//need_refresh = true;	// To notify main activity that screen must be refreshed
			prefEditor.commit();
			//TODO restart the cache-engine.
			//Empty it then refill it with right value
			WU_widgetUpdate = WidgetUpdate.getInstance();
			if(WU_widgetUpdate != null){
				WU_widgetUpdate.refreshNow();
				Tracer.d(mytag, "launching a widget update refresh");
			}else {
				WU_widgetUpdate.init(Tracer, Context, sharedparams);
				Tracer.d(mytag, "launching a widget update init");
			}
		}else if(api_version >=0.7f){
			urlUpdate = sharedparams.getString("URL","1.1.1.1")+"sensor/";
			SharedPreferences.Editor prefEditor=sharedparams.edit();
			prefEditor.putString("UPDATE_URL", urlUpdate);
			//need_refresh = true;	// To notify main activity that screen must be refreshed
			prefEditor.commit();
		}

		Tracer.v(mytag,"UPDATE_URL = "+urlUpdate);

	}
}