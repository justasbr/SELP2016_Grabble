package com.example.justas.grabble;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.justas.grabble.helper.Leaderboard;
import com.example.justas.grabble.helper.LeaderboardFeed;
import com.example.justas.grabble.helper.Player;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ScoreFragment extends Fragment {
    private static final String LEADERBOARD_INDEX = "leaderboard_index";

    // TODO: Customize parameters
    private int mColumnCount = 1;

    private List<Player> mPlayers;
    private RecyclerView mRecyclerView;

    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ScoreFragment() {
    }

    public static ScoreFragment newInstance(int index) {
        ScoreFragment scoreFragment = new ScoreFragment();

        Bundle args = new Bundle();
        args.putInt(LEADERBOARD_INDEX, index);
        scoreFragment.setArguments(args);

        return scoreFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void fetchLeaderboard(int index) {
        String timeInterval = Leaderboard.getPathParameter(index);

        try {
            ServerService.getLeaderboard(timeInterval, new Callback<LeaderboardFeed>() {
                @Override
                public void onResponse(Call<LeaderboardFeed> call, Response<LeaderboardFeed> response) {
                    if (response.body() == null) {
                        return;
                    }

                    LeaderboardFeed leaderboardFeed = response.body();
                    mPlayers = leaderboardFeed.getLeaderboard();
                    Log.d("mPlayers", String.valueOf(mPlayers.size()));

                    if (mPlayers.isEmpty()) {
                        showWarning(getString(R.string.empty_leaderboard_text));
                    }
                    if (mRecyclerView != null) {
                        mRecyclerView.setAdapter(new LeaderboardRecyclerViewAdapter(mPlayers, mListener));
                    }

                }

                @Override
                public void onFailure(Call<LeaderboardFeed> call, Throwable t) {
                    Log.d("LEADERBOARD PARSING", t.toString());
                    showWarning(getString(R.string.leaderboard_failed_to_fetch));
                }
            });
        } catch (IOException e) {
            Log.d("LEADERBOARD PARSING", e.toString());
            showWarning(getString(R.string.leaderboard_failed_to_fetch));
        }
    }

    private void showWarning(String message) {
        if (message != null) {
            TextView warning = (TextView) getView().findViewById(R.id.leaderboard_warning);
            if (warning != null) {
                warning.setText(message);
                warning.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_score_list, container, false);

        int leaderboardIndex = getArguments().getInt(LEADERBOARD_INDEX, Leaderboard.ALL_TIME);
        fetchLeaderboard(leaderboardIndex);

        View recyclerView = view.findViewById(R.id.leaderboard_recyclerview);
        // Set the adapter
        if (recyclerView instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) recyclerView;
            if (mColumnCount <= 1) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
        }

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Player item);
    }
}
