package ique.daitechagent.parcels;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MinisterPikin on 07-Sep-19.
 */
public class MapCustomerDetailsEnvelope implements Parcelable {

    private String customerName;
    private String customerNumber;
    private String customerAddress;
    private double lng;
    private double lat;

    public static final int SINGLE_POINT_PLOT = 0;
    public static final int MULTI_POINT_PLOT = 1;


    public MapCustomerDetailsEnvelope(String customerName, String customerNumber,
                                      String customerAddress, double lng, double lat ){

        this.customerName = customerName;
        this.customerNumber = customerNumber;
        this.customerAddress = customerAddress;
        this.lat = lat;
        this.lng = lng;

    }

    public MapCustomerDetailsEnvelope(Parcel parcel) {
        customerName = parcel.readString();
        customerNumber = parcel.readString();
        customerAddress = parcel.readString();

        lat = parcel.readDouble();
        lng = parcel.readDouble();
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(customerName);
        parcel.writeString(customerNumber);
        parcel.writeString(customerAddress);

        parcel.writeDouble(lat);
        parcel.writeDouble(lng);
    }

    /**
     * Retrieving Map Report data from Parcel object
     * This constructor is invoked by the method createFromParcel(Parcel source) of
     * the object CREATOR
     **/
    public static final Parcelable.Creator<MapCustomerDetailsEnvelope> CREATOR = new Parcelable.Creator<MapCustomerDetailsEnvelope>() {

        @Override
        public MapCustomerDetailsEnvelope createFromParcel(Parcel source) {
            return new MapCustomerDetailsEnvelope(source);
        }

        @Override
        public MapCustomerDetailsEnvelope[] newArray(int size) {
            return new MapCustomerDetailsEnvelope[0];
        }
    };
}
