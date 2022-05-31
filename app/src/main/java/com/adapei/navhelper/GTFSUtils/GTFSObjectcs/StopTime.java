package com.adapei.navhelper.GTFSUtils.GTFSObjectcs;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;

public class StopTime {

    private int trip_id;
    private LocalTime arrival_time;
    private LocalTime departure_time;
    private String stop_id;
    private int stop_sequence;
    private int stop_headsign;
    private int pickup_type;
    private int drop_off_type;


    @RequiresApi(api = Build.VERSION_CODES.O)
    public StopTime(String trip_id,
                    String arrival_time,
                    String departure_time,
                    String stop_id,
                    String stop_sequence,
                    String  stop_headsign,
                    String pickup_type,
                    String drop_off_type) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");

        this.trip_id = (trip_id.length()<1)? -1 : Integer.parseInt(trip_id);
        String[] temp = arrival_time.split(":");
        if (temp[0].equals("24")){
            temp[0] = "00";
        }
        String formatedArrival_Time = "";
        for (int i = 0;i<temp.length;i++){
            if (i < temp.length-1) {
                formatedArrival_Time = formatedArrival_Time + temp[i] + ":";
            }
            else{
                formatedArrival_Time = formatedArrival_Time + temp[i];
            }

        }
        this.arrival_time =  LocalTime.parse(formatedArrival_Time);

        String[] temp2 = departure_time.split(":");
        if (temp2[0].equals("24")){
            temp2[0] = "00";
        }
        String formatedDepartureTime = "";
        for (int i = 0;i<temp2.length;i++){
            if (i < temp2.length-1){
                formatedDepartureTime = formatedDepartureTime + temp2[i] + ":";
            }
            else{
                formatedDepartureTime = formatedDepartureTime + temp2[i];
            }

        }
        this.departure_time =  LocalTime.parse(formatedDepartureTime);
        this.stop_id = stop_id;
        this.stop_sequence = (stop_sequence.length()<1)? -1 : Integer.parseInt(stop_sequence);
        this.stop_headsign = (stop_headsign.length()<1)? -1 : Integer.parseInt(stop_headsign);
        this.pickup_type = (pickup_type.length()<1)? -1 : Integer.parseInt(pickup_type);
        this.drop_off_type = (drop_off_type.length()<1)? -1 : Integer.parseInt(drop_off_type);
    }


    public int getTrip_id() {
        return trip_id;
    }

    public LocalTime getArrival_time() {
        return arrival_time;
    }

    public LocalTime getDeparture_time() {
        return departure_time;
    }

    public String getStop_id() {
        return stop_id;
    }

    public int getStop_sequence() {
        return stop_sequence;
    }

    public int getStop_headsign() {
        return stop_headsign;
    }

    public int getPickup_type() {
        return pickup_type;
    }

    public int getDrop_off_type() {
        return drop_off_type;
    }

    @Override
    public String toString() {
        return "StopTime{" +
                "trip_id=" + trip_id +
                ", arrival_time=" + arrival_time.toString() +
                ", departur_time=" + departure_time.toString() +
                ", stop_id=" + stop_id +
                ", stop_sequence=" + stop_sequence +
                ", stop_headsign=" + stop_headsign +
                ", pickup_type=" + pickup_type +
                ", drop_off_type=" + drop_off_type +
                '}';
    }
}
