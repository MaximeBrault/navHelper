package com.adapei.navhelper.GTFSUtils.GTFSObjectcs;

public class Trip {

    private int route_id;
    private int service_id;
    private int trip_id;
    private String trip_headsign;
    private String trip_short_name;
    private int direction_id;
    private int block_id;
    private int shape_id;
    private int wheelchair_accessible;
    private int bikes_allowed;



    public Trip(String route_id,
                String service_id,
                String trip_id,
                String trip_headsign,
                String trip_short_name,
                String direction_id,
                String block_id,
                String shape_id,
                String wheelchair_accessible,
                String bikes_allowed){

        this.route_id = (route_id.length()<1)? -1 : Integer.parseInt(route_id);
        this.service_id = (service_id.length()<1)? -1 : Integer.parseInt(service_id);
        this.trip_id= (trip_id.length()<1)? -1 : Integer.parseInt(trip_id);
        this.trip_headsign = trip_headsign;
        this.trip_short_name = trip_short_name;
        this.direction_id = (direction_id.length()<1)? -1 : Integer.parseInt(direction_id);
        this.block_id = (block_id.length()<1)? -1 : Integer.parseInt(block_id);
        this.shape_id = (shape_id.length()<1)? -1 : Integer.parseInt(shape_id);
        this.wheelchair_accessible = (wheelchair_accessible.length()<1)? -1 : Integer.parseInt(wheelchair_accessible);
        this.bikes_allowed = (bikes_allowed.length()<1)? -1 : Integer.parseInt(bikes_allowed);

    }

    public int getRoute_id() {
        return route_id;
    }

    public int getService_id() {
        return service_id;
    }

    public int getTrip_id() {
        return trip_id;
    }

    public String getTrip_headsign() {
        return trip_headsign;
    }

    public String getTrip_short_name() {
        return trip_short_name;
    }

    public int getDirection_id() {
        return direction_id;
    }

    public int getBlock_id() {
        return block_id;
    }

    public int getShape_id() {
        return shape_id;
    }

    public int getWheelchair_accessible() {
        return wheelchair_accessible;
    }

    public int getBikes_allowed() {
        return bikes_allowed;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "route_id=" + route_id +
                ", service_id=" + service_id +
                ", trip_id=" + trip_id +
                ", trip_headsign='" + trip_headsign + '\'' +
                ", trip_short_name='" + trip_short_name + '\'' +
                ", direction_id=" + direction_id +
                ", block_id=" + block_id +
                ", shape_id=" + shape_id +
                ", wheelchair_accessible=" + wheelchair_accessible +
                ", bikes_allowed=" + bikes_allowed +
                '}';
    }
}
