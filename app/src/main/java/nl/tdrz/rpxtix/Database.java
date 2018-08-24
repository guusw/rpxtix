package nl.tdrz.rpxtix;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {
    private static final String DATABASE_FILE_NAME = "tickets.db";
    private static final String TAG = "rpxtix";

    private static Database instance;
    private RawDatabase raw = new RawDatabase();

    private List<Ticket> tickets = new ArrayList<>();
    private Map<String, Ticket> ticketsByID = new HashMap<>();

    private Context context;

    public static Database getOrCreateInstance(Context context) {
        if(instance == null) {
            instance = new Database(context);
        }
        return instance;
    }

    private Database(Context context) {
        this.context = context;

        FileInputStream stream = null;
        try {
            stream = context.openFileInput(DATABASE_FILE_NAME);
            StringWriter writer = new StringWriter();
            IOUtils.copy(stream, writer, Charset.defaultCharset());
            String jsonString = writer.toString();
            stream.close();

            Gson gson = new Gson();
            raw = gson.fromJson(jsonString, RawDatabase.class);
        } catch(Exception e) {
            try {
                if (stream != null) {
                    stream.close();
                }
            }catch (Exception e1) {
                Log.e(TAG, "Database: Failed to close database input stream", e1);
            }

            Log.e(TAG, "Database: Failed to open exiting database file", e);

            // Initialize empty database
            raw = new RawDatabase();
        }

        for (RawDatabase.RawDatabaseEntry dbEntry : raw.entries) {
            tickets.add(dbEntry.ticket);
            ticketsByID.put(dbEntry.ticket.travelerId, dbEntry.ticket);
        }
    }

    public List<Ticket> tickets() {
        return tickets;
    }

    public Ticket ticketByID(String travelerID) {
        return ticketsByID.get(travelerID);
    }

    public void deleteTicket(String ticketID) {
        Ticket ticket = ticketsByID.get(ticketID);
        if(ticket != null) {
            tickets.remove(ticket);
            ticketsByID.remove(ticketID);
        }
    }

    public void addOrUpdateTicket(Ticket ticket) {
        Ticket existingTicket = ticketsByID.get(ticket.travelerId);
        if(existingTicket != null) {
            tickets.set(tickets.indexOf(existingTicket), ticket);
        } else {
            tickets.add(ticket);
        }
        ticketsByID.put(ticket.travelerId, ticket);
    }

    public void save() throws Exception {
        raw.entries = new ArrayList<>();
        for (Ticket ticket:
             tickets) {
            raw.entries.add(new RawDatabase.RawDatabaseEntry(ticket));
        }

        FileOutputStream stream = null;
        try {
            stream = context.openFileOutput(DATABASE_FILE_NAME, 0);
            Gson gson = new Gson();
            String jsonData = gson.toJson(raw);
            stream.write(jsonData.getBytes(Charset.defaultCharset()));
            stream.close();
        }catch(Exception e) {
            if(stream != null) {
                stream.close();
            }
            Log.e(TAG, "save: Failed to save database", e);
            throw e;
        }
    }
}
