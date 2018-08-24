package nl.tdrz.rpxtix;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nl.tdrz.rpxtix.TicketItemFragment.OnListFragmentInteractionListener;
import nl.tdrz.rpxtix.TicketContent.TicketItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link TicketItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyTicketItemRecyclerViewAdapter extends RecyclerView.Adapter<MyTicketItemRecyclerViewAdapter.ViewHolder> {

    private final List<TicketItem> values;
    private final OnListFragmentInteractionListener listener;

    public MyTicketItemRecyclerViewAdapter(List<TicketItem> items, OnListFragmentInteractionListener listener) {
        values = items;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_ticketitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = values.get(position);
        holder.routeText.setText(values.get(position).getRouteStringShort());
        holder.dateText.setText(values.get(position).ticket.travelDate);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    listener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view ;
        public final TextView routeText;
        public final TextView dateText;
        public TicketItem mItem;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            routeText = view.findViewById(R.id.routeText);
            dateText = view.findViewById(R.id.dateText);
        }
    }
}
