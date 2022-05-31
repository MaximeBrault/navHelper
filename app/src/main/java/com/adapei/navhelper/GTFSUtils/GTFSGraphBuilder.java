package com.adapei.navhelper.GTFSUtils;

import android.util.Log;

import com.adapei.navhelper.GTFSUtils.GTFSObjectcs.BusConnectionEdge;
import com.adapei.navhelper.GTFSUtils.GTFSObjectcs.Stop;

import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

public class GTFSGraphBuilder{

    private ItemListBuilder itemListBuilder;
    private Graph<Stop, BusConnectionEdge> AllStopsGraph;



    public GTFSGraphBuilder(ItemListBuilder itemListBuilder){
        this.itemListBuilder = itemListBuilder;


        this.AllStopsGraph = new DefaultDirectedWeightedGraph<Stop, BusConnectionEdge>(BusConnectionEdge.class);
        for (Stop stop : itemListBuilder.getStops()
             ) {
            System.out.println(stop.toString());
            AllStopsGraph.addVertex(stop);
        }
        for(BusConnectionEdge bce : itemListBuilder.getBusConnectionEdges()
            ){
            System.out.println(bce.toString());
            AllStopsGraph.addEdge(itemListBuilder.getStopById(bce.getStop1()),itemListBuilder.getStopById(bce.getStop2()),bce);


        }


        DijkstraShortestPath<Stop,BusConnectionEdge> djk = new DijkstraShortestPath<Stop,BusConnectionEdge>(AllStopsGraph);

        if(djk.getPath(this.itemListBuilder.getStopById("BUNI1"),this.itemListBuilder.getStopById("KMREC"))!=null){
            Log.d("SHORTEST PATH ",djk.getPath(this.itemListBuilder.getStopById("BUNI1"),this.itemListBuilder.getStopById("KMREC")).toString());
            int tmps=0;
            for (BusConnectionEdge obj : djk.getPath(this.itemListBuilder.getStopById("BUNI1"),this.itemListBuilder.getStopById("KMREC")).getEdgeList()
            ) {
                tmps+=obj.getWeight();

            }
            Log.d("Tmps de trajet",tmps+"");
        }
        else{
            System.out.println("Pas de trajet disponible à cette heure ci");
        }



    };

    @Override
    public String toString() {
        return AllStopsGraph.toString();
    }
}




