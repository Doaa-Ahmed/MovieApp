package doaaahmed.movie_app;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsActivityFragment extends Fragment {

    ImageView image;
    TextView plot;
    TextView title;
    TextView date;
    TextView rate;
    CheckBox favourite;

    ArrayList<RT> reviews_trailers;
    RTAdapter adapter;

    private ArrayList<Movie> fav_ids;
    private RecyclerView recycler;
    Movie movie;

    public DetailsActivityFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle m = this.getArguments();
        if (m == null) {
            m = getActivity().getIntent().getBundleExtra("movie");
        }
        if (m != null) {
            movie = (Movie)m.getParcelable("movie");

            View v = inflater.inflate(R.layout.fragment_details, container, false);
            image = (ImageView) v.findViewById(R.id.posterthumb);
            plot = (TextView) v.findViewById(R.id.plot);
            title = (TextView) v.findViewById(R.id.title);
            date = (TextView) v.findViewById(R.id.date);
            rate = (TextView) v.findViewById(R.id.rate);
            favourite = (CheckBox) v.findViewById(R.id.favourite);

            recycler = (RecyclerView)v.findViewById(R.id.info_list);
            RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());

            recycler.setLayoutManager(manager);

            adapter = new RTAdapter(getActivity(), new ArrayList<RT>());
            recycler.setAdapter(adapter);

            ReviewTasker tasker = new ReviewTasker(movie.getId());
            tasker.execute();

            final SharedPreferences prefs = getActivity().getSharedPreferences("favourite_data", 0);
            final SharedPreferences.Editor editor = prefs.edit();

            FavouriteParser parser;
            final String favourite_json = prefs.getString("fav", null);
            if (favourite_json != null) {
                parser = new FavouriteParser(favourite_json);
                try {
                    parser.parse();
                    fav_ids = parser.getMovie_ids();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (fav_ids != null) {
                favourite.setChecked(findMovieInFav(movie.getId()));
            }
            else {
                favourite.setChecked(false);
            }

            favourite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    String favs = prefs.getString("fav", null);
                    String movie_json = new Gson().toJson(movie);
                    if (b) {
                        if (favs == null) {
                            JSONArray ar = new JSONArray();
                            try {
                                ar.put(new JSONObject(movie_json));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            editor.putString("fav", ar.toString());
                            editor.apply();
                        }
                        else { // append
                            try {
                                JSONArray ar = new JSONArray(favs);
                                ar.put(new JSONObject(movie_json));
                                editor.putString("fav", ar.toString());
                                editor.apply();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    else {
                        try {
                            JSONArray ar = new JSONArray(favs);
                            int index = findIndx(ar, movie.getId());
                            if (index >= 0) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    ar.remove(index);
                                    editor.putString("fav", ar.toString());
                                    editor.apply();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            Picasso.with(getContext())
                    .load(movie.getPoster())
                    .resize(500,800)
                    .centerCrop()
                    .into(image);
            plot.setText(movie.getPlot());
            title.setText(movie.getTitle());
            date.setText(movie.getDate());
            rate.setText(movie.getRate());

            return v;
        }
        else {
            return inflater.inflate(R.layout.empty_fragment, container, false);
        }
    }

    private int findIndx(JSONArray ar, String id) throws JSONException {
        for (int i = 0; i < ar.length(); i++) {
            JSONObject obj = ar.getJSONObject(i);
            if (obj.getString("id").equalsIgnoreCase(id)) {
                return i;
            }
        }
        return -1;
    }

    private boolean findMovieInFav(String req) {
        for (Movie mov : fav_ids) {
            if (mov.getId().equalsIgnoreCase(req)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    class ReviewTasker extends AsyncTask<String,String,String> {

        String id;
        StringBuilder stringBuilder;


        ReviewTasker(String id) {
            this.id = id;
        }

        @Override
        protected String doInBackground(String... strings) {

            String uri = "http://api.themoviedb.org/3/movie/" + id + "/reviews?api_key=be0168c8674961cf754ebc2b5850f61c";
            URL url = null;
            try {
                url = new URL(uri);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                reviews_trailers = new RJsonParser(stringBuilder.toString()).getData();

            }
            catch (Exception e) {
                Log.e("error_in", e.toString());
            }
            finally{
                urlConnection.disconnect();

            }
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TrailerTasker tasker = new TrailerTasker(movie.getId());
            tasker.execute();
        }
    }

    private class TrailerTasker extends AsyncTask<String,String,String> {

        String id;
        private StringBuilder stringBuilder;

        public TrailerTasker(String id) {
            this.id = id;
        }

        @Override
        protected String doInBackground(String... strings) {
            String uri = "http://api.themoviedb.org/3/movie/" + id + "/videos?api_key=be0168c8674961cf754ebc2b5850f61c";
            URL url = null;
            try {
                url = new URL(uri);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                reviews_trailers.addAll(new TJsonParser(stringBuilder.toString()).getResults());

            }
            catch (Exception e) {
                Log.e("error_in", e.toString());
            }
            finally{
                urlConnection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            adapter.updateList(reviews_trailers);
        }
    }
}
