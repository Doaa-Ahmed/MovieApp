package doaaahmed.movie_app;

import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MovieJsonParser {
    String j_data;
    ArrayList<Movie> data;
    public MovieJsonParser(String s) {
        j_data = s;
        data = new ArrayList();
    }

    public void parse() throws JSONException {
        JSONObject json = new JSONObject(j_data);
        JSONArray results = json.getJSONArray("results");

        JSONObject jsonObject = null;
        for(int i = 0; i< results.length(); i++){
            jsonObject = results.getJSONObject(i);
            String title = jsonObject.getString("original_title");
            String poster = jsonObject.getString("poster_path");
            String plot = jsonObject.getString("overview");
            String rate = jsonObject.get("vote_average").toString();
            String date = jsonObject.get("release_date").toString();
            String id = jsonObject.getString("id");
            data.add(new Movie(id, rate, title, poster, plot, date));
        }
    }

    public ArrayList<Movie> getData() {
        return data;
    }
}
