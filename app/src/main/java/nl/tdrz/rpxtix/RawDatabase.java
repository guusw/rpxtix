package nl.tdrz.rpxtix;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RawDatabase implements Serializable {
    public List<RawDatabaseEntry> entries = new ArrayList<>();

    public static class RawDatabaseEntry implements Serializable {
        Ticket ticket = null;

        public RawDatabaseEntry(Ticket ticket) {
            this.ticket = ticket;
        }
    }
}