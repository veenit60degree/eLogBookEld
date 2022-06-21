package com.constants;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.messaging.logistic.Globally;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CsvReader {

    public CsvReader(){
        super();
    }


    public static List<CanadaLocModel> canadaLocationList = new ArrayList<>();

    List<CanadaLocModel> getCanadaLocationList(Context context){
        String next[] = {};
        List<CanadaLocModel> list = new ArrayList<CanadaLocModel>();
        try {
            CSVReader reader = new CSVReader(new InputStreamReader(context.getAssets().open("geoloc.csv")));//Specify asset file name
            //in open();
            for(;;) {
                next = reader.readNext();
                if(next != null) {
                    String[] data = next[0].split(";");

                    CanadaLocModel locModel = new CanadaLocModel(data[0],data[1],data[2],data[3],data[4]);
                    list.add(locModel);

                } else {
                    break;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

     //   Log.d("list", "list: "+ list.size());
        return list;

    }


    public String getShortestAddress(Context context){

        String finalAddress = "";

        try {
            if (Globally.LATITUDE.length() > 4) {
                if (canadaLocationList.size() == 0) {
                    canadaLocationList = getCanadaLocationList(context);
                }

                CanadaLocModel dataModel = null;
                double distance = 0;
                if (canadaLocationList.size() > 1) {
                    for (int i = 1; i < canadaLocationList.size(); i++) {

                        double selectedLat = Double.parseDouble(canadaLocationList.get(i).getLatitude());
                        double selectedLong = Double.parseDouble(canadaLocationList.get(i).getLongitude());

                        if(Globally.LATITUDE.length() > 3) {
                            if (i == 1) {
                                dataModel = canadaLocationList.get(i);
                                distance = CalculateDistanceInCanada(Double.parseDouble(Globally.LATITUDE), Double.parseDouble(Globally.LONGITUDE),
                                        selectedLat, selectedLong);
                            } else {
                                double distanceCalculated = CalculateDistanceInCanada(Double.parseDouble(Globally.LATITUDE), Double.parseDouble(Globally.LONGITUDE),
                                        selectedLat, selectedLong);
                                if (distanceCalculated < distance) {
                                    dataModel = canadaLocationList.get(i);
                                    distance = distanceCalculated;
                                }
                            }
                        }
                    }
                }

                String stateName = GetStateByName(dataModel.getState());
                String degree = getLocationInDegree(Double.parseDouble(Globally.LATITUDE), Double.parseDouble(Globally.LONGITUDE));
                DecimalFormat dec = new DecimalFormat("0.0");
                //String finalAddress = dec.format(distance) + " mi from " + dataModel.getGeographicalName() + " " + stateName ;
                finalAddress = dec.format(distance) + " km " + degree + " " + stateName + " " + dataModel.getGeographicalName();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return finalAddress;
    }


    public double CalculateDistanceInCanada(double currentLat, double currentLon, double selectedLat, double selectedLon) {
        double distance = 0.0;

        try {
            double theta = currentLon - selectedLon;
            distance = (Math.sin(deg2rad(currentLat)) * Math.sin(deg2rad(selectedLat)) + Math.cos(deg2rad(currentLat)) * Math.cos(deg2rad(selectedLat)) * Math.cos(deg2rad(theta)));
            distance = Math.acos(distance);
            distance = rad2deg(distance);
            distance = distance * 60 * 1.1515;

          /*  if (unit.equals("K")) {
                distance = distance * 1.609344;
            } else if (unit.equals("M")) {
                distance = distance * 0.8684;
            }*/

            distance = distance * 1.609344;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return (distance);
    }


    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }



    public static String GetStateByName(String name)
    {
        try
        {
            switch (name.toUpperCase())
            {
                case "ALABAMA":
                    return "AL";

                case "ALASKA":
                    return "AK";

                case "AMERICAN SAMOA":
                    return "AS";

                case "ARIZONA":
                    return "AZ";

                case "ARKANSAS":
                    return "AR";

                case "CALIFORNIA":
                    return "CA";

                case "BAJA CALIFORNIA":
                    return "CA";

                case "COLORADO":
                    return "CO";

                case "CONNECTICUT":
                    return "CT";

                case "DELAWARE":
                    return "DE";

                case "DISTRICT OF COLUMBIA":
                    return "DC";

                case "FEDERATED STATES OF MICRONESIA":
                    return "FM";

                case "FLORIDA":
                    return "FL";

                case "GEORGIA":
                    return "GA";

                case "GUAM":
                    return "GU";

                case "HAWAII":
                    return "HI";

                case "IDAHO":
                    return "ID";

                case "ILLINOIS":
                    return "IL";

                case "INDIANA":
                    return "IN";

                case "IOWA":
                    return "IA";

                case "KANSAS":
                    return "KS";

                case "KENTUCKY":
                    return "KY";

                case "LOUISIANA":
                    return "LA";

                case "MAINE":
                    return "ME";

                case "MARSHALL ISLANDS":
                    return "MH";

                case "MARYLAND":
                    return "MD";

                case "MASSACHUSETTS":
                    return "MA";

                case "MICHIGAN":
                    return "MI";

                case "MINNESOTA":
                    return "MN";

                case "MISSISSIPPI":
                    return "MS";

                case "MISSOURI":
                    return "MO";

                case "MONTANA":
                    return "MT";

                case "NEBRASKA":
                    return "NE";

                case "NEVADA":
                    return "NV";

                case "NEW HAMPSHIRE":
                    return "NH";

                case "NEW JERSEY":
                    return "NJ";

                case "NEW MEXICO":
                    return "NM";

                case "NEW YORK":
                    return "NY";

                case "NORTH CAROLINA":
                    return "NC";

                case "NORTH DAKOTA":
                    return "ND";

                case "NORTHERN MARIANA ISLANDS":
                    return "MP";

                case "OHIO":
                    return "OH";

                case "OKLAHOMA":
                    return "OK";

                case "OREGON":
                    return "OR";

                case "PALAU":
                    return "PW";

                case "PENNSYLVANIA":
                    return "PA";

                case "PUERTO RICO":
                    return "PR";

                case "RHODE ISLAND":
                    return "RI";

                case "SOUTH CAROLINA":
                    return "SC";

                case "SOUTH DAKOTA":
                    return "SD";

                case "TENNESSEE":
                    return "TN";

                case "TEXAS":
                    return "TX";

                case "UTAH":
                    return "UT";

                case "VERMONT":
                    return "VT";

                case "VIRGIN ISLANDS":
                    return "VI";

                case "VIRGINIA":
                    return "VA";

                case "WASHINGTON":
                    return "WA";

                case "WEST VIRGINIA":
                    return "WV";

                case "WISCONSIN":
                    return "WI";

                case "WYOMING":
                    return "WY";
                case "ALBERTA":
                    return "AB";
                case "BRITISH COLUMBIA":
                    return "BC";
                case "MANITOBA":
                    return "MB";
                case "NEW BRUNSWICK":
                    return "NB";
                case "NEWFOUNDLAND AND LABRADOR":
                    return "NL";
                case "NOVA SCOTIA":
                    return "NS";
                case "NORTHWEST TERRITORIES":
                    return "NT";
                case "NUNAVUT":
                    return "NU";
                case "ONTARIO":
                    return "ON";
                case "PRINCE EDWARD ISLAND":
                    return "PE";
                case "QUEBEC":
                    return "QC";
                case "SASKATCHEWAN":
                    return "SK";
                case "YUKON":
                    return "YT";
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return "";
    }


    String getLocationInDegree(double latitude, double longitude) {
        try {
            int latSeconds = (int) Math.round(latitude * 3600);
            int latDegrees = latSeconds / 3600;
            int longSeconds = (int) Math.round(longitude * 3600);
            int longDegrees = longSeconds / 3600;
            String latDegree = latDegrees >= 0 ? "N" : "S";
            String lonDegrees = longDegrees >= 0 ? "E" : "W";
            String deg =  latDegree+ lonDegrees;

            return deg ;
        } catch (Exception e) {
            return "" ;
        }
    }



 /*   void getLocDegree(Location location){
        String strLongitude = location.convert(location.getLongitude(), location.FORMAT_SECONDS);
        String strLatitude = location.convert(location.getLatitude(), location.FORMAT_SECONDS);
        Log.d("Location", "---strLongitude: " + strLongitude );
        Log.d("Location", "---strLatitude: " + strLatitude );
        getFormattedLocationInDegree(location.getLatitude(), location.getLongitude());
    }

    String getFormattedLocationInDegree(double latitude, double longitude) {
        try {
            int latSeconds = (int) Math.round(latitude * 3600);
            int latDegrees = latSeconds / 3600;
            latSeconds = Math.abs(latSeconds % 3600);
            int latMinutes = latSeconds / 60;
            latSeconds %= 60;

            int longSeconds = (int) Math.round(longitude * 3600);
            int longDegrees = longSeconds / 3600;
            longSeconds = Math.abs(longSeconds % 3600);
            int longMinutes = longSeconds / 60;
            longSeconds %= 60;
            String latDegree = latDegrees >= 0 ? "N" : "S";
            String lonDegrees = longDegrees >= 0 ? "E" : "W";

            String degFormat = Math.abs(latDegrees) + "°" + latMinutes + "'" + latSeconds
                    + "\"" + latDegree +" "+ Math.abs(longDegrees) + "°" + longMinutes
                    + "'" + longSeconds + "\"" + lonDegrees;
            Log.d("LocationDegreeF", "---degFormat: " + degFormat );

            return degFormat ;
        } catch (Exception e) {
            return ""+ String.format("%8.5f", latitude) + "  "
                    + String.format("%8.5f", longitude) ;
        }
    }

*/
    private class CanadaLocModel{

        String GeographicalName;
        String Latitude;
        String Longitude;
        String GenericTerm;
        String State;

        public CanadaLocModel(String geographicalName, String latitude, String longitude, String genericTerm, String state) {
            GeographicalName = geographicalName;
            Latitude = latitude;
            Longitude = longitude;
            GenericTerm = genericTerm;
            State = state;
        }

        public String getGeographicalName() {
            return GeographicalName;
        }

        public String getLatitude() {
            return Latitude;
        }

        public String getLongitude() {
            return Longitude;
        }

        public String getGenericTerm() {
            return GenericTerm;
        }

        public String getState() {
            return State;
        }
    }



}
