package com.aula.im.youcef.kebiche.avechat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class GroupChat extends AppCompatActivity {

    EditText userMsgET;
    ImageButton  backBtn ;
    CardView msgCardView;
    Toolbar grpToolbar;
    ImageView groupImg , sendBtn ;
    TextView grpCode ;
    RecyclerView recycler;
    String uid , grpCodeString , currentTime , grpImgUri ;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseDatabase fdb;
    FirebaseUser user;

    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        userMsgET = findViewById(R.id.insertMsgET);
        sendBtn = findViewById(R.id.sendMsgBtn);
        grpToolbar = findViewById(R.id.groupChatToolBar);
        grpCode = findViewById(R.id.grpNameChat);
        groupImg = findViewById(R.id.groupImgInChat);
        backBtn = findViewById(R.id.backbuttBtnGrpChat);
        grpCodeString = getIntent().getStringExtra("GRP_CODE");
        grpImgUri = getIntent().getStringExtra("GRP_URI");

        setSupportActionBar(grpToolbar);
        grpCode.setText(grpCodeString);
        if( !grpImgUri.isEmpty() || grpImgUri != null ) {
            Picasso.get().load(grpImgUri).into(groupImg);
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        fdb = FirebaseDatabase.getInstance();
        calendar = Calendar.getInstance();
        user = mAuth.getCurrentUser();
        uid = user.getUid();

    }
}