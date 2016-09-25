package doaaahmed.movie_app;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;


public class FavouriteParser {

    private String favourite_json;
    ArrayList<Movie> movie_ids;

    public FavouriteParser(String favourite_json) {
        this.favourite_json = favourite_json;
        movie_ids = new ArrayList();
    }

    public String getFavourite_json() {
        return favourite_json;
    }

    public void setFavourite_json(String favourite_json) {
        this.favourite_json = favourite_json;
    }

    public ArrayList<Movie> getMovie_ids() {
        return movie_ids;
    }

    public void setMovie_ids(ArrayList<Movie> movie_ids) {
        this.movie_ids = movie_ids;
    }

    @Override
    public String toString() {
        return "FavouriteParser{" +
                "favourite_json='" + favourite_json + '\'' +
                ", movie_ids=" + movie_ids +
                '}';
    }

    public void parse() throws JSONException {
        JSONArray list = new JSONArray(favourite_json);
        for (int i = 0; i < list.length(); i++) {
            JSONObject obj = list.getJSONObject(i);
            Type t = new TypeToken<Movie>(){}.getType();
            movie_ids.add((Movie) new Gson().fromJson(obj.toString(), t));
        }
    }
}
