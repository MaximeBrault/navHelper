package com.adapei.navhelper.GTFSUtils.GTFSObjectcs;


public class Route {

    private String route_id;
    private String agency_id;
    private String route_short_name;
    private String route_long_name;
    private String route_desc;
    private String route_type;
    private String route_url;
    private String route_color;
    private String route_text_color;
    private String route_sort_order;

    public Route(String route_id,
                 String agency_id,
                 String route_short_name,
                 String route_long_name,
                 String route_desc,
                 String route_type,
                 String route_url,
                 String route_color,
                 String route_text_color,
                 String route_sort_order) {

        this.route_id = route_id;
        this.agency_id = agency_id;
        this.route_short_name = route_short_name;
        this.route_long_name = route_long_name;
        this.route_desc = route_desc;
        this.route_type = route_type;
        this.route_url = route_url;
        this.route_color = route_color;
        this.route_text_color = route_text_color;
        this.route_sort_order = route_sort_order;

        //rajouter la pondération par la durée de trajet

    }


    public String getRoute_id() {
        return route_id;
    }

    public String getAgency_id() {
        return agency_id;
    }

    public String getRoute_short_name() {
        return route_short_name;
    }

    public String getRoute_long_name() {
        return route_long_name;
    }

    public String getRoute_desc() {
        return route_desc;
    }

    public String getRoute_type() {
        return route_type;
    }

    public String getRoute_url() {
        return route_url;
    }

    public String getRoute_color() {
        return route_color;
    }

    public String getRoute_text_color() {
        return route_text_color;
    }

    public String getRoute_sort_order() {
        return route_sort_order;
    }

    @Override
    public String toString() {
        return "route{" +
                "route_id=" + route_id +
                ", agency_id=" + agency_id +
                ", route_short_name='" + route_short_name + '\'' +
                ", route_long_name='" + route_long_name + '\'' +
                ", route_desc='" + route_desc + '\'' +
                ", route_type=" + route_type +
                ", route_url='" + route_url + '\'' +
                ", route_color='" + route_color + '\'' +
                ", route_text_color='" + route_text_color + '\'' +
                ", route_sort_order='" + route_sort_order + '\'' +
                '}';
    }
}
