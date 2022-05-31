package com.adapei.navhelper.GTFSUtils;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.adapei.navhelper.GTFSUtils.GTFSObjectcs.BusConnectionEdge;
import com.adapei.navhelper.GTFSUtils.GTFSObjectcs.Route;
import com.adapei.navhelper.GTFSUtils.GTFSObjectcs.Stop;
import com.adapei.navhelper.GTFSUtils.GTFSObjectcs.StopTime;
import com.adapei.navhelper.GTFSUtils.GTFSObjectcs.Trip;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class ItemListBuilder {

    private Context context;

    private ArrayList<Route> routes;
    private ArrayList<Stop> stops;
    private ArrayList<Trip> trips;
    private ArrayList<StopTime> stopTimes;
    private ArrayList<BusConnectionEdge> busConnectionEdges;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ItemListBuilder(Context context){
        this.context = context;

        this.routes = new ArrayList<Route>();
        this.stops = new ArrayList<Stop>();
        this.trips = new ArrayList<Trip>();
        this.stopTimes = new ArrayList<StopTime>();
        this.busConnectionEdges = new ArrayList<BusConnectionEdge>();
        this.buildLists();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void buildLists(){
        buildRoutesList();
        buildStopsList();
        buildStopTimes();
         buildConnectionEdgesList();
        //printLists();
        //printStopTimes();
        //printStopList();
        //printStopStimes();
    }

    private  void printStopStimes(){
        int i = 0;
        for (StopTime obj : stopTimes){
            System.out.println(obj.toString());
            i++;

        }
        System.out.println(i);
    }
    private void printLists() {
        System.out.println(this.stopTimes.toString());

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void buildRoutesList(){
        String path ="/data/data/com.adapei.navhelper/files/routes.txt";
        Path pathToFile = Paths.get(path);
        try (BufferedReader br = Files.newBufferedReader(pathToFile
                , Charset.forName("utf-8"))){
            String line = br.readLine();
            line = br.readLine(); //to skip the first line
            while (line != null) {

                String[] attributes = line.split(",");
                routes.add(new Route(attributes[0], attributes[1], attributes[2], attributes[3], attributes[4], attributes[5], attributes[6], attributes[7], attributes[8], attributes[9]));
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void buildStopsList(){
        String path = "/data/data/com.adapei.navhelper/files/stops.txt";
        Path pathToFile = Paths.get(path);

        try (BufferedReader br = Files.newBufferedReader(pathToFile,Charset.forName("utf-8"))){
            String line = br.readLine();
            line = br.readLine(); //to skip the first line

            while (line != null) {

                String[] attributes = line.split(",");

                stops.add(new Stop(attributes[0], attributes[1], attributes[2], attributes[3], attributes[4], attributes[5], attributes[6], attributes[7], attributes[8], attributes[9], attributes[10], attributes[11],"","" + ""));
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void buildTripsList(){
        String path = "/data/data/com.adapei.navhelper/files/trips.txt";
        Path pathToFile = Paths.get(path);




        try (BufferedReader br = Files.newBufferedReader(pathToFile,Charset.forName("utf-8"))){
            String line = br.readLine();
            line = br.readLine(); //to skip the first line
            while (line != null) {
                String[] attributes = line.split(",");
                //add if statement to only take in account trips that are referenced in stop_times where stop times
                trips.add(new Trip(attributes[0], attributes[1], attributes[2], attributes[3], attributes[4], attributes[5], attributes[6], attributes[7], attributes[8], attributes[9]));
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void buildStopTimes() {

        String path =  "/data/data/com.adapei.navhelper/files/stop_times.txt";
        Path pathToFile = Paths.get(path);
        Date date = new Date();   // given date
        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTime(date);   // assigns calendar to given date
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        int hour = rightNow.get(Calendar.HOUR_OF_DAY);

        try (BufferedReader br = Files.newBufferedReader(pathToFile,Charset.forName("utf-8"))){
            String line = br.readLine();
            line = br.readLine(); //to skip the first line
            while (line != null) {
                String[] attributes = line.split(",");
                String[] temp = attributes[2].split(":");
                int stopTimeHour = Integer.parseInt(temp[0]);

                if (stopTimeHour>hour && stopTimeHour<=(hour+1)){
                    stopTimes.add(new StopTime(attributes[0], attributes[1], attributes[2], attributes[3], attributes[4], "", "", ""));
                    line = br.readLine();
                }
                else {
                    line = br.readLine();
                }



            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }




    }

    public void printStopTimes(){
        for (StopTime st : stopTimes
             ) {
            System.out.println(st.toString());
        }
    }

    public void printStopList(){
        for (Stop stop: stops
             ) {
            System.out.println(stop.toString());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void buildConnectionEdgesList(){
        int tripId = this.stopTimes.get(0).getTrip_id();

        int i = 1;
        while (i<this.stopTimes.size()-1){
            while((this.stopTimes.get(i).getTrip_id()) == tripId && i<this.stopTimes.size()-1){
                LocalTime time = this.stopTimes.get(i-1).getArrival_time();
                LocalTime stopTime = this.stopTimes.get(i).getArrival_time();
                Duration weight = Duration.between(time,stopTime);
                this.busConnectionEdges.add(new BusConnectionEdge(this.stopTimes.get(i-1).getStop_id(),this.stopTimes.get(i).getStop_id(),weight));

                i++;
            }
            tripId = this.stopTimes.get(i).getTrip_id();
            i++;

        }






    }



    public Stop getStopById(String id){
        for (Stop stop : stops
             ) {
            if (stop.getStop_id().equals(id))
                return stop;
        }
        return null;
    }

    public Context getContext() {
        return context;
    }

    public ArrayList<Route> getRoutes() {
        return routes;
    }

    public ArrayList<Stop> getStops() {
        return stops;
    }

    public ArrayList<Trip> getTrips() {
        return trips;
    }

    public ArrayList<StopTime> getStopTimes() {
        return stopTimes;
    }

    public ArrayList<BusConnectionEdge> getBusConnectionEdges() {
        return busConnectionEdges;
    }

    @Override
    public String toString() {
        return "ItemListBuilder{" +
                "context=" + context +
                ", routes=" + routes +
                ", stops=" + stops +
                ", trips=" + trips +
                '}';
    }
}
