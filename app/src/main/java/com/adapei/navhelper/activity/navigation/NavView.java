package com.adapei.navhelper.activity.navigation;

import static com.mapbox.core.constants.Constants.PRECISION_6;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.adapei.navhelper.Checkpoint;
import com.adapei.navhelper.Constants;
import com.adapei.navhelper.Location;
import com.adapei.navhelper.database.CheckPointData;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Class before navigation
 * get checkpoints on the road and send sms.
 */
public class NavView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Send an sms
     * @param phoneNumber The phone number
     * @param message the message to send
     */
    protected void sendSMS(String phoneNumber, String message) {
        // sends a text message to the number given as an argument
        if(phoneNumber != null && message != null) {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, message, null, null);
        } else {
            System.out.println("sendSMS : phoneNumber or SMS null");
        }
    }

    /**
     * Alert the user when there is no GPS signal and the SOS button has been clicked
     */
    protected void alertNoPositionWhenSOSNeeded() {
        System.out.println("NO POSITION WHEN SOS NEEDED");
        Toast.makeText(this, "[ERREUR] Position introuvable", Toast.LENGTH_SHORT).show();
    }

    /**
     * Alert the user when there is no parametrised phone number and the SOS button has been clicked
     */
    protected void alertPhoneNumberNullWhenSOSNeeded() {
        System.out.println("PHONE NUMBER NULL WHEN SOS NEEDED");
        Toast.makeText(this, "[ERREUR] Aucun numéro de téléphone entré", Toast.LENGTH_SHORT).show();
    }

    /**
     * Get the checkpoints located on the given route with a 10 meters radius
     * @param route The route of which we want to get the checkpoints
     * @return The checkpoints located on the given route
     */
    public List<Checkpoint> getCheckpointsForRoute(DirectionsRoute route) {

        List<Checkpoint> checkpointsOnRoad = new ArrayList<>();
        List<Checkpoint> checkpoints = new CheckPointData(this).getAllCheckpoints();
        //get all of the points that describe the specified route
        List<Point> routeCoordinateList = directionsRouteToPoints(route);

        for(int i = 0; i < routeCoordinateList.size()-1; i++){

            for (Checkpoint cp : checkpoints) {

                if(!checkpointsOnRoad.contains(cp)) {

                    // 1 trouver l'équation d'une droite d1 (y = ax + b) entre routeCoordinateList.get(i) et routeCoordinateList.get(i+1)
                    Point p1 = routeCoordinateList.get(i);
                    Point p2 = routeCoordinateList.get(i+1);
                    double a = (p2.longitude()-p1.longitude())/(p2.latitude()-p1.latitude());
                    double b = p1.longitude()-(p1.latitude()*a);
                    /* 2 trouver l'équation de la droite d2 perpendiculaire à la droite précédent passant par le checkpoint
                     vecteur directeur de d --> u(1,a)
                     d2 : x + ay + c = 0
                     on remplace x et y par les coordonnées du checkpoint
                     et on trouve c pour que ce soit égal à 0
                     et grâce à ça on trouve c et donc l'équation de d2
                    */
                    double c = 0 - (cp.getLatitude()+a*cp.getLongitude());
                    /* 3 trouver les coordonnées du projeté orthogonal du checkpoint sur d1
                     On a donc ces deux droites :
                     d1 : a*x - y + b = 0
                     d2 : x + a*y + c = 0

                     Qui peuvent s'écrire comme suit :
                     d1 : ax + b = y
                     d2 : (1/-a)*x + c/-a = y

                     et on cherche des x et y qui sont les coordonnées du point le plus proche du checkpoint appartenant à la droite d1
                     */
                    double xi = ((c/-a)-b)/(a-(1/-a));
                    double yi = a*xi+b;
                    /* 4 calculer la distance entre le projeté et le checkpoint
                     simplement utiliser la méthode getDistanceBetweenCoordinates entre le checkpoint et le point qu'on vient de trouver et check si c'est inférieur ou égal à NEAR_METER
                    */

                    //On calcule le produit scalaire entre AB et AC afin de déterminer si le point appartient au segment
                    //distance entre A et B
                    double dist1 = Location.getDistanceBetweenCoordinates(p1.latitude(), p1.longitude(), p2.latitude(), p2.longitude());
                    //distance entre A et le projeté
                    double dist2 = Location.getDistanceBetweenCoordinates(p1.latitude(), p1.longitude(), xi, yi);
                    //détermine si le projeté est avant le point
                    int signe = (xi-p1.latitude()) * (p2.latitude()-p1.latitude()) + (yi-p1.longitude()) * (p2.longitude()-p1.longitude()) > 0 ? 1 : -1;
                    dist2 *= signe;

                    //si 0 < dist1 < dist2 alors la hauteur appartient au segment formé par les deux points.
                    if(0 <= dist2 && dist2 <= dist1) {
                        if (Location.getDistanceBetweenCoordinates(xi, yi, cp.getLatitude(), cp.getLongitude()) <= Constants.NEAR_METER) {
                            //System.out.println("lat : " + cp.getLatitude() + " long : " + cp.getLongitude());
                            //System.out.println(Location.getDistanceBetweenCoordinates(xi, yi, cp.getLatitude(), cp.getLongitude()) + " : " + cp.getDisplayName());
                            checkpointsOnRoad.add(cp); // le checkpoint est donc ajouté car il est à moins du 10m du trajet
                        }
                    }
                }
            }
        }

        return checkpointsOnRoad;
    }

    /**
     * Extract the Points of a route
     * @param directionsRoute The route of which we want to get the points
     * @return the list of every points we're going through for the route
     */
    public List<Point> directionsRouteToPoints(DirectionsRoute directionsRoute) {
        //directionsRoute.geometry() -> Mapbox based, give the global shape of the given route. Return a polylineString
        //fromPolyLine convert a polylineString to a LineString wich is a list of Point.
        // A Point is a geographic point, (longitude, latitude)
        LineString lineString = LineString.fromPolyline(directionsRoute.geometry(), PRECISION_6);

        return lineString.coordinates();
    }
}
