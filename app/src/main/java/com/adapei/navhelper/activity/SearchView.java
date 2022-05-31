package com.adapei.navhelper.activity;

import static com.adapei.navhelper.Constants.MAPBOX_ACCESS_TOKEN;
import static com.adapei.navhelper.Constants.locationEngine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.adapei.navhelper.Constants;
import com.adapei.navhelper.Destination;
import com.adapei.navhelper.R;
import com.adapei.navhelper.database.DestinationData;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchView extends AppCompatActivity {

    /**
     * TAG used for logs
     */
    private static final String TAG = SearchView.class.getSimpleName();

    /**
     * Text to Speech instance
     */
    private TextToSpeech textToSpeech;

    /**
     * Layout containing addresses
     */
    private LinearLayout addressLayout;

    /**
     * List containing Addresses Textview
     */
    private List<TextView> textViews;

    /**
     * Current selected destination
     */
    private Destination selectedDestination;

    /**
     * text field where the user is typing
     */
    private EditText query;

    /**
     * true jf a destination has been added, false otherwise
     */
    private boolean endActivity;

    /**
     * True if a photo has been choosen at this moment
     */
    private boolean photoSelected;

    /**
     * view's items
     */
    private Spinner spinner;
    private ImageView image;
    private String iconId;
    private EditText inputPronunciation;
    private EditText inputNickname;
    private LinearLayout scrollView;
    private ImageButton pronunciationButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        this.photoSelected = false;

        super.onCreate(savedInstanceState);
        //Create interface of search
        setContentView(R.layout.activity_search_view);

        this.scrollView = findViewById(R.id.addressLayout);

        query = (EditText) findViewById(R.id.addressInput);
        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollView.setVisibility(View.VISIBLE);
            }
        });
        query.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
                if(!text.toString().equalsIgnoreCase("")) search(text.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        inputPronunciation = (EditText) findViewById(R.id.prononciationInputSearch);
        pronunciationButton = (ImageButton) findViewById(R.id.listenButton);

        // TextToSpeech creation
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.FRANCE);
                }
            }
        });

        this.selectedDestination = null;
        this.addressLayout = findViewById(R.id.addressLayout);
        this.spinner = findViewById(R.id.iconsSpinnerSearch);
        findViewById(R.id.textViewSearch).setFocusableInTouchMode(true);
        findViewById(R.id.textViewSearch).requestFocus();

        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.spinner_item, getResources().getStringArray(R.array.icons_array));
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        this.spinner.setAdapter(adapter);

        this.image = findViewById(R.id.imageDestinationSearch);
        this.textViews = new ArrayList<>();

        this.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(!spinner.getSelectedItem().toString().equals("icone")) {
                    image.setVisibility(View.VISIBLE);
                    image.setImageDrawable(getResources().getDrawable(getResources().getIdentifier(spinner.getSelectedItem().toString(), "drawable", getPackageName()), getTheme()));
                    iconId = spinner.getSelectedItem().toString();
                } else {
                    if (!photoSelected) {
                        image.setVisibility(View.INVISIBLE);
                        image.setImageDrawable(null);
                        iconId = "";
                    }

                    photoSelected = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                image.setVisibility(View.INVISIBLE);
            }
        });

        if(locationEngine == null) Constants.initLocationEngine(this);

        this.inputNickname = findViewById(R.id.nicknameInputSearch);
        this.inputPronunciation = findViewById(R.id.prononciationInputSearch);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void searchButtonClicked(View view) {
        // Collect the address
        EditText query = ((EditText) findViewById(R.id.addressInput));
        // Launch the search (with adress)
        if(query != null && !query.getText().toString().equals("")) search(query.getText().toString());
    }

    /**
     * Search the query with the Mapbox geocoding API
     * @param query An address to fin
     */
    private void search(String query) {
        MapboxGeocoding mapboxGeocoding = null;
        com.adapei.navhelper.Location.checkLocationPermission(this);

        if(locationEngine.getLastLocation() != null) {

            // the range selected by the user in parameters, it force users to access to destination in a small area and not all over the world
            SharedPreferences mPrefs = this.getSharedPreferences(getResources().getString(R.string.sharedPreferencesKey), Context.MODE_PRIVATE);
            double lat = Double.parseDouble(mPrefs.getString("radius","150"))/111;
            double lon = Double.parseDouble(mPrefs.getString("radius","150"))/79;

            Location userLoc = locationEngine.getLastLocation();
            double minX = userLoc.getLongitude() - lon;
            double minY = userLoc.getLatitude() - lat;
            double maxX = userLoc.getLongitude() + lon;
            double maxY = userLoc.getLatitude() + lat;

            // Generate the map with the MapBox API
            mapboxGeocoding = MapboxGeocoding.builder()
                    .accessToken(MAPBOX_ACCESS_TOKEN)
                    .query(query)
                    .bbox(minX, minY, maxX, maxY)
                    .proximity(Point.fromLngLat(locationEngine.getLastLocation().getLongitude(),locationEngine.getLastLocation().getLatitude()))
                    .build();
        } else {
            mapboxGeocoding = MapboxGeocoding.builder()
                    .accessToken(MAPBOX_ACCESS_TOKEN)
                    .query(query)
                    .build();
        }

        // Display addresses resulting of the query
        mapboxGeocoding.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
                // response.body() return GeocodingResponse
                // response.body().features() return List<CarmenFeature>
                List<CarmenFeature> results = response.body().features();

                if (results.size() > 0) {
                    displayAddresses(results);
                } else {
                    //No result for your request were found.
                    System.err.println("onResponse: No result found");
                }
            }

            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable t) {
                Log.e("SearchView","FAIL GEOCODING MAPBOX");
            }
        });
    }

    public void pronunciationButtonClicked(View view){
        String pronunciationInput = ((EditText) findViewById(R.id.prononciationInputSearch)).getText().toString();
        //Pronounce it
        textToSpeech.speak(pronunciationInput, TextToSpeech.QUEUE_FLUSH, null,null);
    }

    /**
     * Display given addresses
     * @param results Addresses to be displayed
     */
    private void displayAddresses(List<CarmenFeature> results) {

        this.addressLayout.removeAllViews();

        for(int i=0; i<results.size(); i++) {

            Point point = results.get(i).center();
            String name = results.get(i).placeName();

            //Add graphical elements on texts
            TextView addressTextView = new TextView(this);
            addressTextView.setText(name);
            addressTextView.setTextColor(getResources().getColor(R.color.mapboxWhite));
            addressTextView.setPadding(10,10,10,10);

            addressTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textToSpeech.speak(name, TextToSpeech.QUEUE_FLUSH, null, null);
                    selectAddress(new Destination(name, "", "", point.latitude(), point.longitude(), name, Constants.getFoyer(getApplicationContext())));
                    updateUI(addressTextView);
                }
            });

            this.textViews.add(addressTextView);
            this.addressLayout.addView(addressTextView);
        }
    }

    /**
     * Update the addresses color whether the are selected or not
     * @param selectedTextView The addresses TextView
     */
    private void updateUI(TextView selectedTextView) {
        for(TextView textView : textViews) {
            if(textView == selectedTextView)
                textView.setTextColor(getResources().getColor(R.color.checkpointNear));
            else
                textView.setTextColor(getResources().getColor(R.color.mapboxWhite));
        }
    }

    /**
     * Select an address and set it as the selected destination
     * @param destination The selected destination
     */
    private void selectAddress(Destination destination) {
        this.scrollView.setVisibility(View.INVISIBLE);
        EditText text = findViewById(R.id.addressInput);
        text.setText(destination.getDisplayName());
        this.selectedDestination = destination;
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
     * Save the selected destination and set it in the HomeView
     * @param view The current view
     */
    public void validateButtonClicked(View view) {
        if(this.selectedDestination != null) {
            if (!this.inputNickname.getText().toString().equals("")) {
                if (!this.inputPronunciation.getText().toString().equals("")) {
                    if (this.image.getDrawable() != null) {
                        if(this.inputNickname.getText().toString().length() < 30) {
                            //Get the user entries
                            String nameInput = ((EditText) findViewById(R.id.nicknameInputSearch)).getText().toString();
                            Destination destinationToAdd = this.selectedDestination;
                            String pronunciationInput = ((EditText) findViewById(R.id.prononciationInputSearch)).getText().toString();

                            destinationToAdd.setNickname(nameInput);

                            destinationToAdd.setPronunciation(pronunciationInput);
                            destinationToAdd.setIconId(this.iconId);
                            DestinationData data = new DestinationData(this);
                            data.addDestination(destinationToAdd);
                            this.endActivity = true;
                            this.finish();
                        } else {
                            Toast.makeText(this, R.string.maxLengthModifDest, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this, R.string.validerImgSearch, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, R.string.validerPrononciationSearch, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, R.string.validerSurnameSearch, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, R.string.validerDestSearch, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * launch the gallery of the user to get pictures he wants from it
     * @param view
     */
    public void getPictureFromPhone(View view) {
        this.photoSelected = true;
        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), 3); /* open phone's file */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==3 && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                //Reset the spinner to "icon"
                spinner.setSelection(0);

                Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                this.image.setImageBitmap(bm);
                this.image.setVisibility(View.VISIBLE);

                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                this.iconId = cursor.getString(columnIndex);
            } catch (FileNotFoundException e) {
                Toast.makeText(this, "Image non trouvée", Toast.LENGTH_SHORT).show();
                Log.e("No pdp found", e.getMessage());
                this.photoSelected = false;
            } catch (IOException e) {
                Toast.makeText(this, "Problème lors du chargement de l'image", Toast.LENGTH_SHORT).show();
                Log.e("pdp can't be upload", e.getMessage());
                this.photoSelected = false;
            }
        }
    }

    @Override
    public void finish() {
        if(endActivity) setResult(RESULT_OK);
        else setResult(RESULT_CANCELED);
        super.finish();
    }
}
