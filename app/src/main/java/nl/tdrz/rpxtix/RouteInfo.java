package nl.tdrz.rpxtix;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public final class RouteInfo implements Serializable {
    @SerializedName("Carrier")
    public String carrier;
    @SerializedName(alternate = {"fromStationVerkorting"}, value = "FromStationShort")
    public String fromStationCode;
    @SerializedName(alternate = {"fromStation"}, value = "FromStation")
    public String fromStationName;
    @SerializedName(alternate = {"toStationVerkorting"}, value = "ToStationShort")
    public String toStationCode;
    @SerializedName(alternate = {"toStation"}, value = "ToStation")
    public String toStationName;
}
