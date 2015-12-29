package com.howest.nmct.bob.fragments;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.howest.nmct.bob.R;
import com.howest.nmct.bob.activities.EventDetailsActivity;
import com.howest.nmct.bob.data.Contracts.EventEntry;
import com.howest.nmct.bob.data.Contracts.PlaceEntry;
import com.howest.nmct.bob.interfaces.EventActionsListener;
import com.howest.nmct.bob.models.Event;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventDetailsFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    @Bind(R.id.tvEventDetailsAddress) TextView tvEventDetailsAddress;
    @Bind(R.id.tvEventDetailsStartTime) TextView tvEventDetailsStartTime;
    @Bind(R.id.tvEventDetailsEndTime) TextView tvEventDetailsEndTime;
    @Bind(R.id.tvEventDetailsFriendsAndGuests) TextView tvEventDetailsFriendsAndGuests;
    @Bind(R.id.tvDescription) TextView tvDescription;

    @Bind(R.id.btnGoing) Button btnGoing;
    @Bind(R.id.btnInterested) Button btnInterested;
    @Bind(R.id.btnNotGoing) Button btnNotGoing;

    @Bind(R.id.bobBadge) View bobBadge;
    @Bind(R.id.bobBadgeSeparator) View bobBadgeSeparator;

    @Bind(R.id.endsAtContainer) View endsAtContainer;
    @Bind(R.id.endsAtContainerSeparator) View endsAtContainerSeparator;

    @Bind(R.id.startAtContainer) View startAtContainer;
    @Bind(R.id.startAtContainerSeparator) View startAtContainerSeparator;

    @Bind(R.id.locationContainer) View locationContainer;
    @Bind(R.id.locationContainerSeparator) View locationContainerSeparator;

    private String mEventId;
    private EventActionsListener mListener;
    private Cursor mCursor;
    private static final int URL_LOADER = 0;

    private static final String[] EVENT_COLUMNS = {
            EventEntry.TABLE_NAME + "." + EventEntry._ID,
            EventEntry.TABLE_NAME + "." + EventEntry.COLUMN_NAME,
            EventEntry.TABLE_NAME + "." + EventEntry.COLUMN_DESCRIPTION,
            EventEntry.TABLE_NAME + "." + EventEntry.COLUMN_START_TIME,
            EventEntry.TABLE_NAME + "." + EventEntry.COLUMN_END_TIME,
            EventEntry.TABLE_NAME + "." + EventEntry.COLUMN_COVER,
            EventEntry.TABLE_NAME + "." + EventEntry.COLUMN_ATTENDING_COUNT,
            EventEntry.TABLE_NAME + "." + EventEntry.COLUMN_INTERESTED_COUNT,
            EventEntry.TABLE_NAME + "." + EventEntry.COLUMN_DECLINED_COUNT,
            EventEntry.TABLE_NAME + "." + EventEntry.COLUMN_RSVP_STATUS,
            PlaceEntry.TABLE_NAME + "." + PlaceEntry.COLUMN_NAME,
    };

    public static final int COL_EVENT_ID = 0;
    public static final int COL_EVENT_NAME = 1;
    public static final int COL_EVENT_DESCCRIPTION = 2;
    public static final int COL_EVENT_START_TIME = 3;
    public static final int COL_EVENT_END_TIME = 4;
    public static final int COL_EVENT_COVER = 5;
    public static final int COL_EVENT_ATTENDING_COUNT= 6;
    public static final int COL_EVENT_INTERESTED_COUNT = 7;
    public static final int COL_EVENT_DECLINED_COUNT = 8;
    public static final int COL_EVENT_RSVP_STATUS = 9;
    public static final int COL_PLACE_NAME = 10;

    public EventDetailsFragment() {}

    public static EventDetailsFragment newInstance(String eventId) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        fragment.mEventId = eventId;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_event_details, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    public void setEvent(String eventId) {
        this.mEventId = eventId;
        getLoaderManager().restartLoader(URL_LOADER, null, this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListener = (EventActionsListener) getActivity();
        getLoaderManager().initLoader(URL_LOADER, null, this);
    }

    private void initViews() {
        if (mCursor == null) return;

        Date startTime = Event.parseDate(mCursor.getString(COL_EVENT_START_TIME));
        Date endTime = Event.parseDate(mCursor.getString(COL_EVENT_END_TIME));

        tvEventDetailsFriendsAndGuests.setText(String.format("%s are going.",
                        mCursor.getString(COL_EVENT_ATTENDING_COUNT)));

        bobBadge.setVisibility(View.GONE);
        bobBadgeSeparator.setVisibility(View.GONE);

        btnGoing.setText(String.format("Going\n(%d)", mCursor.getInt(COL_EVENT_ATTENDING_COUNT)));
        btnInterested.setText(String.format("Interested\n(%d)", mCursor.getInt(COL_EVENT_INTERESTED_COUNT)));
        btnNotGoing.setText(String.format("Not Going\n(%d)", mCursor.getInt(COL_EVENT_DECLINED_COUNT)));

        String location = mCursor.getString(COL_PLACE_NAME);
        if (location != null && !location.isEmpty()) {
            tvEventDetailsAddress.setText(mCursor.getString(COL_PLACE_NAME));
        } else {
            locationContainer.setVisibility(View.GONE);
            locationContainerSeparator.setVisibility(View.GONE);
        }

        if (startTime != null) {
            tvEventDetailsStartTime.setText(String.format("%s %s at %s",
                    Event.formatDate("EEE", startTime),
                    Event.formatDate("FF", startTime),
                    Event.formatDate("hh:mm a", startTime)));
        } else {
            startAtContainer.setVisibility(View.GONE);
            startAtContainerSeparator.setVisibility(View.GONE);
        }

        if (endTime != null) {
            tvEventDetailsEndTime.setText(String.format("%s %s at %s",
                    Event.formatDate("EEE", endTime),
                    Event.formatDate("FF", endTime),
                    Event.formatDate("hh:mm a", endTime)));
        } else {
            endsAtContainer.setVisibility(View.GONE);
            endsAtContainerSeparator.setVisibility(View.GONE);
        }

        tvDescription.setText(mCursor.getString(COL_EVENT_DESCCRIPTION));
    }

    @OnClick(R.id.btnGoing)
    public void onBtnGoingClick() {
        if (mListener != null) mListener.onGoing();
    }

    @OnClick(R.id.btnInterested)
    public void onBtnInterestedClick() {
        if (mListener != null) mListener.onInterested();
    }

    @OnClick(R.id.btnGoing)
    public void onBtnNotGoingClick() {
        if (mListener != null) mListener.onNotGoing();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case URL_LOADER:
                return new CursorLoader(
                        getContext(),
                        EventEntry.buildEventUri(mEventId),
                        EVENT_COLUMNS,
                        null,
                        null,
                        null
                );
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d("EventDetailsActivity", "Finished loading event " + data.getCount());
        if (data.moveToFirst()) {
            mCursor = data;
            initViews();
            ((EventDetailsActivity) getActivity()).initToolbar(
                    mCursor.getString(COL_EVENT_COVER),
                    mCursor.getString(COL_EVENT_NAME));
        } else {
            Toast.makeText(getContext(), "No event found", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}
}









