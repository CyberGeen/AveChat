package com.aula.im.youcef.kebiche.avechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GroupChat extends AppCompatActivity {

    EditText userMsgET;
    ImageButton  backBtn ;
    CardView msgCardView;
    Toolbar grpToolbar;
    ImageView groupImg , sendBtn ;
    TextView grpName , msgET ;
    RecyclerView recycler;
    String uid , grpCodeString , grpImgUri , grpNameString , msgVal ;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseDatabase fdb;
    FirebaseUser user;

    Calendar calendar;

    ChatAdapter chatAdapter;
    ArrayList<Messages> messagesArrayList;
    Map<String,String> uidToUsername = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        userMsgET = findViewById(R.id.insertMsgET);
        sendBtn = findViewById(R.id.sendMsgBtn);
        grpToolbar = findViewById(R.id.groupChatToolBar);
        grpName = findViewById(R.id.grpNameChat);
        groupImg = findViewById(R.id.groupImgInChat);
        backBtn = findViewById(R.id.backbuttBtnGrpChat);
        msgET = findViewById(R.id.insertMsgET);
        recycler = findViewById(R.id.groupChatRecycler);

        grpCodeString = getIntent().getStringExtra("GRP_CODE");
        grpImgUri = getIntent().getStringExtra("GRP_URI");
        grpNameString = getIntent().getStringExtra("GRP_NAME");

        messagesArrayList = new ArrayList<>();


        //to prevent multiple calls to the backend we use a one time call :
        getUidUsername();

        setSupportActionBar(grpToolbar);
        grpName.setText(grpNameString);
        if( grpImgUri != null ) {
            Picasso.get().load(grpImgUri).into(groupImg);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recycler.setLayoutManager(linearLayoutManager);

        chatAdapter = new ChatAdapter(GroupChat.this , messagesArrayList);
        recycler.setAdapter(chatAdapter);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msgVal = msgET.getText().toString();

                // if there's no msg we quit the process
                if(msgVal.isEmpty()) return;

                //get the current time for sorting
                //currentTime = SimpleDateFormat.getDateTimeInstance().format(calendar.getTime());
                Date date=new Date();
                //create the message object for real time db
                Messages message = new Messages(msgVal.trim() , user.getUid() , date.getTime() );
                fdb.getReference().child(grpCodeString).push().setValue(message)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(GroupChat.this, "Internet problem...", Toast.LENGTH_SHORT).show();
                            }
                        });

                //clearing data after sending
                msgET.setText(null);
                msgVal = null ;

            }
        });
        Log.d("kkdkdkdkd" , grpCodeString );
        fdb = FirebaseDatabase.getInstance("https://avechat-b0e8a-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference databaseReference = fdb.getReference(grpCodeString);


        //todo delete this :
        //chatAdapter = new ChatAdapter(GroupChat.this , messagesArrayList);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for (DataSnapshot data : snapshot.getChildren()){
                    Log.d("LSJSKJF", ": " + data.getValue(Messages.class) );
                    Map<String, Object> td = (HashMap<String, Object>) data.getValue();
                    //Messages messages = new Messages(td.get("message").toString() , td.get("uid").toString() , Long.parseLong(td.get("timeStamp").toString()) );
                    Messages messages = data.getValue(Messages.class);
                    if(messages.getUid() == user.getUid() ){
                        //no need to insert the username
                        messagesArrayList.add(messages);
                    }else{
                        //we check the uid-username list that's created locally
                        //if the username is in there and the list isnt umpty we just get it from there
                        //else we re-fetch the list (solving the problem of a user joining in the middle of a conversation)
                        if(uidToUsername.get(messages.getUid()) == null){
                            getUidUsername();
                        }
                        messages.setUsername(uidToUsername.get(messages.getUid()));
                        messagesArrayList.add(messages);

                    }
                    //messages.setMessage(td.get("message").toString());
                    //messages.setMessage(td.get("uid").toString());
                    //messages.setTimeStamp(Long.parseLong(td.get("timeStamp").toString()));


                }
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getUidUsername() {
        db = FirebaseFirestore.getInstance();
        db.collection("groups").document(grpCodeString).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot doc = task.getResult();
                            ArrayList<Map<String, Object>> data = (ArrayList<Map<String,Object>>) doc.get("users");
                            for(Object object : data ){
                                db.collection("users").document(object.toString()).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful()){
                                                    DocumentSnapshot doc = task.getResult();
                                                    if(doc == null){
                                                        Toast.makeText(GroupChat.this, "Problem getting a user", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }
                                                    Object data = doc.get("display_name");
                                                    uidToUsername.put(object.toString() , data.toString() );
                                                    //Log.d("wild"  , " : " + uidToUsername );
                                                }
                                            }
                                        });
                            }
                            chatAdapter.notifyDataSetChanged();
                        }
                    }
                });



    }


    @Override
    protected void onStart() {
        super.onStart();
        chatAdapter.notifyDataSetChanged();
        mAuth = FirebaseAuth.getInstance();
        fdb = FirebaseDatabase.getInstance("https://avechat-b0e8a-default-rtdb.europe-west1.firebasedatabase.app");
        calendar = Calendar.getInstance();
        user = mAuth.getCurrentUser();
        uid = user.getUid();
    }

    @Override
    protected void onStop() {
        super.onStop();
        chatAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.group_list_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.getCodeMenuList:
                DialogueClass codeDialogue = new DialogueClass(grpCodeString );
                codeDialogue.show(getSupportFragmentManager() , "DIAG_TAG");
                return true;
            case R.id.quitGroupMenuList:
                //to eliminate duplicate code
                QuitGroupHandler quitGroupHandler = new QuitGroupHandler(grpCodeString , getApplicationContext() );
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}