package nl.tdrz.rpxtix;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Manager extends AppCompatActivity implements TicketItemFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Ticket Manager");
        actionBar.setDisplayShowTitleEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.recycler);

        Database database = Database.getOrCreateInstance(getApplicationContext());
        List<Ticket> tickets = database.tickets();

        List<TicketContent.TicketItem> ticketItems = new ArrayList<>();
        for (Ticket ticket : tickets) {
            ticketItems.add(new TicketContent.TicketItem(ticket));
        }

        recyclerView.setAdapter(new MyTicketItemRecyclerViewAdapter(ticketItems, this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    public void onListFragmentInteraction(TicketContent.TicketItem item) {
        Intent intent = new Intent(getBaseContext(), Viewer.class);
        intent.putExtra("ticket", (Parcelable) item.ticket);
        startActivity(intent);
    }
}
