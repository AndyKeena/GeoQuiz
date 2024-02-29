package uk.ac.wlv.geoquiz;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.ContactsContract;



public class MainActivity extends AppCompatActivity {

        private Button mTrueButton;
        private Button mFalseButton;
        private TextView mQuestionTextView;
        private Button button1;
        private int mCurrentIndex = 0;
        private ImageButton mNextButton;
        private ImageButton mPreviousButton;
        private static final int PICK_CONTACT_REQUEST = 1;
        private int attemptedQuestions = 0;
        private int correctAnswers = 0;

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
        Toast.makeText(this, "Success Rate: " + String.format("%.2f", successRate) + "%", Toast.LENGTH_SHORT).show();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        updateQuestion();
        mTrueButton = (Button) findViewById(R.id.true_button);
        mFalseButton = (Button) findViewById(R.id.false_button);
        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT,"kkkkkkkkk");

                startActivity(sendIntent);
            }

        });
        mTrueButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                checkAnswer(true);            }
        });
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);            }
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
    }
}