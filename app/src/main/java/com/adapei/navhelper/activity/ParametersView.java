package com.adapei.navhelper.activity;

import static com.adapei.navhelper.Constants.defaultParam;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.adapei.navhelper.City;
import com.adapei.navhelper.PhoneNumber;
import com.adapei.navhelper.R;
import com.adapei.navhelper.database.PhoneData;
import com.mapbox.api.directions.v5.DirectionsCriteria;

import java.util.List;

/**
 * Class for changing the style of the navigation, the center, the locomotion profile, the status and the safety phone number
 */
public class ParametersView extends AppCompatActivity {

    /**
     * mValidate the validate button to get all the information set by the user on the ParametersView and save them in the SharedPreferences
     */
    private PhoneData accesData;

    private Button mValidate;
    private RadioGroup mDisplayMapRadioGroup;
    private RadioGroup mFoyerRadioGroup;
    private RadioGroup mProfileRadioGroup;
    private RadioGroup affichageRadioGroup;

    private EditText radiusArea;
    private EditText passwordArea;

    private CheckBox pedestrianCheckBox;
    private CheckBox bikeCheckBox;
    private CheckBox carCheckBox;
    private CheckBox busCheckBox;

    private Spinner numberSelector;
    private Button addNumber;
    private Button removeButton;

