package doaaahmed.movie_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

   /* public final String popular_URL= "http://api.themoviedb.org/3/movie/popular?api_key=be0168c8674961cf754ebc2b5850f61c";
    public final String topRated_URL= "http://api.themoviedb.org/3/movie/top_rated?api_key=be0168c8674961cf754ebc2b5850f61c";
   */

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    private FragmentRefreshListener fragRefreshListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefs = getSharedPreferences("settings", 0);
        editor = prefs.edit();

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int id = item.getItemId();
                switch (id){
                    case R.id.popularity:{
                        editor.putBoolean("popularity", true);
                        editor.putBoolean("top_rated", false);
                        editor.putBoolean("favourite", false);
                        editor.commit();
                        if(getFragRefreshListener()!=null){
                            getFragRefreshListener().onRefresh();
                        }
                        break;
                    }
                    case R.id.top_rated:{
                        editor.putBoolean("popularity", false);
                        editor.putBoolean("top_rated", true);
                        editor.putBoolean("favourite", false);

                        editor.commit();
                        if(getFragRefreshListener()!=null){
                            getFragRefreshListener().onRefresh();
                        }
                        break;
                    }
                    case R.id.fav_item:{
                        editor.putBoolean("favourite", true);
                        editor.putBoolean("popularity", false);
                        editor.putBoolean("top_rated", false);
                        editor.commit();

                        if(getFragRefreshListener()!=null){
                            getFragRefreshListener().onRefresh();
                        }
                        break;
                    }
                }
                return true;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.id.popularity:{
                editor.putBoolean("popularity", true);
                editor.putBoolean("top_rated", false);
                editor.apply();
                if(getFragRefreshListener()!=null){
                    getFragRefreshListener().onRefresh();
                }
                break;
            }
            case R.id.top_rated:{
                editor.putBoolean("popularity", false);
                editor.putBoolean("top_rated", true);
                editor.apply();
                if(getFragRefreshListener()!=null){
                    getFragRefreshListener().onRefresh();
                }
                break;
            }
        }
        return true;
    }*/

    public FragmentRefreshListener getFragRefreshListener() {
        return fragRefreshListener;
    }

    public void setFragRefreshListener(FragmentRefreshListener fragRefreshListener) {
        this.fragRefreshListener = fragRefreshListener;
    }
}
