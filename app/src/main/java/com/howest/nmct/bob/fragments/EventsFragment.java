package com.howest.nmct.bob.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.howest.nmct.bob.MainActivity;
import com.howest.nmct.bob.R;
import com.howest.nmct.bob.adapters.EventAdapter;
import com.howest.nmct.bob.collections.Events;
import com.howest.nmct.bob.collections.Rides;
import com.howest.nmct.bob.fragments.CreateRideDialogFragment.RideOptionSelectedListener;
import com.howest.nmct.bob.models.Event;
import com.howest.nmct.bob.models.Ride;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Nick on 28/10/2015.
 */
public class EventsFragment extends Fragment implements RideOptionSelectedListener {
    @Bind(R.id.list)
    RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public EventsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_events, container, false);
        ButterKnife.bind(this, view);
        initViews();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    private void initData() {
        Events.fetchData();
    }

    private void initViews() {
        if (mLayoutManager == null)
            mLayoutManager = new LinearLayoutManager(getActivity());

        if (mAdapter == null)
            mAdapter = new EventAdapter(this, Events.getEvents());

        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(mAdapter);
        }
    }

    public void onShowCreateRideDialog(Event event) {
        DialogFragment dialog = CreateRideDialogFragment.newInstance(this, event);
        dialog.show(getFragmentManager(), "CreateRideDialogFragment");
    }

    @Override
    public void onDialogBobClick(Event event) {
        MainActivity parentActivity = (MainActivity) getActivity();
        Ride newRide = Ride.createRideFromEvent(event, parentActivity.mUser);
        Rides.addRide(newRide);
        parentActivity.navigateToRides();
        parentActivity.navigatetoRideDetails(newRide);
    }

    @Override
    public void onDialogNotBobClick(Event mEvent) {

    }

    public void onEventSelected(Event event) {
        if (getView() == null) return;

        navigateToEventFragment(event);

    }

    private void navigateToEventFragment(Event event) {
        navigateToFragment(EventDetailsFragment.newInstance(event), true);
        getActivity().setTitle(event.getEventName());
    }


    public void navigateToFragment(Fragment fragment, Boolean addToManager) {
        getActivity().getSupportFragmentManager().popBackStack();

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(fragment.getClass().toString())
                .commit();
    }
}