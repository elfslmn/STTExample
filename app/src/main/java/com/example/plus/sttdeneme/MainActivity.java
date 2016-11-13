package com.example.plus.sttdeneme;
// http://www.truiton.com/2014/06/android-speech-recognition-without-dialog-custom-activity/

import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements RecognitionListener {

    SpeechRecognizer mSpeechRecognizer = null;
    Intent mRecognizerIntent;
    TextView tvMainResult, tvInform, tvOtherResults;
    ToggleButton toggleButton;

    TextToSpeech mTts;

    Parser mParser;

    private String LOG_TAG = "SpeechRecognition";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvMainResult = (TextView) findViewById(R.id.textView);
        tvInform = (TextView) findViewById(R.id.textView2);
        tvOtherResults = (TextView) findViewById(R.id.textView3);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);

        mRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,"en"); //for English
        mRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        mRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM); //or WEB_SEARCH
        mRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1); //?

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tvInform.setText("Listening...");
                    mSpeechRecognizer.startListening(mRecognizerIntent);
                } else {
                    mSpeechRecognizer.stopListening();
                }
            }
        });

        mTts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    mTts.setLanguage(Locale.US);
                }
            }
        });


    }
    @Override
    protected void onResume() {
        super.onResume();
        mSpeechRecognizer =SpeechRecognizer.createSpeechRecognizer(this); //Initialization
        mSpeechRecognizer.setRecognitionListener(this);
        Log.i(LOG_TAG, "resumes");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.destroy();
            Log.i(LOG_TAG, "destroy");
        }

        if(mTts != null ){
            mTts.stop();
            mTts.shutdown();
        }


    }


    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.i(LOG_TAG, "onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
        toggleButton.setChecked(false);
    }

    @Override
    public void onError(int error) {
        String errorMessage = getErrorInfo(error);
        Log.d(LOG_TAG, "FAILED " + errorMessage);
        tvInform.setText(errorMessage);
        toggleButton.setChecked(false);
    }

    @Override
    public void onResults(Bundle results) {
        tvInform.setText("Stopped.");
        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String otherText = "Other possible results:";
        String text=matches.get(0);
        matches.set(0,"");
        for (String result : matches)
            otherText += result + "\n";

        tvMainResult.setText(text);
        tvOtherResults.setText(otherText);

        mParser = new Parser(text);
        if(mParser.find("meet")) {
            setMeeting();
        }

        mTts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.i(LOG_TAG, "onPartialResults");
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.i(LOG_TAG, "onEvent");
    }

    public static String getErrorInfo(int errorNum) {
        String text;
        switch (errorNum) {
            case SpeechRecognizer.ERROR_AUDIO:
                text="Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                text="Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                text="Insufficient permissions error";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                text="Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                text="Network timeout error";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                text="No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                text="RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                text="Server error";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                text="No mSpeechRecognizer input";
                break;
            default:
                text="I did not understand, please try again.";
                break;
        }
        return text;
    }

    public void setMeeting(){
        tvInform.setText(" meet is found");

        // TODO Göstermelik
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", cal.getTimeInMillis());
        intent.putExtra("allDay", true);
        intent.putExtra("rrule", "FREQ=YEARLY");
        intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
        intent.putExtra("title", "It is a meeting set by STTDeneme");
        startActivity(intent);
    }

}
