package com.models;

public class InfoWindowModel  {
    String userId;
    String loungeName;
    String latitude;
    String longitude;
    int position;

    public InfoWindowModel(String userId,String loungeName,String latitude,String longitude,  int position){
        this.userId=userId;
        this.loungeName=loungeName;
        this.latitude=latitude;
        this.longitude=longitude;
        this.position = position;
    }



    public String getUserId(){
        return userId;
    }
    public void setUserId(String userId){
        this.userId=userId;
    }
    public String getLoungeName(){
        return loungeName;
    }
    public void setLoungeName(String loungeName){
        this.loungeName=loungeName;
    }
    public String getLatitude(){
        return latitude;
    }
    public void setLatitude(String latitude){
        this.latitude=latitude;
    }
    public String getLongitude(){
        return longitude;
    }
    public void setLongitude(String longitude){
        this.longitude=longitude;
    }


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }


}
