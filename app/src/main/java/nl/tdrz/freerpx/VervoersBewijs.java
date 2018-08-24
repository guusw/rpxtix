package nl.tdrz.freerpx;

import java.io.Serializable;
import java.math.BigDecimal;

public final class VervoersBewijs implements Serializable {
    public String backFileUrl;
    public String frontFileUrl;
    public String initials;
    public String lastName;
    public BigDecimal price;
    public String productName;
    public String secutixMobilePDFLink;
    public String ticketProduct;
    public String ticketType;
    public String travelClass;
};