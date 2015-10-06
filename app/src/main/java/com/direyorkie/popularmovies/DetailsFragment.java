package com.direyorkie.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.direyorkie.popularmovies.com.direyorkie.popularmovies.utilities.Constants;
import com.direyorkie.popularmovies.com.direyorkie.popularmovies.utilities.Movie;
import com.squareup.picasso.Picasso;


public class DetailsFragment extends Fragment {

    private Movie movie;

    public DetailsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);

        Bundle bundle = this.getArguments();
        movie = bundle.getParcelable(Constants.MOVIES_DETAIL_EXTRA);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        ((TextView) rootView.findViewById(R.id.movie_title)).setText(movie.title);
        ((TextView) rootView.findViewById(R.id.movie_data)).setText(movie.data);
        ((TextView) rootView.findViewById(R.id.movie_overview)).setText(movie.overview);
        Picasso.with(getActivity()).load(movie.poster).noPlaceholder().into((ImageView) rootView.findViewById(R.id.movie_poster));

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}

