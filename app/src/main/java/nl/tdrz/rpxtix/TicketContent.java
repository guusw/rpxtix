package nl.tdrz.rpxtix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.tdrz.rpxtix.Ticket;

public class TicketContent {
    public static final List<TicketItem> ITEMS = new ArrayList<TicketItem>();
    public static final Map<String, TicketItem> ITEM_MAP = new HashMap<String, TicketItem>();

    private static void addItem(TicketItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.ticket.travelerId, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class TicketItem {
        public final Ticket ticket;

        public TicketItem(Ticket ticket) {
            this.ticket = ticket;
        }

        public String getRouteStringShort() {
            return ticket.fromStationShort + " -> " + ticket.toStationShort;
        }
        public String getRouteString() {
            return ticket.fromStation + " -> " + ticket.toStation;
        }

        @Override
        public String toString() {
            return ticket.travelerId;
        }
    }
}
