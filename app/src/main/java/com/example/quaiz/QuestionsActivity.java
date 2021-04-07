package com.example.quaiz;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class QuestionsActivity extends AppCompatActivity {
    private static final String FILE_NAME = "QUIZZER";
    private static final String KEY_NAME = "QUESTIONS";
    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    private TextView question, noIndicator;
    private FloatingActionButton bookmarkBtn;
    private LinearLayout optionsContainer;
    private Button shareBtn, nextBtn;
    private int cont = 0;
    private List<QuestionModel> list;
    private int postion = 0;
    private int score = 0;
    private String category;
    private int setNo;
    private Dialog loadingDialog;
    private List<QuestionModel> bookmarketsList;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private int matchedQuastionPosition;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        question = findViewById(R.id.quastion);
        noIndicator = findViewById(R.id.no_indicator);
        bookmarkBtn = findViewById(R.id.bookmarks_btn);
        optionsContainer = findViewById(R.id.options_container);
        nextBtn = findViewById(R.id.next_btn);
        shareBtn = findViewById(R.id.shara_btn);


        preferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
        gson = new Gson();
        getBookmarks();

        bookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modelMatcher()) {

                    bookmarketsList.remove(matchedQuastionPosition);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        bookmarkBtn.setImageDrawable(getDrawable(R.drawable.ic_bookmark_border));
                    }
                } else {
                    bookmarketsList.add(list.get(postion));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        bookmarkBtn.setImageDrawable(getDrawable(R.drawable.ic_baseline_bookmark_24));
                    }
                }
            }
        });


        category = getIntent().getStringExtra("category");
        setNo = getIntent().getIntExtra("setNo", 1);


        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.round_cornner));
        }
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);
        loadingDialog.show();


        list = new ArrayList<>();
        myRef.child("SETS").child(category).child("questions").orderByChild("setNo").equalTo(setNo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    list.add(snapshot1.getValue(QuestionModel.class));

                }
                if (list.size() > 0) {


                    for (int i = 0; i < 4; i++) {
                        optionsContainer.getChildAt(i).setOnClickListener(v ->
                                checkAnswer((Button) v));
                    }
                    playAnim(question, 0, list.get(postion).getQuestion());
                    nextBtn.setOnClickListener(v -> {
                        nextBtn.setEnabled(false);
                        nextBtn.setAlpha(0.7f);
                        postion++;
                        //    enableOption(true);
                        if (postion == list.size()) {
                            // soreActivity
                            Intent scoreIntent = new Intent(QuestionsActivity.this, ScoreActivity.class);
                            scoreIntent.putExtra("score", score);
                            scoreIntent.putExtra("total", list.size());
                            startActivity(scoreIntent);
                            finish();
                            return;
                        }
                        cont = 0;
                        playAnim(question, 0, list.get(postion).getQuestion());
                    });
                    shareBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String body = list.get(postion).getQuestion() + "\n" +
                                    list.get(postion).getOptionA() + "\n" +
                                    list.get(postion).getOptionB() + "\n" +
                                    list.get(postion).getOptionC() + "\n" +
                                    list.get(postion).getOptionD();

                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Quizeer challenge");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, body);
                            startActivity(Intent.createChooser(shareIntent, "Share Via"));
                        }
                    });
                } else {
                    finish();
                    Toast.makeText(QuestionsActivity.this, "no question", Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(QuestionsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
                finish();
            }
        });

        loadADS();

    }  // end class

    @Override
    protected void onPause() {
        super.onPause();
        storeBookmarks();
    }

    private void playAnim(View view, final int value, final String data) {
        view.animate().alpha(value).scaleY(value).setDuration(500).setStartDelay(100)
                .setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (value == 0 && cont < 4) {
                    String option = "";
                    if (cont == 0) {
                        option = list.get(postion).getOptionA();
                    } else if (cont == 1) {
                        option = list.get(postion).getOptionB();
                    } else if (cont == 2) {
                        option = list.get(postion).getOptionC();
                    } else if (cont == 3) {
                        option = list.get(postion).getOptionD();

                    }
                    playAnim(optionsContainer.getChildAt(cont), 0, option);
                    cont++;
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (value == 0) {
                    try {
                        ((TextView) view).setText(data);
                        noIndicator.setText(postion + 1 + "/" + list.size());

                        if (modelMatcher()) {

                            // bookmarketsList.remove(matchedQuastionPosition);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                bookmarkBtn.setImageDrawable(getDrawable(R.drawable.ic_bookmark_border));
                            }
                        } else {
                            // bookmarketsList.add(list.get(postion));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                bookmarkBtn.setImageDrawable(getDrawable(R.drawable.ic_baseline_bookmark_24));
                            }
                        }


                    } catch (ClassCastException ex) {
                        ((Button) view).setText(data);
                    }
                    view.setTag(data);
                    playAnim(view, 1, data);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void checkAnswer(Button selectOption) {
        enableOption(false);
        nextBtn.setEnabled(true);
        nextBtn.setAlpha(1);
        if (selectOption.getText().toString().equals(list.get(postion).getCorrectANS())) {
            // correct
            score++;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                selectOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
            }

        } else {
            // incorrect
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                selectOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff0000")));
            }
            Button correctoption = optionsContainer.findViewWithTag(list.get(postion).getCorrectANS());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                correctoption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
            }
        }
    }

    private void enableOption(boolean enable) {
        for (int i = 0; i < 4; i++) {
            optionsContainer.getChildAt(i).setEnabled(enable);
            /*
            if (enable) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    optionsContainer.getChildAt(i).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#989898")));
                }


            }

             */

        }
    }

    public void getBookmarks() {
        String json = preferences.getString(KEY_NAME, "");
        Type type = new TypeToken<List<QuestionModel>>() {
        }.getType();
        bookmarketsList = gson.fromJson(json, type);

        if (bookmarketsList == null) {
            bookmarketsList = new ArrayList<>();
        }
    }

    private boolean modelMatcher() {
        boolean matched = false;
        int i = 0;

        for (QuestionModel model : bookmarketsList) {
            // i++;
            if (model.getQuestion().equals(list.get(postion).getQuestion())
                    && model.getCorrectANS().equals(list.get(postion).getCorrectANS())
                    && model.getSetNo() == list.get(postion).getSetNo()) {
                matched = true;
                matchedQuastionPosition = i;

            }
            i++;
        }
        return matched;


    }

    private void storeBookmarks() {
        String json = gson.toJson(bookmarketsList);

        editor.putString(KEY_NAME, json);
        editor.commit();

    }

    private void loadADS() {
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }


}