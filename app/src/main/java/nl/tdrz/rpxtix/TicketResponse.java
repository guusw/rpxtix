package nl.tdrz.rpxtix;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TicketResponse implements Serializable {
    @SerializedName("ticket")
    List<Ticket> tickets = new ArrayList<>();
    String shareUrl;
}
