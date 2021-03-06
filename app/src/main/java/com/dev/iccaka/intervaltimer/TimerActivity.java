package com.dev.iccaka.intervaltimer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class TimerActivity extends Activity {

    //view from activity_timer
    private TextView trainingSetsQuantity;
    private TextView trainingWorkQuantity;
    private TextView trainingRestQuantity;
    private TextView trainingMotivationalText;
    private ConstraintLayout thisActivity;
    private Button pauseBtn;
    private Button continueBtn;
    private Button endBtn;

    //starting parameters that we get from the main activity
    private int startingWorkSecs;
    private int startingWorkMins;
    private int startingRestSecs;
    private int startingRestMins;

    //current parameters
    private int sets;
    private int workSecs;
    private int workMins;
    private int restSecs;
    private int restMins;

    //parameters when we stop, so we know where to continue from
    private int pausedSets;
    private int pausedWorkSecs;
    private int pausedWorkMins;
    private int pausedRestSecs;
    private int pausedRestMins;

    //the two main timers
    private CountDownTimer workCountDownTimer;
    private CountDownTimer restCountDownTimer;

    //sounds when we press the buttons
    private MediaPlayer mpToWork;
    private MediaPlayer mpToRest;
    private MediaPlayer mpToPause;
    private MediaPlayer mpToContinue;
    private MediaPlayer mpToEnd;

    //booleans to see if the timer is working and if the timer has been paused
    private boolean isWorkOn;
    private boolean hasBeenPaused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        this.isWorkOn = true;
        this.hasBeenPaused = false;

        this.mpToWork = MediaPlayer.create(this.getApplicationContext(), R.raw.beep9);
        this.mpToRest = MediaPlayer.create(this.getApplicationContext(), R.raw.beep5);
        this.mpToPause = MediaPlayer.create(this.getApplicationContext(), R.raw.button3);
        this.mpToContinue = MediaPlayer.create(this.getApplicationContext(), R.raw.button5);
        this.mpToEnd = MediaPlayer.create(this.getApplicationContext(), R.raw.button8);

        this.continueBtn = findViewById(R.id.continueBtn);
        this.pauseBtn = findViewById(R.id.pauseBtn);
        this.endBtn = findViewById(R.id.endBtn);

        this.endBtn.setVisibility(View.GONE);
        this.continueBtn.setVisibility(View.GONE);

        this.thisActivity = findViewById(R.id.timerActivity);
        this.thisActivity.setBackgroundColor(Color.RED);

        this.trainingSetsQuantity = findViewById(R.id.trainingSetsQuantity);
        this.trainingWorkQuantity = findViewById(R.id.trainingWorkQuantity);
        this.trainingRestQuantity = findViewById(R.id.trainingRestQuantity);
        this.trainingMotivationalText = findViewById(R.id.trainingMotivationalText);

        this.trainingRestQuantity.setVisibility(View.GONE);

        Bundle extras = getIntent().getExtras();
        this.sets = extras.getInt("sets");
        this.workSecs = extras.getInt("workSecs");
        this.workMins = extras.getInt("workMins");
        this.restSecs = extras.getInt("restSecs");
        this.restMins = extras.getInt("restMins");

        this.startingWorkSecs = this.workSecs;
        this.startingWorkMins = this.workMins;
        this.startingRestSecs = this.restSecs;
        this.startingRestMins = this.restMins;

        //TODO fix the ContDownTimers and add the other functionality(end and pause timer)

        this.updateSets();
        this.updateWork();
        this.updateRest();

        this.workCountDownTimer = new CountDownTimer((((this.workMins * 60) + this.workSecs) * 1000) + 1, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                decrementWork(trainingWorkQuantity);
                updateWork();
            }

            @Override
            public void onFinish() {
                decrementSets(trainingSetsQuantity);
                updateSets();
                startRestTimer();
            }
        };

        this.restCountDownTimer = new CountDownTimer((((this.restMins * 60) + this.restSecs) * 1000) + 1, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                decrementRest(trainingRestQuantity);
                updateRest();
            }

            @Override
            public void onFinish() {
                if (sets > 0) {
                    startWorkTimer();
                } else {
                    endTimer(endBtn);
                }
            }
        };


        this.startWorkTimer();

    }

    @Override
    public void onBackPressed() {
        this.mpToWork.start();
    }

    private void startWorkTimer() {
        this.thisActivity.setBackgroundColor(Color.RED);

        this.mpToWork.start();

        this.workSecs = this.startingWorkSecs;
        this.workMins = this.startingWorkMins;

        this.trainingRestQuantity.setVisibility(View.GONE);
        this.trainingWorkQuantity.setVisibility(View.VISIBLE);

        this.trainingMotivationalText.setText("Work it");

        this.workCountDownTimer.start();
    }

    private void startRestTimer() {
        this.thisActivity.setBackgroundColor(Color.GREEN);

        this.mpToRest.start();

        this.restSecs = this.startingRestSecs;
        this.restMins = this.startingRestMins;

        this.trainingRestQuantity.setVisibility(View.VISIBLE);
        this.trainingWorkQuantity.setVisibility(View.GONE);

        this.trainingMotivationalText.setText("Rest now");

        this.restCountDownTimer.start();
    }

    public void continueTimer(View view) {
        this.mpToContinue.start();

        if (this.isWorkOn) {
            this.workCountDownTimer = new CountDownTimer((((this.pausedWorkMins * 60) + this.pausedWorkSecs) * 1000) + 1, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    decrementWork(trainingWorkQuantity);
                    updateWork();
                }

                @Override
                public void onFinish() {
                    decrementSets(trainingSetsQuantity);
                    updateSets();
                    startRestTimer();
                }
            };

            this.workCountDownTimer.start();

            this.thisActivity.setBackgroundColor(Color.RED);
        }
        else {
            this.restCountDownTimer = new CountDownTimer((((this.pausedRestMins * 60) + this.pausedRestSecs) * 1000) + 1, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    decrementRest(trainingRestQuantity);
                    updateRest();
                }

                @Override
                public void onFinish() {
                    if (sets > 0) {
                        startWorkTimer();
                    } else {
                        endTimer(endBtn);
                    }
                }
            };

            this.restCountDownTimer.start();

            this.thisActivity.setBackgroundColor(Color.GREEN);
        }

        this.pauseBtn.setVisibility(View.VISIBLE);
        this.continueBtn.setVisibility(View.GONE);
        this.endBtn.setVisibility(View.GONE);
    }

    public void pauseTimer(View view) {
        this.mpToPause.start();

        if (this.isWorkOn) {
            this.workCountDownTimer.cancel();
        } else {
            this.restCountDownTimer.cancel();
        }


        this.pauseBtn.setVisibility(View.GONE);
        this.continueBtn.setVisibility(View.VISIBLE);
        this.endBtn.setVisibility(View.VISIBLE);

        this.thisActivity.setBackgroundColor(Color.YELLOW);

        this.hasBeenPaused = true;
        this.updatePausedFields();
    }

    public void endTimer(View view) {
        this.mpToEnd.start();

    }

    private void decrementSets(View view) {
        if (this.sets > 1) {
            this.sets--;
        }

        this.updateSets();
    }

    private void decrementWork(View view) {
        if (this.workMins == 0 && this.workSecs == 1) {
            return;
        }

        if (this.workSecs >= 0) {
            this.workSecs--;

            if (this.workSecs == -1) {
                this.workSecs = 59;
                this.workMins--;

                if (this.workMins == -1) {
                    this.workSecs = 0;
                    this.workMins = 0;
                }
            }
        }

        this.updateWork();
    }

    private void decrementRest(View view) {
        if (this.restSecs >= 0) {
            this.restSecs--;

            if (this.restSecs == -1) {
                this.restSecs = 59;
                this.restMins--;

                if (this.restMins == -1) {
                    this.restSecs = 0;
                    this.restMins = 0;
                }
            }
        }

        this.updateRest();
    }

    @SuppressLint("SetTextI18n")
    private void updateSets() {
        if (this.sets > 9) {
            this.trainingSetsQuantity.setText("" + this.sets);
            return;
        }

        this.trainingSetsQuantity.setText("0" + this.sets);
    }

    @SuppressLint("SetTextI18n")
    private void updateWork() {
        if (this.workMins > 9 && this.workSecs > 9) {
            this.trainingWorkQuantity.setText("" + this.workMins + " : " + this.workSecs);
        } else if (this.workMins > 9 && this.workSecs <= 9) {
            this.trainingWorkQuantity.setText("" + this.workMins + " : 0" + this.workSecs);
        } else if (this.workMins <= 9 && this.workSecs > 9) {
            this.trainingWorkQuantity.setText("0" + this.workMins + " : " + this.workSecs);
        } else {
            this.trainingWorkQuantity.setText("0" + this.workMins + " : 0" + this.workSecs);
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateRest() {
        if (this.restMins > 9 && this.restSecs > 9) {
            this.trainingRestQuantity.setText("" + this.restMins + " : " + this.restSecs);
        } else if (this.restMins > 9 && this.restSecs <= 9) {
            this.trainingRestQuantity.setText("" + this.restMins + " : 0" + this.restSecs);
        } else if (this.restMins <= 9 && this.restSecs > 9) {
            this.trainingRestQuantity.setText("0" + this.restMins + " : " + this.restSecs);
        } else {
            this.trainingRestQuantity.setText("0" + this.restMins + " : 0" + this.restSecs);
        }
    }

    private void updatePausedFields() {
        this.pausedSets = this.sets;
        this.pausedWorkSecs = this.workSecs;
        this.pausedWorkMins = this.workMins;
        this.pausedRestSecs = this.restSecs;
        this.pausedRestMins = this.restMins;
    }

}
