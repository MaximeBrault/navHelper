package com.adapei.navhelper.activity;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.adapei.navhelper.Checkpoint;
import com.adapei.navhelper.Constants;
import com.adapei.navhelper.R;
import com.adapei.navhelper.database.CheckPointData;
import com.mapbox.geojson.Point;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Class allowing an add of a checkpoint
 */
public class ArrowNavCheckpointAddView extends AppCompatActivity {

    /**
     * Code returned when the picture is taken
     */
    private static final int REQUEST_ID_IMAGE_CAPTURE = 2;

    /**
     * Text to speech used for this activity
     */
    private TextToSpeech textToSpeech;

    /**
     * Picture representing the checkpoint
     */
    private Uri uriPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrow_nav_checkpoint_add_view);

        // TextToSpeech creation
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.FRANCE);
                }
            }
        });

        Button takeAgain = findViewById(R.id.retakePictureButton);
        takeAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePictureButtonClicked();
            }
        });

        this.takePictureButtonClicked();
    }

    /**
     * Pronounce the name given to the checkpoint
     * @param view The current view
     */
    public void listenButtonClicked(View view) {
        EditText checkpointPronunciationText = findViewById(R.id.checkpointPronunciationEditText);
        this.textToSpeech.speak(checkpointPronunciationText.getText().toString(), TextToSpeech.QUEUE_FLUSH, null, null);
    }

    /**
     * Quit the view when the abort button is pressed
     * @param view The current view
     */
    public void abortButtonClicked(View view) {
        this.finish();
    }

    /**
     * Launch the camera activity to take a picture
     */
    public void takePictureButtonClicked() {
        //Create an implicit intent, for image capture.
        Intent camera = new Intent();
        camera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        Date date = new Date();
        DateFormat df = new SimpleDateFormat(getResources().getString(R.string.pictureNameDateFormat), Locale.FRANCE);

        //Name the image with the date it was taken
        String newPicFile = df.format(date) + ".jpg";
        File outFile = new File(Environment.getExternalStorageDirectory(), newPicFile);

        uriPicture = FileProvider.getUriForFile(
                this,
                this.getPackageName() + ".provider", //(use your app signature + ".provider" )
                outFile);

        camera.putExtra(MediaStore.EXTRA_OUTPUT, uriPicture);
        camera.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
        camera.addFlags(FLAG_GRANT_WRITE_URI_PERMISSION);

        if(camera.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(camera, REQUEST_ID_IMAGE_CAPTURE);
        }
    }

    /**
     * manage the return of the camera
     * @param requestCode
     * @param resultCode
     * @param data
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ID_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                //Reset the spinner to "icon"
                Toast.makeText(this, getResources().getString(R.string.pictureTaken), Toast.LENGTH_LONG).show();
                ((ImageView)findViewById(R.id.checkpointPicture)).setImageURI(Uri.parse(uriPicture.toString()));
                findViewById(R.id.validateButton).setEnabled(true);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, getResources().getString(R.string.cameraCancel), Toast.LENGTH_LONG).show();
                uriPicture = null;
            } else {
                Toast.makeText(this, getResources().getString(R.string.cameraError), Toast.LENGTH_LONG).show();
                uriPicture = null;
            }
        }
    }

    /**
     * Validate the information and add the new destination on the home page
     */
    public void validateButtonClicked(View view) {
        EditText cpName = findViewById(R.id.checkpointNameEditText);
        EditText cpPron = findViewById(R.id.checkpointPronunciationEditText);
        if(!cpName.getText().toString().equals("") && !cpPron.getText().toString().equals("")) {
            //Get the user entries
            String nameInput = cpName.getText().toString();
            String pronunciationInput = cpPron.getText().toString();

            //Creation of the destination belonging if the user selected an icon or took a picture
            Checkpoint checkpointToAdd = null;

            //Get the user's current location when the button is clicked
            com.adapei.navhelper.Location.checkLocationPermission(this);
            Constants.locationEngine.requestLocationUpdates();
            Location lastLocation = Constants.locationEngine.getLastLocation();
            for (int i = 0; i < 100 && lastLocation == null; i++) {
                lastLocation = Constants.locationEngine.getLastLocation();
            }
            Point currentUserPosition = Point.fromLngLat(lastLocation.getLongitude(), lastLocation.getLatitude());
            double destinationLng = currentUserPosition.longitude();
            double destinationLat = currentUserPosition.latitude();

            //Creation of checkpoint
            if (uriPicture != null) {
                try {
                    checkpointToAdd = new Checkpoint(destinationLat, destinationLng, nameInput, pronunciationInput, new URI(uriPicture.toString()));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
            // save the checkpoint in the bdd
            new CheckPointData(this).addCheckpoint(checkpointToAdd);

            Toast.makeText(this, R.string.validationAjoutCheckpoint, Toast.LENGTH_SHORT).show();

            this.finish();
        } else {
            Toast.makeText(this, R.string.validerAjoutCheckpoint, Toast.LENGTH_SHORT).show();
        }
    }
}
