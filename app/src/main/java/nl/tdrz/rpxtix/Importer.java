package nl.tdrz.rpxtix;

import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.Map;

public class Importer extends AppCompatActivity {
    private RequestQueue requestQueue;

    private TextView logTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_importer);

        logTextView = findViewById(R.id.logText);
        logTextView.setText("");

        Intent intent = getIntent();
        Uri importUri = intent.getData();
        if(importUri == null ) {
            // Test
            importUri = Uri.parse("rpx://ticketUpdate?orderID=3.YKktEepi8AAAFko9MMBP7U&travelerID=xRYKktEek60AAAFktNMMBP7U");
        }

        logTextView.append("Importing " + importUri.toString() + "\n");

        String orderID = importUri.getQueryParameter("orderID");
        String travelerID = importUri.getQueryParameter("travelerID");

        logTextView.append("orderID: " + orderID + "\n");
        logTextView.append("travelerID: " + travelerID + "\n");

        Uri uri = new Uri.Builder()
                .scheme("https")
                .path("tickets.ns-mlab.nl/api/v1/tickets/import")
//                .appendEncodedPath("https://tickets.ns-mlab.nl/api/v1/tickets/import")
                .appendQueryParameter("orderID", orderID)
                .appendQueryParameter("travelerID", travelerID).build();


        String requestUriString = uri.toString();
        logTextView.append("Sending request to: " + requestUriString + "\n");

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest req = new ImporterRequest(Request.Method.GET, requestUriString, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    logTextView.append("Parsing ticket response" +"\n");
                    try {
                        handleTicketResponse(response);
                    } catch (Exception e) {
                        logTextView.append("Failed to parse ticket response" + e + "\n");
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    logTextView.append("Failed to get ticket: " + error + "\n");
                }
            });

        requestQueue.add(req);
    }

    private void handleTicketResponse(String ticketData) throws Exception {
        Gson gson = new Gson();
        TicketResponse ticketResponse = gson.fromJson(ticketData, TicketResponse.class);
        if(ticketResponse.tickets.isEmpty()) {
            throw new Exception("No tickets returned in response");
        }

        Ticket ticket = ticketResponse.tickets.get(0);
        String ownerName = ticket.initials + " " + ticket.lastName;
        logTextView.append("Got ticket for: " + ownerName+ "\n");

        // Register ticket in database
        Database database = Database.getOrCreateInstance(getApplicationContext());
        database.addOrUpdateTicket(ticket);
        database.save();
        // Return to manager
        Intent intent = new Intent(getBaseContext(), Manager.class);
        startActivity(intent);
        finish();
    }
}
