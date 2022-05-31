package com.adapei.navhelper.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.adapei.navhelper.Constants;
import com.adapei.navhelper.CustomAdapter;
import com.adapei.navhelper.Destination;
import com.adapei.navhelper.GTFSUtils.GTFSAccesseur;
import com.adapei.navhelper.GTFSUtils.GTFSGraphBuilder;
import com.adapei.navhelper.GTFSUtils.ItemListBuilder;
import com.adapei.navhelper.R;
import com.adapei.navhelper.activity.navigation.ArrowNavView;
import com.adapei.navhelper.activity.navigation.MapNavView;
import com.adapei.navhelper.database.CheckPointData;
import com.adapei.navhelper.database.DestinationData;
import com.adapei.navhelper.database.PhoneData;

import java.util.List;
import java.util.Locale;

/**
 * Main View, where you can search or create a destination, change your profile, select a destination and start the navigation
 */
public class HomeView extends AppCompatActivity {
    /**
     * Destinations loaded
     */
    private List<Destination> destinations;

    /**
     * Destination selected
     */
    private Destination destinationSelected;

    /**
     * Text to speech used for this activity
     */
    private TextToSpeech textToSpeech;

    /**
     * Preferences of the user
     */
    private SharedPreferences sharedPreferences;

    /**
     * Manager for the assets
     */
    private AssetManager assetManager;

    /**
     * True if the start button is enable, false otherwise
     */
    private boolean startButtonEnabled;

    /**
     * True if all the permissions needed are granted
     */
    private boolean havePermission;

    /**
     * used to know if everything is set for the permissions
     */
    private boolean create;

    /**
     * give required methods to access the database
     */
    private DestinationData destinationData;

