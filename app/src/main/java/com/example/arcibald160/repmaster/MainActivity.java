package com.example.arcibald160.repmaster;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    TextView showValue, showExercise;
    RepManager mRepManager = null;
    ProgressBar progressBar;

    String username, choosenExerise, realRepNumber;

    ToneGenerator tone = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
    CountDownTimer timer = new CountDownTimer(5000,1000) {
        @Override
        public void onTick(long l) {
            tone.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 100);

        }

        @Override
        public void onFinish() {
            tone.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 500);
            mRepManager.register(username, choosenExerise, realRepNumber);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // set initial values
        int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
        username = getString(R.string.default_username);
        choosenExerise = getString(R.string.default_exercise);
        realRepNumber = getString(R.string.default_realRepNumber);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

        showValue = (TextView) findViewById(R.id.rep_number);
        showExercise = (TextView) findViewById(R.id.show_exercise);

        mRepManager = new RepManager(this);
        progressBar = (ProgressBar)  findViewById(R.id.loadingCircle);
        final Button toggleButton = (Button) findViewById(R.id.toggle_btn);

        // inital state
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (toggleButton.getText().toString() == getString(R.string.start)) {
                    showValue.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    timer.start();
                    toggleButton.setText(getString(R.string.end));
                } else {
                    mRepManager.unregister();
                    showValue.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);

                    toggleButton.setText(getString(R.string.start));
                    showValue.setText(String.format("%d", mRepManager.getReps()));
                  
                    showExercise.setText(mRepManager.getExcersise());
                    mRepManager.makeFilesVisibleOnPC(MainActivity.this);

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.settings_button:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                // for custom layout
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                // Pass null as the parent view because its going in the dialog layout
                View mView = inflater.inflate(R.layout.settings_main, null);

                builder.setTitle(R.string.settings);
                // settings (form) elements
                final TextView user = (TextView) mView.findViewById(R.id.settings_username);
                final TextView rNumber = (TextView) mView.findViewById(R.id.settings_realRepNumber);
                final Spinner exercise = (Spinner) mView.findViewById(R.id.settings_exercise);
                builder.setView(mView);

                // set value if choosen
                if (username != getString(R.string.default_username)) {
                    user.setText(username);
                }
                if (choosenExerise != getString(R.string.default_exercise)) {
                    int spinnerPosition = Arrays.asList(getResources().getStringArray(R.array.exercise_array)).indexOf(choosenExerise);
                    exercise.setSelection(spinnerPosition);
                }
                if (realRepNumber != getString(R.string.default_realRepNumber)) {
                    rNumber.setText(realRepNumber);
                }

                // apply
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        username = user.getText().toString();
                        choosenExerise = exercise.getSelectedItem().toString();
                        realRepNumber = rNumber.getText().toString();
                    }
                });
                // dismiss
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }
        return true;
    }
}
