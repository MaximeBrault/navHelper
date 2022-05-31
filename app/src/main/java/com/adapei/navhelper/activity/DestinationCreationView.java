package com.adapei.navhelper.activity;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.adapei.navhelper.Constants;
import com.adapei.navhelper.Destination;
import com.adapei.navhelper.R;
import com.adapei.navhelper.database.DestinationData;
import com.mapbox.geojson.Point;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Class for adding a new destination on the home page
 */
public class DestinationCreationView extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    /**
     * Code returned when the picture is taken
     */
    private static final int REQUEST_ID_IMAGE_CAPTURE = 1;

    /**
     * Text to Speech instance
     */
    private TextToSpeech textToSpeech;

    /**
     * Longitude of the place
     */
    private double destinationLng;

    /**
     * Latitude od the place
     */
    private double destinationLat;

    /**
     * Picture taken why the user
     */
    private Uri uriPicture;

    /**
     * Spinner displaying icons
     */
    private Spinner spinner;

    /**
     * Frame representing a picture or an icon
     */
    private ImageView imageDestination;

    /**
     * True if a photo is beeing taken at this moment
     */
    private boolean photoTaking;

    /**
     * True if the actual user's pos has been saved
     */
    private boolean posSaved;

    /**
     * true jf a destination has been added, false otherwise
     */
    private boolean endActivity;

    private EditText inputPronunciation;
    private EditText inputNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination_creation_view);

        //TextToSpeech creation
        textToSpeech = new TextToSpeech(getApplicationContext(), status -> {
            if(status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.FRANCE);
            }
        });

        //Spinner creation for the icon list
        spinner = findViewById(R.id.iconsSpinner);

        spinner.setOnItemSelectedListener(this);
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.spinner_item, getResources().getStringArray(R.array.icons_array));
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        this.spinner.setAdapter(adapter);

        //By default, no picture is taken
        uriPicture = null;
        //Bind the imageView
        this.imageDestination = findViewById(R.id.imageDestination);

        this.inputNickname = findViewById(R.id.destinationNameInput);
        this.inputPronunciation = findViewById(R.id.prononciationInput);

        this.posSaved = false;

        findViewById(R.id.ajouterTitle).setFocusable(true);
        findViewById(R.id.ajouterTitle).requestFocus();
    }

    @Override
    protected void onResume() { super.onResume(); }

    /**
     * Prononce the text written in the prononciation textbox
     * @param view View displayed
     */
    public void listenButtonClicked(View view) {
        //Get the user pronunciation entry
        String pronunciationInput = ((EditText) findViewById(R.id.prononciationInput)).getText().toString();
        //Pronounce it
        textToSpeech.speak(pronunciationInput, TextToSpeech.QUEUE_FLUSH, null,null);
    }

    /**
     * Display and save the user's actual position
     * @param view View displayed
     */
    public void posButtonClicked(View view) {

        //Get the coordinates
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
        if(manager != null) {
            boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if(statusOfGPS) {
                //Get the user's current location when the button is clicked
                com.adapei.navhelper.Location.checkLocationPermission(this);
                Constants.locationEngine.requestLocationUpdates();
                Location lastLocation = Constants.locationEngine.getLastLocation();
                for(int i = 0; i < 100 && lastLocation == null; i++){
                    lastLocation = Constants.locationEngine.getLastLocation();
                }
                if(lastLocation != null) {
                    Point currentUserPosition = Point.fromLngLat(lastLocation.getLongitude(), lastLocation.getLatitude());
                    destinationLng= currentUserPosition.longitude();
                    destinationLat= currentUserPosition.latitude();

                    TextView displayCoord = (TextView) (findViewById(R.id.coordoneeTextView));
                    displayCoord.setText("Position actuelle : " + destinationLat + ", " + destinationLng);

                    Button buttonCoord = (Button) (findViewById(R.id.coordoneeButton));
                    buttonCoord.setTextColor(getApplication().getResources().getColor(R.color.greenOffButtonADAPEI));

                    findViewById(R.id.coordoneeButton).setEnabled(false);
                    this.posSaved = true;
                }
            } else {
                //Set the parameters of GPS
                Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(gpsOptionsIntent);
            }
        }
    }

    //The 2 following methods are needed for the spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (!spinner.getSelectedItem().toString().equals("icone")) {
            uriPicture = null;
            imageDestination.setVisibility(View.VISIBLE);
            imageDestination.setImageDrawable(getResources().getDrawable(getResources().getIdentifier(spinner.getSelectedItem().toString(), "drawable", getPackageName()), getTheme()));
        } else {
            if (!photoTaking) {
                imageDestination.setVisibility(View.INVISIBLE);
                imageDestination.setImageDrawable(null);
            }

            photoTaking = false;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //needs to be there for the interface implementation
    }

    /**
     * Launch the camera activity to take a picture
     * @param view View displayed
     */
    public void cameraButtonClicked(View view) {

        //Lock the imageDestination for the future picture
        photoTaking = true;
        //Create an implicit intent, for image capture.
        Intent camera = new Intent();
        camera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        Date date = new Date();
        DateFormat df = new SimpleDateFormat(getResources().getString(R.string.pictureNameDateFormat),Locale.FRANCE);

        //Name the image with the date it was taken
        String newPicFile = df.format(date) + ".jpg";
        File outFile = new File(Environment.getExternalStorageDirectory(), newPicFile);

        uriPicture = FileProvider.getUriForFile(
                this,
                this.getPackageName() + ".provider", //(use your app signature + ".provider" )
                outFile);

        //if (photoTaking) findViewById(R.id.validateButton).setEnabled(true);

        //Add the camera option
        camera.putExtra(MediaStore.EXTRA_OUTPUT, uriPicture);
        camera.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
        camera.addFlags(FLAG_GRANT_WRITE_URI_PERMISSION);

        if(camera.resolveActivity(getPackageManager()) != null) startActivityForResult(camera, REQUEST_ID_IMAGE_CAPTURE);
    }

    /**
     * change the appearance of the activity depending on the type of picture chosen (icon or picture)
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
                spinner.setSelection(0);
                Toast.makeText(this, getResources().getString(R.string.pictureTaken), Toast.LENGTH_LONG).show();
                imageDestination.setVisibility(View.VISIBLE);
                ((ImageView)findViewById(R.id.imageDestination)).setImageURI(Uri.parse(uriPicture.toString()));
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, getResources().getString(R.string.cameraCancel), Toast.LENGTH_LONG).show();
                uriPicture = null;
                photoTaking = false;
            } else {
                Toast.makeText(this, getResources().getString(R.string.cameraError), Toast.LENGTH_LONG).show();
                uriPicture = null;
                photoTaking = false;
            }
        }
    }

    /**
     * Quit the view when the abort button is pressed
     * @param view The current view
     */
    public void abortButtonClicked(View view) {
        this.endActivity = false;
        this.finish();
    }

    /**
     * Validate the informations and add the new destination on the home page
     * @param view View displayed
     */
    public void validateButtonClicked(View view) {
        if (this.posSaved) {
            if (!this.inputNickname.getText().toString().equals("")) {
                if (!this.inputPronunciation.getText().toString().equals("")) {
                    if (this.imageDestination.getDrawable() != null) {
                        if(this.inputNickname.getText().toString().length() < 30) {
                            //Get the user entries
                            String nameInput = ((EditText) findViewById(R.id.destinationNameInput)).getText().toString();
                            String pronunciationInput = ((EditText) findViewById(R.id.prononciationInput)).getText().toString();

                            //Creation of the destination belonging if the user selected an icon or took a picture
                            Destination destinationToAdd = null;

                            //With picture
                            if (uriPicture != null) {
                                destinationToAdd = new Destination(nameInput, nameInput, uriPicture.toString(), destinationLat, destinationLng, pronunciationInput, Constants.getFoyer(getApplicationContext()));
                            }
                            //With icon
                            else {
                                destinationToAdd = new Destination(nameInput, nameInput, spinner.getSelectedItem().toString(), destinationLat, destinationLng, pronunciationInput, Constants.getFoyer(getApplicationContext()));
                            }

                            DestinationData data = new DestinationData(this);
                            data.addDestination(destinationToAdd);
                            this.endActivity = true;
                            this.finish();
                        } else {
                            Toast.makeText(this, R.string.maxLengthModifDest, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, R.string.validerImgDest, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, R.string.validerPrononciationSearch, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, R.string.validerNameSearch, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, R.string.validerActualPos, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void finish(){
        if(endActivity) setResult(RESULT_OK);
        else setResult(RESULT_CANCELED);
        super.finish();
    }
}