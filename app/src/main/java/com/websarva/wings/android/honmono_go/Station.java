package com.websarva.wings.android.honmono_go;

public class Station {

    private String id;
    private String name;
    private String lon;
    private String lat;

    public Station(String id, String name, String lon, String lat) {
        this.id = id;
        this.name = name;
        this.lon = lon;
        this.lat = lat;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    //経度
    public String getLon() {
        return lon;
    }
    public void setLon(String lon) {
        this.lon = lon;
    }

    //緯度
    public String getLat() {
        return lat;
    }
    public void setLat(String lat) {
        this.lat = lat;
    }

    //to display object as a string in spinner
    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Prefectures){
            Prefectures c = (Prefectures )obj;
            if(c.getName().equals(name) && c.getId()==id ) return true;
        }
        return false;
    }
}
