package com.codepath.apps.simpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.helper.GlideApp;
import com.codepath.apps.simpletweets.models.User;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ComposeActivity extends AppCompatActivity {

    @BindView(R.id.etTweet)
    EditText etTweet;
    @BindView(R.id.ivImage)
    ImageView ivImage;
    @BindView(R.id.tvCount)
    TextView tvCount;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvUserName)
    TextView tvUserName;
    @BindView(R.id.btnClose)
    ImageButton btnClose;
    @BindView(R.id.btnTweet)
    Button btnTweet;

    private final int MAX_LIMIT = 140;
//    private Unbinder unbinder;
    private User mUser;
    private String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        ButterKnife.bind(this);

        mUser = (User) Parcels.unwrap(getIntent().getParcelableExtra("user"));

        btnTweet.setOnClickListener(view1 -> {

            msg = etTweet.getText().toString();
            //send data back to parent activity
            Intent data = new Intent();
            // Pass relevant data back as a result
            data.putExtra("message", msg);
            data.putExtra("code", 200); // ints work too
            // Activity finished ok, return the data
            setResult(RESULT_OK, data); // set result code and bundle data for response
            finish();

        });

        etTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null) {
                    tvCount.setText(String.valueOf(MAX_LIMIT - s.length()));
                    if (s.length() > MAX_LIMIT) {
                        btnTweet.setEnabled(false);
                    } else {
                        btnTweet.setEnabled(true);
                    }
                } else {
                    tvCount.setText("0");
                }
            }
        });

        tvName.setText(mUser.getName());
        tvUserName.setText("@" + mUser.getScreenName());
        etTweet.setText(""); //clear content
        GlideApp.with(this).load(mUser.getProfileImageUrl()).fitCenter().override(150, 150).into(ivImage);
        tvCount.setText(String.valueOf(MAX_LIMIT));   //max limit

        btnClose.setOnClickListener(view1 -> {

            //send abort msg back
            Intent data = new Intent();
            // Pass relevant data back as a result
            //data.putExtra("message", msg);
            data.putExtra("code", 400); // ints work too
            // Activity finished ok, return the data
            setResult(RESULT_OK, data); // set result code and bundle data for response
            finish();

        });

    }
}
