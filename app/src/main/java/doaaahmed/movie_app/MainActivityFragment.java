package doaaahmed.movie_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private final String popular_URL= "http://api.themoviedb.org/3/movie/popular?api_key=be0168c8674961cf754ebc2b5850f61c";
    private final String topRated_URL= "http://api.themoviedb.org/3/movie/top_rated?api_key=be0168c8674961cf754ebc2b5850f61c";

    public static ArrayList<Movie> movies = new ArrayList<>();


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    static Context context ;

    SharedPreferences prefs;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        mLayoutManager = new GridLayoutManager(getContext(),4);
        mRecyclerView.setLayoutManager(mLayoutManager);

        MainActivity main = (MainActivity) getActivity();
        FragmentRefreshListener frag = new FragmentRefreshListener() {
            @Override
            public void onRefresh() {
                if (movies != null) {
                    movies.clear();
                    prefs = getActivity().getSharedPreferences("settings",0);
                    boolean pop = prefs.getBoolean("popularity", false);
                    boolean top = prefs.getBoolean("top_rated", false);
                    boolean fav = prefs.getBoolean("favourite", false);
                    if (!fav) {
                        if (pop && ! top) {
                            MoviesViewerTask task_worker = new MoviesViewerTask(popular_URL, true);
                            task_worker.execute();
                        }
                        else if (!pop && top){
                            MoviesViewerTask task_worker = new MoviesViewerTask(topRated_URL, true);
                            task_worker.execute();
                        }
                        else {
                            MoviesViewerTask task_worker = new MoviesViewerTask(popular_URL, true);
                            task_worker.execute();
                        }
                    }
                    else {
                        MoviesViewerTask task_worker = new MoviesViewerTask(popular_URL, false);
                        task_worker.execute();
                    }
                }
            }
        };
        main.setFragRefreshListener(frag);

        return view;
    }

    @Override
    public void onStart() {
        // to force the app to choose a default if no options were selected
        ((MainActivity)getActivity()).getFragRefreshListener().onRefresh();
        super.onStart();
    }

    private class MoviesViewerTask extends AsyncTask<String, String, String> {
        String path;
        private StringBuilder stringBuilder;
        boolean state;

        @Override
        protected String doInBackground(String... strings) {

            try {
                if (state) {
                    URL url = new URL(path);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    try {
                        urlConnection.setRequestMethod("GET");
                        urlConnection.setDoInput(true);
                        urlConnection.setDoOutput(true);
                        urlConnection.connect();

                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        stringBuilder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                        bufferedReader.close();

                        MovieJsonParser parser = new MovieJsonParser(stringBuilder.toString());
                        parser.parse();
                        movies = parser.getData();
                        mAdapter = new MovieAdapter(getActivity(),getContext(), movies);
                    }
                    catch (Exception e) {
                        Log.e("error_in", e.toString());
                    }
                    finally{
                        urlConnection.disconnect();
                    }
                }
                else {
                    SharedPreferences pref = getActivity().getSharedPreferences("favourite_data", 0);
                    String fav_json = pref.getString("fav", null);
                    // The type we need the JSON Array to be converted to
                    // we need to convert our JSON Array "favorite" to list of Movies "class"
                    Type t = new TypeToken<List<Movie>>() {}.getType();
                    movies = new Gson().fromJson(fav_json, t);
                    mAdapter = new MovieAdapter(getActivity(),getContext(), movies);
                }
            }
            catch (Exception ex) {

            }

            return null;
        }

        public MoviesViewerTask(String url, boolean state) {
            super();
            path = url;
            this.state = state;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mRecyclerView.setAdapter(mAdapter);

        }
    }

    class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieHolder>{

        private ArrayList<Movie> movies;
        Context context;
        Activity activity;

        public MovieAdapter(Activity activity, Context context, ArrayList<Movie> data){
            this.movies = data;
            this.context = context;
            this.activity = activity;
        }

        public ArrayList<Movie> getMovies() {
            return movies;
        }

        public void setMovies(ArrayList<Movie> movies) {
            this.movies = movies;
        }

        public void add(int position, Movie item) {
            movies.add(position, item);
            notifyItemInserted(position);
        }

        public void remove(Movie item) {
            int position = movies.indexOf(item);
            movies.remove(position);
            notifyItemRemoved(position);
        }
        @Override
        public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_layout, parent, false);
            MovieHolder mh = new MovieHolder(v);
            return mh;
        }

        @Override
        public void onBindViewHolder(MovieHolder holder, final int position) {
            Picasso.with(context)
                    .load(movies.get(position).getPoster())
                    .into(holder.poster);

            holder.poster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Bundle data = new Bundle();
                    data.putParcelable("movie", movies.get(position));

                    // for tablet
                    if (getActivity().findViewById(R.id.details_frame) != null) {
                        FragmentTransaction t = getActivity().getSupportFragmentManager()
                                .beginTransaction();
                        Fragment mFrag = new DetailsActivityFragment();
                        mFrag.setArguments(data);

                        t.replace(R.id.details_frame,mFrag);
                        t.commit();

                    } else {
                        Intent intent = new Intent(activity.getApplicationContext(), DetailsActivity.class);
                        intent.putExtra("movie", data);
                        activity.startActivity(intent);

                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            if (movies == null) {
                return 0;
            }
            return movies.size();
        }

        public class MovieHolder extends RecyclerView.ViewHolder{

            public ImageView poster;

            public MovieHolder(View itemView) {
                super(itemView);
                poster = (ImageView) itemView.findViewById(R.id.imageView);
            }
        }
    }

}
