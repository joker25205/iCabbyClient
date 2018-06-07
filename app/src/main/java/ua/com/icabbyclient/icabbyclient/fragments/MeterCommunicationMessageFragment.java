package ua.com.icabbyclient.icabbyclient.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ua.com.icabbyclient.icabbyclient.R;
import ua.com.icabbyclient.icabbyclient.adapters.LogsAdapter;

public class MeterCommunicationMessageFragment extends Fragment {

    private RecyclerView mRecyclerView;

    List<String> mLogsList = new ArrayList<>();
    LogsAdapter mLogsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_meter_comunication, container, false);

        mRecyclerView = view.findViewById(R.id.logs_recycler_view);
        mLogsAdapter = new LogsAdapter(mLogsList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mLogsAdapter);
        return view;
    }

    public void updateList(final List<String> list) {
        mLogsList = list;
        if (mLogsAdapter != null)
            mLogsAdapter.notifyDataSetChanged();
    }
}
