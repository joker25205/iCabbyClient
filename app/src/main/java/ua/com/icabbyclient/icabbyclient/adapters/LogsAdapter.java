package ua.com.icabbyclient.icabbyclient.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ua.com.icabbyclient.icabbyclient.R;

public class LogsAdapter extends RecyclerView.Adapter<LogsAdapter.Holder> {
    List<String> logs;


    public LogsAdapter(final List<String> logs) {
        this.logs = logs;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        return new Holder((TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.logs_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, final int position) {
        holder.mRootView.setText(logs.get(position));
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView mRootView;

        public Holder(final TextView itemView) {
            super(itemView);
            mRootView = itemView;
        }
    }
}