    /**
     * list of the phoneNumbers available to help the user
     */
    public List<PhoneNumber> numbers;
    private ArrayAdapter<PhoneNumber> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameters_view);
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.accesData = new PhoneData(this);

        //Find the different elements on the ParametersView by their id
        this.mValidate = findViewById(R.id.validateButton);
        this.mDisplayMapRadioGroup = findViewById(R.id.displayMapRadioGroup);
        this.mFoyerRadioGroup = findViewById(R.id.foyerRadioGroup);
        this.mProfileRadioGroup = findViewById(R.id.profileRadioGroup);
        this.affichageRadioGroup = findViewById(R.id.affichageRadioGroup);

        this.radiusArea = (EditText) findViewById(R.id.radiusArea);
        this.passwordArea = (EditText) findViewById(R.id.passwordEditText);

        this.numberSelector = findViewById(R.id.selectNumber);
        this.addNumber = findViewById(R.id.addNumberButton);
        this.removeButton = findViewById(R.id.deleteNumButton);

        this.pedestrianCheckBox = findViewById(R.id.pedestrianCheckBoxButton);
        this.bikeCheckBox = findViewById(R.id.bikeCheckBoxButton);
        this.carCheckBox = findViewById(R.id.carCheckBoxButton);
        this.busCheckBox = findViewById(R.id.busCheckBoxButton);

        this.numbers = this.accesData.getAllPhoneNumbers();

        this.adapter = new ArrayAdapter<>(this, R.layout.spinner_item, numbers);
        this.adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        this.numberSelector.setAdapter(adapter);
        this.mDisplayMapRadioGroup.setFocusableInTouchMode(true);
        this.mDisplayMapRadioGroup.requestFocus();

        this.addNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ParametersView.this, R.style.CustomAlertDialog);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.parameter_dialog, null);

                EditText inputNum = dialogView.findViewById(R.id.inputNum);
                EditText inputName = dialogView.findViewById(R.id.inputName);

                builder.setView(dialogView);
                final AlertDialog dialog = builder.create();

                Button valid = dialogView.findViewById(R.id.parameterValider);
                Button retour = dialogView.findViewById(R.id.parameterRetour);

                valid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String newNum = inputNum.getText().toString();
                        if(newNum.length() <= 10 && newNum.length() > 0) {
                            addNewNumber(newNum, inputName.getText().toString().replaceAll("\"", " "));
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getApplicationContext(), "Numéro invalide : " +  newNum, Toast.LENGTH_SHORT).show();
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
            }
        });

        //Long click on item of the list = remove
        this.removeButton.setVisibility(View.INVISIBLE);
        this.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneNumber phone = (PhoneNumber)numberSelector.getSelectedItem();

                AlertDialog.Builder builder = new AlertDialog.Builder(ParametersView.this, R.style.CustomAlertDialog);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.delete_dialog, null);

                TextView textViewDelete = dialogView.findViewById(R.id.deleteTextView);
                textViewDelete.setText("Voulez-vous supprimer " + phone.getNumber() + " ?");

                builder.setView(dialogView);
                final AlertDialog dialog = builder.create();

                Button valid = dialogView.findViewById(R.id.deleteOui);
                Button retour = dialogView.findViewById(R.id.deleteNon);

                valid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        accesData.removePhone(phone);

                        if(accesData.getAllPhoneNumbers().size() == 0) removeButton.setVisibility(View.INVISIBLE);

                        adapter.remove(phone);
                        adapter.notifyDataSetChanged();
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
            }
        });

        //Initialize the default value of each of the elements of the view with the user initial settings
        initializeDisplay();
    }

    /**
     * add a new number to the data base
     * @param number the number to add
     * @param name the name to add
     */
    private void addNewNumber(String number, String name) {
        PhoneNumber phoneNumber = new PhoneNumber(number, name);
        if(!this.accesData.isIn(phoneNumber)) {
            this.accesData.addPhone(phoneNumber);
            this.adapter.add(phoneNumber);
            this.removeButton.setVisibility(View.VISIBLE);
            this.adapter.notifyDataSetChanged();
        }
    }

    /**
     * called when the ParametersView is opened to set all the user initial settings on the different RadioGroup
     */
    private void initializeDisplay() {
        //Get the SharedPreferences to get the user initial settings
        SharedPreferences mPrefs = this.getSharedPreferences(getResources().getString(R.string.sharedPreferencesKey), Context.MODE_PRIVATE);

        //Set the current user settings on the different RadioGroup/EditText
        boolean useMap = mPrefs.getBoolean(getResources().getString(R.string.useMapPreferencesName), Boolean.parseBoolean(defaultParam[0]));
        if(useMap) {
            this.mDisplayMapRadioGroup.check(R.id.yesRadioButton);
        } else {
            this.mDisplayMapRadioGroup.check(R.id.noRadioButton);
        }

        //Set the city between Lorient and Auray
        String city = mPrefs.getString(getResources().getString(R.string.cityPreferencesName), defaultParam[1]);
        if(city.equals(City.LORIENT.getName())){
            this.mFoyerRadioGroup.check(R.id.lorientRadioButton);
        } else {
            this.mFoyerRadioGroup.check(R.id.aurayRadioButton);
        }

        //Set the type of locomotion
        boolean pedestrian = mPrefs.getBoolean(getResources().getString(R.string.pedestrianPreferencesName), Boolean.parseBoolean(defaultParam[2]));
        boolean bike = mPrefs.getBoolean(getResources().getString(R.string.bikePreferencesName), Boolean.parseBoolean(defaultParam[3]));
        boolean car = mPrefs.getBoolean(getResources().getString(R.string.carPreferencesName), Boolean.parseBoolean(defaultParam[4]));
        boolean bus = mPrefs.getBoolean(getResources().getString(R.string.busPreferencesName), Boolean.parseBoolean(defaultParam[5]));

        if (pedestrian) {
            this.pedestrianCheckBox.setChecked(true);
        }

        if (bike) {
            this.bikeCheckBox.setChecked(true);
        }

        if (car) {
            this.carCheckBox.setChecked(true);
        }

        if (bus) {
            this.busCheckBox.setChecked(true);
        }

        //Set the profile of user between guide or not
        boolean profile = mPrefs.getBoolean(getResources().getString(R.string.profilePreferencesName), Boolean.parseBoolean(defaultParam[6]));
        if(profile) {
            this.mProfileRadioGroup.check(R.id.guideRadioButton);
        } else {
            this.mProfileRadioGroup.check(R.id.userRadioButton);
        }

        int nbrCols = mPrefs.getInt("nbrCols", Integer.parseInt(defaultParam[7]));
        if(nbrCols == 2) {
            this.affichageRadioGroup.check(R.id.radioButton_2col);
        }
        else {
            this.affichageRadioGroup.check(R.id.radioButton_3col);
        }

        String numberSelected = mPrefs.getString("spinnerNumber", defaultParam[8]);
        boolean find = false;
        int i=0;
        while(!find && i < this.numbers.size()-1) {
            if(this.numbers.get(i).getNumber().equals(numberSelected)) {
                find = true;
            } else {
                i++;
            }
        }
        this.numberSelector.setSelection(i);

        this.radiusArea.setText(mPrefs.getString("radius", defaultParam[9]));

        if (!this.getSharedPreferences(getResources().getString(R.string.sharedPreferencesKey), Context.MODE_PRIVATE).getString(getResources().getString(R.string.passwordAcc), "").equals("")) {
            this.passwordArea.setText(this.getSharedPreferences(getResources().getString(R.string.sharedPreferencesKey), Context.MODE_PRIVATE).getString(getResources().getString(R.string.passwordAcc), "default"));
        }
    }

    /**
     * Quit the view when the abort button is pressed
     * @param view The current view
     */
    public void abortButtonClicked(View view) {
        SharedPreferences mPrefs = this.getSharedPreferences(getResources().getString(R.string.sharedPreferencesKey), Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();

        if(this.numbers.size() <= 0) {
            prefsEditor.putString("spinnerNumber", "0000000000");
            prefsEditor.apply();
        }
        this.finish();
    }

    @Override
    public void onBackPressed() {
        this.abortButtonClicked(findViewById(R.id.abortButton));
    }

    /**
     * Validation button clicked
     * @param view
     */
    public void validateButtonClicked(View view) {
        if (this.pedestrianCheckBox.isChecked() || this.bikeCheckBox.isChecked() || this.carCheckBox.isChecked() || this.busCheckBox.isChecked()) {
            if(!this.radiusArea.getText().toString().equals("")) {
                if(Integer.parseInt(radiusArea.getText().toString()) > 0 && Integer.parseInt(radiusArea.getText().toString()) < 1000) {
                    PhoneData p = new PhoneData(this);
                    if(p.getAllPhoneNumbers().size() > 0) {
                        if(!this.passwordArea.getText().toString().equals("")) {
                            //Get the value of the RadioGroup mDisplayMapRadioGroup to know if the user want to use the map or the arrow
                            boolean useMap = this.mDisplayMapRadioGroup.getCheckedRadioButtonId() == R.id.yesRadioButton;

                            //Get the value of the RadioGroup mFoyerRadioGroup to know if the user want to use the foyer Lorient or Auray
                            String city = (String) findViewById(this.mFoyerRadioGroup.getCheckedRadioButtonId()).getTag();

                            //Get the value of the RadioGroup mLocomotionRadioGroup to know if the user is walking, cycling or driving

                            boolean pedestrian = this.pedestrianCheckBox.isChecked();
                            boolean bike = this.bikeCheckBox.isChecked();
                            boolean car = this.carCheckBox.isChecked();
                            boolean bus = this.busCheckBox.isChecked();

                            String favorite = DirectionsCriteria.PROFILE_WALKING;
                            if (car) {
                                favorite = DirectionsCriteria.PROFILE_DRIVING;
                            } else if (bike) {
                                favorite = DirectionsCriteria.PROFILE_CYCLING;
                            }

                            //Get the value of the RadioGroup mProfileRadioGroup to know if the user is a guide or a lambda user
                            boolean profile = this.mProfileRadioGroup.getCheckedRadioButtonId() == R.id.guideRadioButton;

                            //Get the value of the RadioGroup affichageRadioGroup to know how many columns to show
                            int cols = Integer.parseInt((String) findViewById(this.affichageRadioGroup.getCheckedRadioButtonId()).getTag());

                            String passwordAcc = this.passwordArea.getText().toString();

                            //Edit the SharedPreferences with the new user settings
                            SharedPreferences mPrefs = this.getSharedPreferences(getResources().getString(R.string.sharedPreferencesKey), Context.MODE_PRIVATE);
                            SharedPreferences.Editor prefsEditor = mPrefs.edit();

                            prefsEditor.putBoolean(getResources().getString(R.string.useMapPreferencesName), useMap);
                            prefsEditor.putString(getResources().getString(R.string.cityPreferencesName), city);
                            prefsEditor.putBoolean(getResources().getString(R.string.pedestrianPreferencesName), pedestrian);
                            prefsEditor.putBoolean(getResources().getString(R.string.bikePreferencesName), bike);
                            prefsEditor.putBoolean(getResources().getString(R.string.carPreferencesName), car);
                            prefsEditor.putBoolean(getResources().getString(R.string.busPreferencesName), bus);
                            prefsEditor.putBoolean(getResources().getString(R.string.profilePreferencesName), profile);
                            prefsEditor.putString(getResources().getString(R.string.favoriteTypePreferencesName), favorite);
                            prefsEditor.putString(getResources().getString(R.string.phoneNumber), ((PhoneNumber) (this.numberSelector.getSelectedItem())).getNumber());
                            prefsEditor.putInt("nbrCols", cols);
                            prefsEditor.putString("radius", radiusArea.getText().toString());
                            prefsEditor.putString(getResources().getString(R.string.passwordAcc), passwordAcc);

                            prefsEditor.apply();

                            this.finish();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.passwordAccNeeded, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.phoneNumberNeeded, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.correctRayonNeeded, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.rayonNeeded, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.deplacementTypeNeeded, Toast.LENGTH_SHORT).show();
        }
    }
}
