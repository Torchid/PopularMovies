package com.direyorkie.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.direyorkie.popularmovies.com.direyorkie.popularmovies.utilities.Constants;
import com.direyorkie.popularmovies.com.direyorkie.popularmovies.utilities.ImageAdapter;
import com.direyorkie.popularmovies.com.direyorkie.popularmovies.utilities.Movie;

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

//Fragment containing a grid view of popular movie posters
public class PostersFragment extends Fragment {

    private final String LOG_TAG = PostersFragment.class.getSimpleName();
    ImageAdapter moviePostersAdapter;
    ArrayList<Movie> movies = new ArrayList<>();
    private final String POSTER_PATH_BASE = "http://image.tmdb.org/t/p/w780";

    public PostersFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_posters, container, false);

        moviePostersAdapter = new ImageAdapter(getActivity(), R.layout.grid_item_poster, R.id.grid_item_poster, new ArrayList<String>());

        GridView gridOfPosters = (GridView) rootView.findViewById(R.id.gridview_posters);
        gridOfPosters.setAdapter(moviePostersAdapter);
        gridOfPosters.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v(LOG_TAG, movies.get(position).title);
                Intent detailIntent = new Intent(getActivity(), DetailsActivity.class).putExtra(Constants.MOVIES_DETAIL_EXTRA, movies.get(position));
                startActivity(detailIntent);
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updatePosters();
    }

    public void updatePosters(){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = settings.getString(getString(R.string.preference_sort_key), getString(R.string.preference_sort_default));

        FetchPosterTask fetchPosterTask = new FetchPosterTask();
        fetchPosterTask.execute(sortOrder);
    }

    public class FetchPosterTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        public final String LOG_TAG = FetchPosterTask.class.getSimpleName();

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviePosterJSON = null;

            final String POSTER_BASE_URL = "https://api.themoviedb.org/3/discover/movie?";
            final String KEY_PARAM = "api_key";
            final String SORT_PARAM = "sort_by";
            String sortType;

            Resources res = getResources();
            String[] sortOptions = res.getStringArray(R.array.preference_sort_values);

            if(params[0].equals(sortOptions[0])){
                sortType = "popularity.desc";
            }
            else{
                sortType = "vote_average.desc";
            }

            Uri builtUri = Uri.parse(POSTER_BASE_URL)
                    .buildUpon()
                    .appendQueryParameter(KEY_PARAM, getString(R.string.api_key))
                    .appendQueryParameter(SORT_PARAM, sortType)
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
                return getMovieDataFromJson(moviePosterJSON);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> moviesFromJSON) {
            super.onPostExecute(moviesFromJSON);
            ArrayList<String> moviePosters = new ArrayList<>();

            for(Movie mov: moviesFromJSON){
                moviePosters.add(mov.poster);
            }

            if(moviePosters != null) {
                moviePostersAdapter.clear();
                moviePostersAdapter.addAll(moviePosters);
            }
            movies = new ArrayList<>(moviesFromJSON);
        }

        private ArrayList<Movie> getMovieDataFromJson(String moviePosterJSON) throws JSONException {
            // These are the names of the JSON objects that need to be extracted.
            final String TMDb_RESULTS = "results";
            final String TMDb_POSTER_PATH = "poster_path";
            final String TMDb_TITLE = "title";
            final String TMDb_VOTE_AVERAGE = "vote_average";
            final String TMDb_RELEASE_DATE = "release_date";
            final String TMDb_OVERVIEW = "overview";

            JSONObject popularMoviesJSON = new JSONObject(moviePosterJSON);
            JSONArray moviesJSONArray = popularMoviesJSON.getJSONArray(TMDb_RESULTS);
            ArrayList<Movie> movieData = new ArrayList<>();

            for(int i = 0; i < moviesJSONArray.length(); i++){
                Movie newMovie = new Movie();

                JSONObject movieJSON = moviesJSONArray.getJSONObject(i);
                newMovie.poster = (POSTER_PATH_BASE + movieJSON.getString(TMDb_POSTER_PATH));
                newMovie.title = movieJSON.getString(TMDb_TITLE);
                newMovie.year = movieJSON.getString(TMDb_RELEASE_DATE).substring(0, 4);
                newMovie.length = "120min";
                newMovie.rating = movieJSON.getString(TMDb_VOTE_AVERAGE) + "/10";
                newMovie.overview = movieJSON.getString(TMDb_OVERVIEW);
                movieData.add(newMovie);
            }

            return movieData;
        }
    }
}
