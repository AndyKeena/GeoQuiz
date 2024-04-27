package uk.ac.wlv.geoquiz;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.ContentUris;
import android.util.Log;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.ContactsContract;
import android.Manifest;
import android.app.Activity;

public class MainActivity extends AppCompatActivity {
    private static final String KEY_INDEX = "index";
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_CHEAT = 0;
    private static final int REQUEST_CONTACT = 1 ;
    private Button mCheatButton;
    private Button mTrueButton;
        private Button mFalseButton;
        private TextView mQuestionTextView;
        private Button share_button;
        private int mCurrentIndex = 0;
        private ImageButton mNextButton;
        private ImageButton mPreviousButton;
        private static final int READ_CONTACTS_PERMISSION_REQUEST_CODE = 0;
        private int attemptedQuestions = 0;
        private int correctAnswers = 0;
        private Button contact_button;
        private TextView contact_name;
        private TextView contact_number;
        private TextView success_button;
        private boolean mIsCheater;
        public boolean isAnswerShown = false;
    private static final String KEY_IS_ANSWER_SHOWN = "is_answer_shown"; // Define a key for saving isAnswerShown state

    private void updateQuestion(){
            int question = mQuestionBank[mCurrentIndex].getTextResId();
            mQuestionTextView.setText(question);
        }
        private Question[] mQuestionBank = new Question[]{
                new Question(R.string.question_oceans, true),
                new Question(R.string.question_mideast, false),
                new Question(R.string.question_africa, false),
                new Question(R.string.question_americas, true),
                new Question(R.string.question_asia, true),
                new Question(R.string.question_oceans1, true),
                new Question(R.string.question_mideast1, false),
                new Question(R.string.question_africa1, false),
                new Question(R.string.question_americas1, false),
                new Question(R.string.question_asia1, true),
        };

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId = 0;
        if (isAnswerShown) {
            Toast.makeText(this, "You have cheated", Toast.LENGTH_SHORT).show();
            if (userPressedTrue == answerIsTrue) {
                correctAnswers++;
            }
        }
        else {
            if (userPressedTrue == answerIsTrue) {
                correctAnswers++;
                messageResId = R.string.correct_toast;
            } else {
                messageResId = R.string.incorrect_toast;
            }
            Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
        }
        attemptedQuestions++;
        displaySuccessRate();
    }

    private void displaySuccessRate() {
        double successRate = 0;
        if (attemptedQuestions > 0) {
//            int validAnswers = attemptedQuestions - (mIsCheater ? 1 : 0); // Exclude cheated answers
            successRate = ((double) correctAnswers / attemptedQuestions) * 100;
        }
        String successRateText = "Success Rate is  " + String.format("%.2f", successRate) + "%";
        success_button.setText(successRateText);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null){
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            isAnswerShown = savedInstanceState.getBoolean(KEY_IS_ANSWER_SHOWN, false);
            attemptedQuestions = savedInstanceState.getInt("attemptedQuestions", 0);
            correctAnswers = savedInstanceState.getInt("correctAnswers", 0);
        }
        mCheatButton = (Button)findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent i = CheatActivity.newIntent(MainActivity.this, answerIsTrue);
                startActivityForResult(i, REQUEST_CODE_CHEAT);
            }
        });
        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        updateQuestion();
        mTrueButton = (Button) findViewById(R.id.true_button);
        mFalseButton = (Button) findViewById(R.id.false_button);
        share_button = (Button) findViewById(R.id.share_button);
        success_button = (TextView) findViewById(R.id.success_button);

        share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hi");

                startActivity(sendIntent);
            }

        });
        mTrueButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                mIsCheater = false;
                updateQuestion();
            }
        });

        mPreviousButton = (ImageButton) findViewById(R.id.previous_button);
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

        contact_name = findViewById(R.id.contact_name);
        contact_number = findViewById(R.id.contact_number);
        contact_button = findViewById(R.id.contact_button);

        contact_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        requestContactsPermission();
        updateButton(hasContactPermission());
        displaySuccessRate();
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState){
        super.onSaveInstanceState(saveInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        saveInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        saveInstanceState.putBoolean(KEY_IS_ANSWER_SHOWN, isAnswerShown);
        saveInstanceState.putInt("attemptedQuestions", attemptedQuestions);
        saveInstanceState.putInt("correctAnswers", correctAnswers);
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "onStart() called");
    }
    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "onResume() called");
    }
    @Override
    public void onStop(){
        super.onStop();
        Log.d(TAG, "onStop() called");
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData(); // Get the URI
            String[] queryFields = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
            String[] selectionArgs = new String[]{String.valueOf(ContentUris.parseId(contactUri))};
            String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";

            Cursor cursor = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, queryFields, selection, selectionArgs, null);
            try {
                if (cursor.getCount() == 0) return;
                cursor.moveToFirst();

                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                @SuppressLint("Range") String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                contact_name.setText(name);
                contact_number.setText(number);

            } finally {
                cursor.close();
            }
        }
        if (requestCode == REQUEST_CODE_CHEAT && data != null) {
            isAnswerShown = data.getBooleanExtra(CheatActivity.EXTRA_ANSWER_SHOWN, false); // Retrieve isAnswerShown value
            if (isAnswerShown) {
                // Check if the user cheated but selected the correct answer
                mIsCheater = true;
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_CONTACTS_PERMISSION_REQUEST_CODE) {
            // Check if the permission has been granted
            if (requestCode == READ_CONTACTS_PERMISSION_REQUEST_CODE && grantResults.length > 0)
            {
                updateButton(grantResults[0] == PackageManager.PERMISSION_GRANTED);
            }
        }
    }

    private boolean hasContactPermission(){
        return ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }
        private void requestContactsPermission() {
            if (!hasContactPermission()) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_PERMISSION_REQUEST_CODE);
            }
        }

        public void updateButton(boolean enable) {
            contact_button.setEnabled(enable);
            contact_name.setEnabled(enable);
            contact_number.setEnabled(enable);
        }

}