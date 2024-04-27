package uk.ac.wlv.geoquiz;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CheatActivity extends AppCompatActivity {
        private static final String EXTRA_ANSWER_IS_TRUE = "uk.ac.wlv.geo quiz.answer_is_true";
        public static final String EXTRA_ANSWER_SHOWN = "uk.ac.wlv.geo quiz.answer_shown";
        private boolean mAnswerIsTrue;
        private TextView mAnswerTextView;
        private Button mShowAnswer;
        private boolean isAnswerShown = false;

    public static Intent newIntent(Context packageContext, boolean answerIsTrue) {
            Intent i = new Intent(packageContext, CheatActivity.class);
            i.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
            return i;
    }

    private void setAnswerShownResult(boolean isAnswerShown) {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        setResult(Activity.RESULT_OK, data);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);
        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
        mAnswerTextView = (TextView) findViewById(R.id.answer_text_view);
        mShowAnswer = (Button) findViewById(R.id.show_answer_button);
        mShowAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAnswerIsTrue){
                    mAnswerTextView.setText(R.string.true_button);
                }
                else {
                    mAnswerTextView.setText(R.string.false_button);
                }
//                setAnswerShownResult(true);
                isAnswerShown = true;
                setAnswerShownResult(isAnswerShown);

            }
        });

    }

//    @Override
//    public void onBackPressed() {
//        // Override onBackPressed to handle when the user presses the back button
//
//        // Set the result indicating that the user has not cheated
//        setAnswerShownResult(false);
//
//        // Show a toast message indicating that the user has cheated
//        Toast.makeText(this, "Cheated!", Toast.LENGTH_SHORT).show();
//
//        // Call super.onBackPressed() to allow the default back button behavior
//        super.onBackPressed();
//    }


}
