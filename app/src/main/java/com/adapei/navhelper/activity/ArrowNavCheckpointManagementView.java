package com.adapei.navhelper.activity;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.adapei.navhelper.Checkpoint;
import com.adapei.navhelper.CheckpointAdapter;
import com.adapei.navhelper.R;
import com.adapei.navhelper.database.CheckPointData;

import java.util.List;
import java.util.Locale;

/**
 * Class managing the checkpoints
 */
public class ArrowNavCheckpointManagementView extends AppCompatActivity {

    /**
     * The text to speech instance
     */
    private TextToSpeech textToSpeech;

    /**
     * selected checkpoint
     */
    private Checkpoint selectedCheckpoint;

    /**
     * Selected view associated to the checkpoint
     */
    private View selectedView;

    /**
     * List of the settled checkpoints of the route
     */
    private List<Checkpoint> checkpoints;

    /**
     * the data base for checkpoints
     */
    private CheckPointData bdd;

    private RecyclerView recyclerView;
    private CheckpointAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrow_nav_checkpoint_management_view);
        this.bdd = new CheckPointData(this);
        this.textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.FRANCE);
                }
            }
        });
        this.recyclerView = findViewById(R.id.recyclerView);
        this.recyclerView.setHasFixedSize(true);

        this.checkpoints = this.bdd.getAllCheckpoints();

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        this.recyclerView.setLayoutManager(manager);

        initCheckpoints();

        SearchView searchView = findViewById(R.id.searchCheckpoint);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                deselectCurrent();
                return false;
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });
    }

    @Override
    protected void onDestroy() {
        textToSpeech.stop();
        super.onDestroy();

    }

    @Override
    public void finish() {
        this.textToSpeech.stop();
        this.textToSpeech.shutdown();
        super.finish();
    }

    /**
     * display all the checkpoints
     */
    public void initCheckpoints() {
        //Initialize checkpoint with image
        this.adapter = new CheckpointAdapter(this, this.checkpoints);

        adapter.setOnItemClickListener(new CheckpointAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, Checkpoint checkpoint) {
                deselectCurrent();

                textToSpeech.speak(checkpoint.getPronunciation(), TextToSpeech.QUEUE_FLUSH, null, null);
                DrawableCompat.setTint(DrawableCompat.wrap(view.findViewById(R.id.textCheckpoint).getBackground()), getResources().getColor(R.color.greenADAPEI));
                DrawableCompat.setTint(DrawableCompat.wrap(view.findViewById(R.id.imageCheckpoint).getBackground()), getResources().getColor(R.color.greenADAPEI));
                ((TextView) (view.findViewById(R.id.textCheckpoint))).setTextColor(getResources().getColor(R.color.mapboxWhite));
                selectedCheckpoint = checkpoint;
                selectedView = view;
            }
        });

        this.recyclerView.setAdapter(adapter);
    }

    /**
     * Deselect the current selected item
     */
    public void deselectCurrent() {
        if(selectedView != null) {
            DrawableCompat.setTint(DrawableCompat.wrap(selectedView.findViewById(R.id.textCheckpoint).getBackground()), getResources().getColor(R.color.colorPrimary));
            DrawableCompat.setTint(DrawableCompat.wrap(selectedView.findViewById(R.id.imageCheckpoint).getBackground()), getResources().getColor(R.color.colorPrimary));
            ((TextView) (selectedView.findViewById(R.id.textCheckpoint))).setTextColor(getResources().getColor(R.color.greenADAPEI));
            this.selectedCheckpoint = null;
            this.selectedView = null;
        }
    }

    /**
     * Delete all the selected checkpoints
     * @param view The current view
     */
    public void deleteButtonClicked(View view) {
        if(selectedCheckpoint != null) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ArrowNavCheckpointManagementView.this, R.style.CustomAlertDialog);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.delete_checkpoint_confirm, null);

            builder.setView(dialogView);
            final android.app.AlertDialog dialog = builder.create();

            Button retour = dialogView.findViewById(R.id.passwordConfRetour);
            Button valid = dialogView.findViewById(R.id.passwordConfValider);

            retour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            valid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkpoints.remove(selectedCheckpoint);
                    bdd.deleteCheckpoint(selectedCheckpoint);
                    adapter.notifyDataSetChanged();
                    deselectCurrent();
                    dialog.dismiss();
                }
            });

            dialog.show();
        }
    }

    /**
     * Quit the view when the abort button is pressed
     * @param view The current view
     */
    public void abortButtonClicked(View view) { this.finish(); }

    /**
     * edit the current selected checkpoint using an alertDialog
     * @param view The current view
     */
    public void editButtonClicked(View view) {

        if(this.selectedCheckpoint != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ArrowNavCheckpointManagementView.this, R.style.CustomAlertDialog);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.checkpoint_edit_dialog, null);
            builder.setView(dialogView);
            final AlertDialog dialog = builder.create();

            TextView inputName = dialogView.findViewById(R.id.checkpoint_edit_textview);
            inputName.setText(getResources().getString(R.string.xml_modifDest_ModifOf) + this.selectedCheckpoint.getDisplayName());

            ImageButton ecoute = dialogView.findViewById(R.id.listenCheckpointButton);
            ecoute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Get the user pronunciation entry
                    String pronunciationInput = ((EditText) dialogView.findViewById(R.id.inputPrononcName)).getText().toString();
                    //Pronounce it
                    textToSpeech.speak(pronunciationInput, TextToSpeech.QUEUE_FLUSH, null, null);
                }
            });

            Button valid = dialogView.findViewById(R.id.cpModifValider);
            valid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    EditText inputName = dialogView.findViewById(R.id.inputCPName);
                    EditText inputPro = dialogView.findViewById(R.id.inputPrononcName);

                    if (!inputName.getText().toString().equals("") && !inputPro.getText().toString().equals("")) {
                        Checkpoint cp = selectedCheckpoint;
                        bdd.updatePronunciation(cp, inputPro.getText().toString());
                        bdd.updateName(cp, inputName.getText().toString());
                        checkpoints.get(checkpoints.indexOf(selectedCheckpoint)).setPronunciation(inputPro.getText().toString());
                        checkpoints.get(checkpoints.indexOf(selectedCheckpoint)).setName(inputName.getText().toString());
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(ArrowNavCheckpointManagementView.this, R.string.fillEverything, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            Button retour = dialogView.findViewById(R.id.cpModifRetour);
            retour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        }
    }

    /**
     * end the activity
     * @param view The current view
     */
    public void validateButtonClicked(View view) {
        this.finish();
    }
}