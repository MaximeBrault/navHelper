package com.adapei.navhelper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adapei.navhelper.R;
import com.adapei.navhelper.database.DestinationData;

public class AddView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_view);

        this.updateCheckpointsButton();
    }

    /**
     * Start destination creation view when create destination button clicked
     * @param view The current view
     */
    public void addDestinationButtonClicked(View view) {
        Intent destinationCreationIntent = new Intent(this.getApplicationContext(), DestinationCreationView.class);
        this.startActivityForResult(destinationCreationIntent, 1);
    }

    /**
     * Launch the ArrowNavCheckpointAddView activity
     * @param view The related view
     */
    public void checkpointAddButtonClicked(View view) {
        Intent arrowNavCheckpointPictureIntent = new Intent(this.getApplicationContext(), ArrowNavCheckpointAddView.class);
        this.startActivity(arrowNavCheckpointPictureIntent);
    }

    /**
     * Start destination creation view when create destination button clicked
     * @param view The current view
     */
    public void researchButtonClicked(View view) {
        Intent destinationCreationIntent = new Intent(this.getApplicationContext(), SearchView.class);
        this.startActivityForResult(destinationCreationIntent, 1); // if a destination has been added, the activity send RESULT_OK
    }

    public void abortButtonClicked(View view){ this.finish(); }

    public void updateCheckpointsButton(){
        DestinationData d = new DestinationData(this);
        TextView textView = findViewById(R.id.newCPText);
        LinearLayout button = findViewById(R.id.newCPBtn);

        if(d.getNbDestination() < 1) {
            textView.setTextColor(getResources().getColor(R.color.whiteOffButton));
            button.setEnabled(false);
        } else {
            textView.setTextColor(getResources().getColor(R.color.mapboxWhite));
            button.setEnabled(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) finish(); // if a destination has been added, the activity send RESULT_OK
    }

}
