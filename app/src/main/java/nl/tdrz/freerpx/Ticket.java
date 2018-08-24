package nl.tdrz.freerpx;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Ticket implements Serializable {
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
}