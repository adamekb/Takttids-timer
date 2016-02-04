package kth.ict.adam.grothproofofconcept;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private NumberPicker np1, np2;
    private Button startButton, doneButton;
    private long productionTime, currentTaktTime, timer3Time;
    private TextView timer2;
    private boolean timer1Done, timer2Done, doneButtonPressed;
    private Chronometer timer3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        np1 = (NumberPicker) findViewById(R.id.numberPicker1);
        np2 = (NumberPicker) findViewById(R.id.numberPicker2);
        startButton = (Button) findViewById(R.id.startButton);
        doneButton = (Button) findViewById(R.id.doneButton);
        timer2 = (TextView) findViewById(R.id.timer2);
        timer3 = (Chronometer) findViewById(R.id.timer3);

        np1.setMaxValue(10000);
        np1.setMinValue(0);
        np1.setWrapSelectorWheel(false);
        np1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                np1.setValue(newVal);
            }
        });

        np2.setMaxValue(10000);
        np2.setMinValue(0);
        np2.setWrapSelectorWheel(false);
        np2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                np2.setValue(newVal);
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                startButton.setEnabled(false);
                startButton.setAlpha(0.7F);
                currentTaktTime = np2.getValue() * 1000;
                productionTime = np1.getValue() * 1000 * 60;
                timer3.setBase(SystemClock.elapsedRealtime());
                doneButtonPressed = false;
                timer3Time = 0;
                startTimer1();
                startTimer2();
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                doneButtonPressed = true;
                pauseTimer3();
                if (timer2Done && !timer1Done) {
                    doneButtonPressed = false;
                    startTimer2();
                }
            }
        });

    }

    private void startTimer1() {
        timer1Done = false;
        new MoreAccurateTimer(productionTime, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                startButton.setEnabled(true);
                startButton.setAlpha(1F);
                timer1Done = true;
                pauseTimer3();
            }
        }.start();
    }

    private void startTimer2() {
        timer2Done = false;
        timer2.setText("" + String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(currentTaktTime),
                TimeUnit.MILLISECONDS.toSeconds(currentTaktTime) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(currentTaktTime))));

        new MoreAccurateTimer(currentTaktTime, 1000) {

            public void onTick(long millisUntilFinished) {
                if (!timer1Done) {
                    millisUntilFinished = Math.round((float)millisUntilFinished/1000)*1000;
                    timer2.setText("" + String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                }
            }

            public void onFinish() {
                timer2Done = true;
                if (doneButtonPressed && !timer1Done) {
                    doneButtonPressed = false;
                    startTimer2();
                } else if (!timer1Done) {
                    timer2.setText("00:00");
                    startTimer3();
                }
            }
        }.start();
    }

    private void startTimer3() {
        if (timer3Time == 0) {
            timer3.setBase(SystemClock.elapsedRealtime());
        } else {
            timer3.setBase(SystemClock.elapsedRealtime() + timer3Time);
        }
        timer3.start();
    }

    private void pauseTimer3() {
        timer3Time = timer3.getBase() - SystemClock.elapsedRealtime();
        timer3.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
