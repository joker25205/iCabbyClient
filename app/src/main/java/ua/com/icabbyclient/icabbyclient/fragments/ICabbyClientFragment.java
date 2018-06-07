package ua.com.icabbyclient.icabbyclient.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import ua.com.icabbyclient.icabbyclient.R;
import ua.com.icabbyclient.icabbyclient.adapters.LogsAdapter;
import ua.com.icabbyclient.icabbyclient.utils.MeterCommand;


@SuppressLint("ValidFragment")
public class ICabbyClientFragment extends Fragment implements View.OnClickListener, BaseFragment {

    private static final String TRIP_ID = "A2514F2";

    private Button btnStartRide, btnStopRide, btnFinishRide, btnMiterUpdateTripInformation;
    private RecyclerView mRecyclerView;

    List<String> mLogsList = new ArrayList<>();
    LogsAdapter mLogsAdapter;

    Handler mHandler;
    MeterFunctions mMeterFunctions;

    public ICabbyClientFragment(MeterFunctions meterFunctions) {
        mMeterFunctions = meterFunctions;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_icabby_client, container, false);

        btnStartRide = view.findViewById(R.id.start_ride);
        btnStartRide.setOnClickListener(this);
        btnStopRide = view.findViewById(R.id.stop_ride);
        btnStopRide.setOnClickListener(this);
        btnFinishRide = view.findViewById(R.id.finish_ride);
        btnFinishRide.setOnClickListener(this);
        btnMiterUpdateTripInformation = view.findViewById(R.id.update_trip);
        btnMiterUpdateTripInformation.setOnClickListener(this);

        mRecyclerView = view.findViewById(R.id.logs_recycler_view);
        mLogsAdapter = new LogsAdapter(mLogsList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mLogsAdapter);

        return view;
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.start_ride:
                mMeterFunctions.setMeterCommand(MeterCommand.START_RIDE);
                break;
            case R.id.stop_ride:
                mMeterFunctions.setMeterCommand(MeterCommand.STOP_RIDE);
                break;
            case R.id.finish_ride:
                mMeterFunctions.setMeterCommand(MeterCommand.FINISH_RIDE);
                break;
            case R.id.update_trip:
                mMeterFunctions.setMeterCommand(MeterCommand.EXTRAS);
                break;
        }
    }

    @Override
    public void updateList(final List<String> list) {
        mLogsList = list;
        if (mLogsAdapter != null)
            mLogsAdapter.notifyDataSetChanged();
    }

    public interface MeterFunctions {
        void setMeterCommand(MeterCommand mMeterCommand);
    }
}
