package com.direyorkie.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    ArrayAdapter<String> moviePostersAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        moviePostersAdapter = new ArrayAdapter<String>(getActivity(), R.layout.grid_item_poster,
                R.id.grid_item_poster, new ArrayList<String>());

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

    public class FetchPosterTask extends AsyncTask<Void, Void, Void> {

        public final String LOG_TAG = FetchPosterTask.class.getSimpleName();

        @Override
        protected Void doInBackground(Void... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviePosterJSON = null;
            //https://api.themoviedb.org/3/movie/550?api_key=7584a1f46a5e53997bad82c438715667
            final String POSTER_BASE_URL = "https://api.themoviedb.org/3/movie/550?";
            final String API_KEY = "7584a1f46a5e53997bad82c438715667";
            final String KEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(POSTER_BASE_URL)
                    .buildUpon()
                    .appendQueryParameter(KEY_PARAM, API_KEY)
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
                    Log.v(LOG_TAG, "No input stream!!!");
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
                    Log.v(LOG_TAG, "Stream was empty!!!");
                }
                moviePosterJSON = buffer.toString();
                Log.v(LOG_TAG, moviePosterJSON);
            }  catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                moviePosterJSON = null;
                Log.v(LOG_TAG, "Failed to get weather data!!!");
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

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }
}
