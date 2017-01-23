package com.example.justas.grabble;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.justas.grabble.ScoreFragment.OnListFragmentInteractionListener;
import com.example.justas.grabble.helper.Player;

import java.util.List;

public class LeaderboardRecyclerViewAdapter extends RecyclerView.Adapter<LeaderboardRecyclerViewAdapter.ViewHolder> {
    private final int evenColor = Color.rgb(250, 250, 250);
    private final int oddColor = Color.rgb(255, 255, 255);
    private final int goldColor = Color.rgb(255, 215, 0);
    private final int silverColor = Color.rgb(192, 192, 192);
    private final int bronzeColor = Color.rgb(205, 127, 50);


    private final List<Player> mValues;

    public LeaderboardRecyclerViewAdapter(List<Player> items, OnListFragmentInteractionListener listener) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_score, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mPositionView.setText(String.valueOf(position + 1));
        holder.mNameView.setText(mValues.get(position).getName());
        holder.mScoreView.setText(String.valueOf(mValues.get(position).getTotalPoints()));

        setBackgroundColor(holder.mView, position);
    }

    private void setBackgroundColor(View view, int position) {
        if (position == 0) {
            view.setBackgroundColor(goldColor);
        } else if (position == 1) {
            view.setBackgroundColor(silverColor);
        } else if (position == 2) {
            view.setBackgroundColor(bronzeColor);
        } else if (position % 2 == 0) {
            view.setBackgroundColor(evenColor);
        } else {
            view.setBackgroundColor(oddColor);
        }
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mPositionView;
        public final TextView mNameView;
        public final TextView mScoreView;
        public Player mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mPositionView = (TextView) view.findViewById(R.id.leaderboard_position);
            mNameView = (TextView) view.findViewById(R.id.leaderboard_name);
            mScoreView = (TextView) view.findViewById(R.id.leaderboard_score);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
