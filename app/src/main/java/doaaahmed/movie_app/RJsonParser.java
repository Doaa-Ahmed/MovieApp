package doaaahmed.movie_app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class RJsonParser {

    private ArrayList<RT> data;
    String json;

    public RJsonParser(String s) {
        json = s;
    }

    public void parse() throws JSONException {
        JSONObject j = new JSONObject(json);
        JSONArray a = j.getJSONArray("results");
        data = new ArrayList();
        for (int i = 0; i < a.length(); i++) {
            data.add(new RT(false, a.getJSONObject(i).getString("author"), a.getJSONObject(i).getString("content")));
        }
    }

    public ArrayList<RT> getData() throws JSONException {
        parse();
        return data;
    }
}
