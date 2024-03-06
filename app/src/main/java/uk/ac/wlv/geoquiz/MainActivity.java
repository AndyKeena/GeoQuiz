package uk.ac.wlv.geoquiz;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.ContactsContract;
import android.Manifest;




public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CONTACT = 1 ;
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

//        private void checkAnswer(boolean userPressedTrue){
//            boolean answerIsTrue = mQuestionBank [mCurrentIndex].isAnswerTrue();
//            int messageResId = 0;
//            if (userPressedTrue == answerIsTrue){
//                messageResId = R.string.correct_toast;
//            }
//            else {
//                messageResId = R.string.incorrect_toast;
//            }
//            Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
//        }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        if (userPressedTrue == answerIsTrue) {
            correctAnswers++;
            Toast.makeText(this, R.string.correct_toast, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.incorrect_toast, Toast.LENGTH_SHORT).show();
        }
        attemptedQuestions++;
        displaySuccessRate();
    }

    private void displaySuccessRate() {
        double successRate = 0;
        if (attemptedQuestions > 0) {
            successRate = ((double) correctAnswers / attemptedQuestions) * 100;
        }
      //  Toast.makeText(this, "Success Rate: " + String.format("%.2f", successRate) + "%", Toast.LENGTH_SHORT).show();
        String successRateText = "Success Rate is  " + String.format("%.2f", successRate) + "%";
        success_button.setText(successRateText);

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_main);
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
                sendIntent.putExtra(Intent.EXTRA_TEXT, "kkkkkkkkk");

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
                updateQuestion();
            }
        });

        mPreviousButton = (ImageButton) findViewById(R.id.previous_button);
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
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
        if (requestCode == REQUEST_CONTACT && data != null)  {
            Uri contactUri = data.getData();            // Get the URI and query the content provider for the display the name
            String[] queryFields = new String[]{ContactsContract.Contacts.DISPLAY_NAME};
            //String[] queryFields1 = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};

            Cursor cursor = this.getContentResolver().query(contactUri, queryFields, null, null, null);
            try
            {
                if (cursor.getCount() == 0) return;
                cursor.moveToFirst();

                String name = cursor.getString(0);
                contact_name.setText(name);

            }
            finally
            {
                cursor.close();
            }
        }
    }
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode != Activity.RESULT_OK || data == null) return;
//        if (requestCode == REQUEST_CONTACT) {
//            // Get the URI and query the content provider for the phone number
//            Uri contactUri = data.getData();
//            String[] queryFields = new String[]{ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
//
//            Cursor cursor = null;
//            try {
//                cursor = this.getContentResolver().query(contactUri, queryFields, null, null, null);
//                if (cursor != null && cursor.moveToFirst()) {
//                    int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
//                    int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//                    if (nameIndex != -1 && numberIndex != -1) { // Check if columns exist
//                        String name = cursor.getString(nameIndex);
//                        String phoneNumber = cursor.getString(numberIndex);
//                        String contactInfo = name + " - " + phoneNumber;
//                        contact_name.setText(contactInfo);
//                    }
//                }
//            } finally {
//                if (cursor != null) {
//                    cursor.close();
//                }
//            }
//        }
//    }
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
    private void requestContactsPermission()
    {
        if (!hasContactPermission())
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_PERMISSION_REQUEST_CODE);
        }
    }

    public void updateButton(boolean enable)
    {
        contact_button.setEnabled(enable);
        contact_name.setEnabled(enable);
        contact_number.setEnabled(enable);
    }
}