package com.adapei.navhelper.GTFSUtils.GTFSObjectcs;


import android.os.Build;
import android.support.annotation.RequiresApi;

import org.jgrapht.graph.DefaultWeightedEdge;

import java.time.Duration;

public class BusConnectionEdge extends DefaultWeightedEdge {
    private String stop1;
    private String stop2;
    private double weight;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public BusConnectionEdge(String stop1, String stop2, Duration weight){
        this.stop1 = stop1;
        this.stop2 = stop2;
        this.weight = weight.toMinutes();
    }


    public String getStop1() {
        return stop1;
    }

    public String getStop2() {
        return stop2;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "BusConnectionEdge{" +
                "stop1=" + stop1+
                ", stop2=" + stop2 +
                ", weight=" + weight +
                '}';
    }
}
