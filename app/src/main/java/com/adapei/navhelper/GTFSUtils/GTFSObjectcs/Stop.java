package com.adapei.navhelper.GTFSUtils.GTFSObjectcs;

import com.google.android.gms.maps.model.LatLng;

public class Stop {

    private String stop_id;
    private int stop_code;
    private String stop_name;
    private String stop_desc;
    private LatLng stop_LatLng;
    private int zone_id;
    private String stop_url;
    private int location_type;
    private int parent_station;
    private String stop_timezone;
    private int wheelchair_boarding;
    private int level_id;
    private int plateform_code;

    public Stop(String stop_id,
                String stop_code,
                String stop_name,
                String stop_desc,
                String stop_lat,
                String stop_lon,
                String zone_id,
                String stop_url,
                String location_type,
                String parent_station,
                String stop_timezone,
                String wheelchair_boarding,
                String level_id,
                String platform_code
                ){

        this.stop_id = stop_id;
        this.stop_code = (stop_code.length()<1)? -1 : Integer.parseInt(stop_code);
        this.stop_name = stop_name;
        this.stop_desc = stop_desc;
        if(stop_lat.length()>0 && stop_lon.length()>0){
            this.stop_LatLng = new LatLng(Double.parseDouble(stop_lat),Double.parseDouble(stop_lon));
        }
        this.zone_id = (zone_id.length()<1)? -1 : Integer.parseInt(zone_id);
        this.stop_url = stop_url;
        this.location_type = (location_type.length()<1)? -1 : Integer.parseInt(location_type);
        this.parent_station = (parent_station.length()<1)? -1 : Integer.parseInt(parent_station);
        this.stop_timezone = stop_timezone;
        this.wheelchair_boarding = (wheelchair_boarding.length()<1)? -1 :Integer.parseInt(wheelchair_boarding);
        this.level_id = (level_id.length()<1)? -1 :Integer.parseInt(level_id);
        this.plateform_code = (platform_code.length()<1)? -1 :Integer.parseInt(platform_code);


    }

    public String getStop_id() {
        return stop_id;
    }

    public int getStop_code() {
        return stop_code;
    }

    public String getStop_name() {
        return stop_name;
    }

    public String getStop_desc() {
        return stop_desc;
    }

    public LatLng getStop_LatLng() {
        return stop_LatLng;
    }

    public int getZone_id() {
        return zone_id;
    }

    public String getStop_url() {
        return stop_url;
    }

    public int getLocation_type() {
        return location_type;
    }

    public int getParent_station() {
        return parent_station;
    }

    public String getStop_timezone() {
        return stop_timezone;
    }

    public int getWheelchair_boarding() {
        return wheelchair_boarding;
    }

    public int getLevel_id() {
        return level_id;
    }

    public int getPlateform_code() {
        return plateform_code;
    }

    @Override
    public String toString() {
        return "Stop{" +
                "stop_id=" + stop_id +
                ", stop_code=" + stop_code +
                ", stop_name='" + stop_name + '\'' +
                ", stop_desc='" + stop_desc + '\'' +
                ", stop_LatLng=" + stop_LatLng +
                ", zone_id=" + zone_id +
                ", stop_url='" + stop_url + '\'' +
                ", location_type=" + location_type +
                ", parent_station=" + parent_station +
                ", stop_timezone='" + stop_timezone + '\'' +
                ", wheelchair_boarding=" + wheelchair_boarding +
                ", level_id=" + level_id +
                ", plateform_code=" + plateform_code +
                '}';
    }
}
