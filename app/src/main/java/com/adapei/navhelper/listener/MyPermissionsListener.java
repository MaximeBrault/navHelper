package com.adapei.navhelper.listener;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import com.adapei.navhelper.activity.navigation.MapNavView;
import com.mapbox.android.core.permissions.PermissionsListener;

import java.util.List;

/**
 * MyPermissionsListener is used to detect when a permission is needeed to be given by users.
 */
public class MyPermissionsListener implements PermissionsListener {

    /**
     * The current view of the map
     */
    private MapNavView mapNavView;

    /**
     * Viewing information about the required permissions
     * @param permissionsToExplain The text about the required permissions
     */
    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mapNavView);
        builder.setTitle("Permissions");
        builder.setMessage("Les permissions ne seront utilisée que pour les trajets");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Detect if the permission are allowed by the user
     * @param granted true if allowed, false instead
     */
    @Override
    public void onPermissionResult(boolean granted) {

        if (granted) {

            System.out.println("Permission granted !");
        } else {

            Toast.makeText(mapNavView, "Permission non acceptée, certaines fonctionnalités seront bloquée", Toast.LENGTH_SHORT).show();
        }
    }
}
