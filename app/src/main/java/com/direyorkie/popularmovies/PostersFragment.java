package com.direyorkie.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.direyorkie.popularmovies.com.direyorkie.popularmovies.utilities.ImageAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;


//Fragment containing a grid view of popular movie posters
public class PostersFragment extends Fragment {

    ImageAdapter moviePostersAdapter;
    private final String POSTER_PATH_BASE = "http://image.tmdb.org/t/p/w342";

    public PostersFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.posters, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_posters, container, false);

        moviePostersAdapter = new ImageAdapter(getActivity(), R.layout.grid_item_poster, R.id.grid_item_poster, new ArrayList<String>());

        GridView gridOfPosters = (GridView) rootView.findViewById(R.id.gridview_posters);
        gridOfPosters.setAdapter(moviePostersAdapter);
       // gridOfPosters.setItemOnClickListener(new Adapter);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updatePosters();
    }

    public void updatePosters(){
        FetchPosterTask fetchPosterTask = new FetchPosterTask();
        fetchPosterTask.execute();
    }

    public class FetchPosterTask extends AsyncTask<Void, Void, String[]> {

        public final String LOG_TAG = FetchPosterTask.class.getSimpleName();
        public final int numOfPosters = 20;

        @Override
        protected String[] doInBackground(Void... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviePosterJSON = null;

            final String POSTER_BASE_URL = "https://api.themoviedb.org/3/discover/movie?";
            final String KEY_PARAM = "api_key";
            final String SORT_PARAM = "sort_by";

            Uri builtUri = Uri.parse(POSTER_BASE_URL)
                    .buildUpon()
                    .appendQueryParameter(KEY_PARAM, getString(R.string.api_key))
                    .appendQueryParameter(SORT_PARAM, "popularity.desc")
                    .build();

            try {
                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    moviePosterJSON = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    moviePosterJSON = null;
                }
                moviePosterJSON = buffer.toString();
                Log.v(LOG_TAG, moviePosterJSON);
            }  catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the movie data, there's no point in attempting
                // to parse it.
                moviePosterJSON = null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getPosterDataFromJson(moviePosterJSON, numOfPosters);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            if(strings != null) {
                moviePostersAdapter.clear();
                moviePostersAdapter.addAll(new ArrayList(Arrays.asList(strings)));
            }
        }

        private String[] getPosterDataFromJson(String moviePosterJSON, int numOfPosters) throws JSONException {
            // These are the names of the JSON objects that need to be extracted.
            final String TMDb_RESULTS = "results";
            final String TMDb_POSTER_PATH = "poster_path";

            JSONObject popularMoviesJSON = new JSONObject(moviePosterJSON);
            JSONArray moviesJSONArray = popularMoviesJSON.getJSONArray(TMDb_RESULTS);
            String[] resultStrs = new String[moviesJSONArray.length()];

            for(int i = 0; i < moviesJSONArray.length(); i++){
                String posterPath;

                JSONObject movieJSON = moviesJSONArray.getJSONObject(i);
                posterPath = movieJSON.getString(TMDb_POSTER_PATH);
                resultStrs[i] = POSTER_PATH_BASE + posterPath;
            }

            return resultStrs;
        }
    }
}
