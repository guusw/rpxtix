package nl.tdrz.rpxtix;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Ticket implements Serializable, Parcelable {
    public String fromStationShort;
    public String toStationShort;
    public String travelerId;
    public String orderId;
    public String type;
    public String orderCreationDate;
    public String documentNo;
    public String travelDate;
    public String returnDate;
    public String productName;
    public String sapCode;
    public String travelClass;
    public String fromStation;
    public String toStation;
    public String routeChoice;
    public String initials;
    public String lastName;
    public String birthDate;
    public String secutixTicketId;
    public String secutixMobilePDFLink;
    public BigDecimal price;
    public String barcodeBase64;
    public String pdfLinkNl;
    public String pdfLinkEn;
    // routeInfo
    // iconImage

    public static Ticket fromResultJson(String ticketResponseData) throws Exception {
        Gson gson = new Gson();
        TicketResponse ticketResponse = gson.fromJson(ticketResponseData, TicketResponse.class);
        if(ticketResponse.tickets.isEmpty()) {
            throw new Exception("No tickets returned in response");
        }

        return ticketResponse.tickets.get(0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this);
    }


    public static final Parcelable.Creator<Ticket> CREATOR
            = new Parcelable.Creator<Ticket>() {
        public Ticket createFromParcel(Parcel in) {
            return (Ticket)in.readSerializable();
        }

        public Ticket[] newArray(int size) {
            return new Ticket[size];
        }
    };
}