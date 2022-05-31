package com.adapei.navhelper.listener;

import com.adapei.navhelper.activity.navigation.ArrowNavView;
import com.mapbox.services.android.navigation.v5.milestone.Milestone;
import com.mapbox.services.android.navigation.v5.milestone.MilestoneEventListener;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress;

/**
 * MyMilestoneEventListener react when there was an update on the milestone
 * to update the arrow in the correct orientation.
 */
public class MyMilestoneEventListener implements MilestoneEventListener {

    /**
     * The arrow pointing to a destination with a path to follow
     */
    private ArrowNavView arrowNavView;

    /**
     * Constructor method of MyMilestoneEventListener, setting the arrowNavView attribute
     * @param arrowNavView the arrow of the object
     */
    public MyMilestoneEventListener(ArrowNavView arrowNavView) {
        if (arrowNavView != null) {
            this.arrowNavView = arrowNavView;
        }
    }

    /**
     * Update the arrow instructions automatically after a milestone update
     * @param routeProgress the progress informations of route
     * @param instruction The textual informations of the next milestone
     * @param milestone The current milestone
     */
    @Override
    public void onMilestoneEvent(RouteProgress routeProgress, String instruction, Milestone milestone) {
        if (!instruction.equals("")) {
            this.arrowNavView.updateInstruction(instruction);
        }
    }
}