    private GridView grid;
    private View view_selected;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_view);

        this.askPermission();
        this.turnGPSOn();
        GTFSAccesseur gtfs = new GTFSAccesseur(this);

        ItemListBuilder itm = new ItemListBuilder(this);

        GTFSGraphBuilder gtfsGraphBuilder = new GTFSGraphBuilder(itm);
        //Log.d("test", gtfsGraphBuilder.toString());

        this.textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.FRANCE);
                }
            }
        });
        this.sharedPreferences = this.getSharedPreferences(getResources().getString(R.string.sharedPreferencesKey), Context.MODE_PRIVATE);
        this.destinationData = new DestinationData(this);
        this.grid = findViewById(R.id.grid);

        if(this.sharedPreferences.getBoolean("firstTimeOpenned", true)) { // actions to execute the first time the application is open

            this.sharedPreferences.edit().putBoolean(getResources().getString(R.string.profilePreferencesName), this.sharedPreferences.getBoolean("firstTimeOpenned", true)).apply();

            AlertDialog.Builder builder = new AlertDialog.Builder(HomeView.this, R.style.CustomAlertDialog);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.ok_dialog, null);

            TextView title = dialogView.findViewById(R.id.okTitle);
            TextView text = dialogView.findViewById(R.id.okText);
            Button okButton = dialogView.findViewById(R.id.okButton);

            title.setText("Bienvenue !");
            text.setText("Bienvenue sur l'application Nav0Limite. N'oubliez pas de changer les paramètres !");

            builder.setView(dialogView);
            final AlertDialog dialog = builder.create();

            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    askPermission();
                    if (havePermission) {
                        Constants.initLocationEngine(HomeView.this);
                        sharedPreferences.edit().putBoolean("firstTimeOpenned", false).apply();
                        startActivity(new Intent(getApplicationContext(), ParametersView.class));
                    }
                    dialog.dismiss();
                }
            });

            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

        } else {
            if(havePermission) {
                Constants.initLocationEngine(HomeView.this);
            }
        }

        this.updateDestinationDeleteButtonVisibility(sharedPreferences);
        this.updateDestinationAddButtonVisibility(sharedPreferences);
        this.updateCheckpointsManagementButtonVisibility(sharedPreferences);

        this.updateHomeStartButton();

        this.assetManager = this.getAssets();
        this.create = true;
    }

    /**
     * Ask the user all the permissions required
     */
    public void askPermission() {

        if(ActivityCompat.checkSelfPermission(HomeView.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(HomeView.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(HomeView.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(HomeView.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            this.havePermission = true;
        } else {

            if(!(ActivityCompat.shouldShowRequestPermissionRationale(HomeView.this, Manifest.permission.ACCESS_FINE_LOCATION)) &&
                    !(ActivityCompat.shouldShowRequestPermissionRationale(HomeView.this, Manifest.permission.READ_PHONE_STATE)) &&
                    !(ActivityCompat.shouldShowRequestPermissionRationale(HomeView.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) &&
                    !(ActivityCompat.shouldShowRequestPermissionRationale(HomeView.this, Manifest.permission.SEND_SMS))) {
                String[] permissions = new String[4];
                permissions[0] = Manifest.permission.ACCESS_FINE_LOCATION;
                permissions[1] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                permissions[2] = Manifest.permission.SEND_SMS;
                permissions[3] = Manifest.permission.READ_PHONE_STATE;
                ActivityCompat.requestPermissions(HomeView.this, permissions, 2);
            } else {
                this.havePermission = false;
                if(this.create) {
                    this.displayPerm();
                }
            }
        }
    }

    /**
     * turn the gps on if it's not
     */
    private void turnGPSOn(){
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(!provider.contains("gps")){ // if gps is disabled

            AlertDialog.Builder builder = new AlertDialog.Builder(HomeView.this, R.style.CustomAlertDialog);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.ok_dialog, null);

            TextView title = dialogView.findViewById(R.id.okTitle);
            TextView text = dialogView.findViewById(R.id.okText);
            Button okButton = dialogView.findViewById(R.id.okButton);

            title.setText("Activer la localisation");
            text.setText("Vous devez d'abord activer votre localisation");

            builder.setView(dialogView);
            final AlertDialog dialog = builder.create();

            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });

            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    /**
     * if the user didn't accept some permission required,
     */
    private void displayPerm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeView.this, R.style.CustomAlertDialog);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.ok_dialog, null);

        TextView title = dialogView.findViewById(R.id.okTitle);
        TextView text = dialogView.findViewById(R.id.okText);
        Button okButton = dialogView.findViewById(R.id.okButton);

        title.setText("Permissions manquantes");
        text.setText("Vous n'avez pas les permissions nécessaires");

        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.resetSelectedDestination();
        this.turnGPSOn();

        if (this.havePermission) {
            SharedPreferences sharedPreferences = this.getSharedPreferences(getResources().getString(R.string.sharedPreferencesKey), Context.MODE_PRIVATE);
            // Renew Destinations
            this.initDestinations(sharedPreferences);
            this.updateDestinationDeleteButtonVisibility(sharedPreferences);
            this.updateDestinationAddButtonVisibility(sharedPreferences);
            this.updateCheckpointsManagementButtonVisibility(sharedPreferences);

            this.updateHomeStartButton();
        }
    }

    @Override
    protected void onDestroy() {
        Constants.deactivateLocationEngine();
        if(this.textToSpeech != null) {
            this.textToSpeech.stop();
            this.textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    /**
     * Set the visibility of the delete button belonging to the user profile
     * @param sharedPreferences The user preferences
     */
    private void updateDestinationDeleteButtonVisibility(SharedPreferences sharedPreferences) {
        // Get the profile in current preferences
        boolean isAccompagnateur = sharedPreferences.getBoolean(getResources().getString(R.string.profilePreferencesName), false);

        // change the visibility of the admin layout
        findViewById(R.id.adminLayout).setVisibility(isAccompagnateur ? View.VISIBLE : View.GONE);
    }

    /**
     * Set the visibility of the add destinations button belonging to the user profile
     * @param sharedPreferences The user preferences
     */
    private void updateDestinationAddButtonVisibility(SharedPreferences sharedPreferences) {
        FloatingActionButton addDestBtn = findViewById(R.id.addDestinationButton);
        boolean isAccompagnateur = sharedPreferences.getBoolean(getResources().getString(R.string.profilePreferencesName), false);
        if(!isAccompagnateur) {
            addDestBtn.setVisibility(View.INVISIBLE);
        } else {
            addDestBtn.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Set the visibility of the checkpoint management button belonging to the user profile
     * @param sharedPreferences The user preferences
     */
    private void updateCheckpointsManagementButtonVisibility(SharedPreferences sharedPreferences) {
        FloatingActionButton manageCheckpointsBtn = findViewById(R.id.checkpointButton);
        boolean isAccompagnateur = sharedPreferences.getBoolean(getResources().getString(R.string.profilePreferencesName), false);
        if(!isAccompagnateur) {
            manageCheckpointsBtn.setVisibility(View.INVISIBLE);
        } else {
            manageCheckpointsBtn.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Reload destinations
     */
    private void reloadDestinations() {
        this.initDestinations(this.getSharedPreferences(getResources().getString(R.string.sharedPreferencesKey), Context.MODE_PRIVATE));
    }

    /**
     * Load and display destinations already created
     * @param sharedPreferences Preferences to load
     */
    private void initDestinations(SharedPreferences sharedPreferences) {
        this.destinations = this.destinationData.getAllDestinationFoyer(Constants.getFoyer(this));
        CustomAdapter adapter = new CustomAdapter(this, this.destinations);
        this.grid.setNumColumns(sharedPreferences.getInt("nbrCols", Constants.DEFAULT_NB_COLUMNS));
        this.grid.setAdapter(adapter);

        this.grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                destinationButtonClicked(destinations.get(i), view);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Launch the SearchView activity or the Google Search activity
     * @param view The current view
     */
    public void researchButtonClicked(View view) {

        // launch the Mapbox search intent
        this.askPermission();
        if(this.havePermission) {
            Intent destinationCreationIntent = new Intent(this.getApplicationContext(), SearchView.class);
            System.out.println("SEARCH_VIEW_INTENT : " + destinationCreationIntent);
            this.startActivity(destinationCreationIntent);
        }
    }

    /**
     * Start parameter view when parameter button clicked
     * @param view The current view
     */
    public void parameterButtonClicked(View view) {
        this.askPermission();
        if(this.havePermission) {
            if (this.sharedPreferences.getBoolean(getResources().getString(R.string.profilePreferencesName), false)) { // if profile "accompagnateur"
                Intent parameterIntent = new Intent(this.getApplicationContext(), ParametersView.class);
                this.startActivity(parameterIntent);
            } else { // ask a password to access parameters if profile "utilisateur"

                AlertDialog.Builder builder = new AlertDialog.Builder(HomeView.this, R.style.CustomAlertDialog);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.password_dialog, null);

                EditText inputPassword = dialogView.findViewById(R.id.passwordEditText);
                TextView state = dialogView.findViewById(R.id.password_state);
                state.setVisibility(View.GONE);

                builder.setView(dialogView);
                final AlertDialog dialog = builder.create();

                Button valid = dialogView.findViewById(R.id.passwordValider);
                Button retour = dialogView.findViewById(R.id.passwordRetour);
                Button oublier = dialogView.findViewById(R.id.passwordOublier);

                valid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (inputPassword.getText().toString().equals(sharedPreferences.getString(getResources().getString(R.string.passwordAcc), ""))) {

                            //set the profile to admin
                            SharedPreferences mPrefs = getSharedPreferences(getResources().getString(R.string.sharedPreferencesKey), Context.MODE_PRIVATE);
                            SharedPreferences.Editor prefsEditor = mPrefs.edit();
                            prefsEditor.putBoolean(getResources().getString(R.string.profilePreferencesName), true);
                            prefsEditor.apply();
                            dialog.dismiss();

                            Intent parameterIntent = new Intent(getApplicationContext(), ParametersView.class);
                            startActivity(parameterIntent);
                        } else {
                            state.setVisibility(View.VISIBLE);
                            state.setText("Mot de passe invalide");
                            state.setTextColor(getResources().getColor(R.color.colorRedArrow));
                        }
                    }
                });

                oublier.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builderConf = new AlertDialog.Builder(HomeView.this, R.style.CustomAlertDialog);
                        LayoutInflater inflaterConf = getLayoutInflater();
                        View dialogViewConf = inflaterConf.inflate(R.layout.password_confirm, null);

                        builderConf.setView(dialogViewConf);
                        final AlertDialog dialogConf = builderConf.create();

                        TextView confirmMsgPassword = dialogViewConf.findViewById(R.id.confirmMessagePassword);
                        Button validConf = dialogViewConf.findViewById(R.id.passwordConfValider);
                        Button retConf = dialogViewConf.findViewById(R.id.passwordConfRetour);

                        PhoneData phone = new PhoneData(HomeView.this);
                        String phoneNumber = sharedPreferences.getString(getResources().getString(R.string.phoneNumber), null);
                        String phoneName = phone.getPhoneFromNumber(phoneNumber).getName();
                        String phoneMessage = getResources().getString(R.string.lostPasswordString) + " " + sharedPreferences.getString(getResources().getString(R.string.passwordAcc), "");
                        confirmMsgPassword.setText(confirmMsgPassword.getText().toString() + " " + phoneName);

                        validConf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if(phoneNumber != null && (!phoneMessage.equals(""))) {
                                    SmsManager sms = SmsManager.getDefault();
                                    sms.sendTextMessage(phoneNumber, null, phoneMessage, null, null);
                                    state.setVisibility(View.VISIBLE);
                                    state.setText("SMS envoyé à " + phoneNumber);
                                    state.setTextColor(getResources().getColor(R.color.greenADAPEI));
                                } else {
                                    System.err.println("(HomeView line 567) LostPassword : phoneNumber or passwordAcc null");
                                }

                                dialogConf.dismiss();
                            }
                        });

                        retConf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogConf.dismiss();
                            }
                        });

                        dialogConf.show();
                    }
                });

                retour.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        }
    }

    /**
     * Start destination creation view when create destination button clicked
     * @param view The current view
     */
    public void addButtonClicked(View view) {
        this.askPermission();
        if(this.havePermission) {
            Intent destinationCreationIntent = new Intent(this.getApplicationContext(), AddView.class);
            this.startActivity(destinationCreationIntent);
        }
    }

    /**
     * Launch the ArrowNavCheckpointManagementView activity
     * @param view The related view
     */
    public void checkpointsFolderButtonClicked(View view) {
        CheckPointData cp = new CheckPointData(this);

        if(cp.getNbCheckpoint() < 1) {
            Toast.makeText(getApplicationContext(), R.string.oneCheckpointNeeded, Toast.LENGTH_SHORT).show();
        } else {
            Intent arrowNavCheckpointManagementIntent = new Intent(this.getApplicationContext(), ArrowNavCheckpointManagementView.class);
            this.startActivity(arrowNavCheckpointManagementIntent);
        }
    }

    /**
     * Delete the selected destination
     * @param view The current view
     */
    public void deleteDestinationButtonClicked(View view) {
        if(this.destinationSelected != null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(HomeView.this, R.style.CustomAlertDialog);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.delete_dialog, null);

            TextView textViewDelete = dialogView.findViewById(R.id.deleteTextView);
            textViewDelete.setText("Voulez-vous supprimer " + destinationSelected.getNickname() + " ?");

            builder.setView(dialogView);
            final AlertDialog dialog = builder.create();

            Button valid = dialogView.findViewById(R.id.deleteOui);
            Button retour = dialogView.findViewById(R.id.deleteNon);

            valid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    destinationData.deleteDestination(destinationSelected);
                    resetSelectedDestination();
                    reloadDestinations();
                    dialog.dismiss();
                }
            });

            retour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        } else {
            Toast.makeText(getApplicationContext(), R.string.anyDestSelect, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Start the navigation when start button clicked
     * @param view The current view
     */
    public void homeStartButtonClicked(View view) {
        PhoneData p1 = new PhoneData(this);

        if(destinationSelected != null) {
            if (p1.noPhoneNumber()) {
                Bundle extras = new Bundle();
                extras.putString(getResources().getString(R.string.DestinationHomeToNav), Constants.GSON.toJson(destinationSelected));
                LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                if (manager != null) {

                    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(gpsOptionsIntent);
                    } else {
                        Intent navIntent;

                        if (sharedPreferences.getBoolean(getResources().getString(R.string.useMapPreferencesName), false)) {
                            navIntent = new Intent(this, MapNavView.class);
                        } else {
                            navIntent = new Intent(this, ArrowNavView.class);
                        }
                        navIntent.putExtras(extras);
                        startActivity(navIntent);
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.enterPhoneFromParam, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.anyDestSelect, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateHomeStartButton() {

        DestinationData d = new DestinationData(this);
        Button startButton = (Button) findViewById(R.id.homeStartButton);

        if(d.getNbDestination() < 1) {
            startButton.setTextColor(getResources().getColor(R.color.greenOffButtonADAPEI));
        } else {
            startButton.setTextColor(getResources().getColor(R.color.mapboxWhite));
        }

    }

    /**
     * Select the destination when clicked
     * @param destination The current destination
     */
    public void destinationButtonClicked(Destination destination, View view) {
        if (destination != null) {
            resetSelectedDestination();

            this.destinationSelected = destination;
            this.view_selected = view;
            this.textToSpeech.speak(destination.getPronunciation(), TextToSpeech.QUEUE_FLUSH, null, null);

            DrawableCompat.setTint(DrawableCompat.wrap(view.findViewById(R.id.textView).getBackground()), getResources().getColor(R.color.greenADAPEI));
            DrawableCompat.setTint(DrawableCompat.wrap(view.findViewById(R.id.imageButton).getBackground()), getResources().getColor(R.color.greenADAPEI));
            ((TextView)(view.findViewById(R.id.textView))).setTextColor(getResources().getColor(R.color.mapboxWhite));

            System.out.println("DESTINATION CHOISIE (lat,lon) : " + destination.getLatitude() + "," + destination.getLongitude());
        }
    }

    /**
     * Deselect the current selected destination
     */
    private void resetSelectedDestination() {
        if (this.destinationSelected != null && this.view_selected != null) {
            DrawableCompat.setTint(DrawableCompat.wrap(view_selected.findViewById(R.id.textView).getBackground()), getResources().getColor(R.color.colorPrimary));
            DrawableCompat.setTint(DrawableCompat.wrap(view_selected.findViewById(R.id.imageButton).getBackground()), getResources().getColor(R.color.colorPrimary));
            ((TextView)(view_selected.findViewById(R.id.textView))).setTextColor(getResources().getColor(R.color.greenADAPEI));
            this.destinationSelected = null;
            this.view_selected = null;
        }
    }

    /**
     * Enable the start button
     * @param wantedState The state wanted for the start button
     */
    private void updateStartButton(boolean wantedState) {
        if (!startButtonEnabled == wantedState) {
            Button homeStartButton = findViewById(R.id.homeStartButton);
            homeStartButton.setEnabled(wantedState);
            this.startButtonEnabled = wantedState;
        }
    }

    /**
     * launch the alertDialog used to customize the selected destination
     * @param view
     */
    public void editDestinationButtonClicked(View view) {
        if(this.destinationSelected != null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(HomeView.this, R.style.CustomAlertDialog);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.alert_dialog_modification, null);

            TextView adress = dialogView.findViewById(R.id.edit_state);
            EditText inputNickname = dialogView.findViewById(R.id.userInputNickname);
            EditText inputPronunciation = dialogView.findViewById(R.id.userInputPronunciation);
            ImageButton listenButton = dialogView.findViewById(R.id.buttonListen);
            Button validateButton = dialogView.findViewById(R.id.buttonValidate);
            Button retour = dialogView.findViewById(R.id.editRetour);

            builder.setView(dialogView);
            final AlertDialog dialog = builder.create();

            adress.setText("Modification de : " + this.destinationSelected.getDisplayName());

            validateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!inputNickname.getText().toString().equals("")) {
                        if (!inputPronunciation.getText().toString().equals("")) {
                            if (inputNickname.getText().toString().length() < 30) {
                                String nickname = inputNickname.getText().toString();
                                String pronunciation = inputPronunciation.getText().toString();

                                destinationData.updateSurname(destinationSelected, nickname);
                                destinationData.updatePronunciation(destinationSelected, pronunciation);

                                resetSelectedDestination();
                                reloadDestinations();
                                dialog.dismiss();
                            } else {
                                Toast.makeText(HomeView.this, R.string.maxLengthModifDest, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(HomeView.this, R.string.validerNewPrononciation, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(HomeView.this, R.string.validerNewNickname, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            listenButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (inputPronunciation.getText() != null) {
                        textToSpeech.speak(inputPronunciation.getText(), TextToSpeech.QUEUE_FLUSH, null,null);
                    }
                }
            });

            retour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        } else {
            Toast.makeText(getApplicationContext(), R.string.anyDestSelect, Toast.LENGTH_SHORT).show();
        }
    }
}
