package com.aula.im.youcef.kebiche.avechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aula.im.youcef.kebiche.avechat.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    //NOTE for the teacher : the main activity or the start , starts from splash screen and goes to AuthHandlerMain
    private FirebaseAuth mAuth;
    Uri photoUri ;
    String name ;
    ImageView pp;
    StorageReference storageReference;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigationView.setSelectedItemId(R.id.bottomNavHome);

        replaceFregment(new HomeFragment());
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.bottomNavCreate:
                    replaceFregment(new CreateFragment());
                    break;
                case R.id.bottomNavHome:
                    replaceFregment(new HomeFragment());
                    break;
                case R.id.bottomNavSettings:
                    replaceFregment(new SettingsFragment());
                    break;
            }
            return true;
        });


/*
        mAuth = FirebaseAuth.getInstance();

        pp = findViewById(R.id.testProfilePic);


        FirebaseUser user = mAuth.getCurrentUser();
        photoUri = user.getPhotoUrl();
        name = user.getDisplayName();



        storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference picRef = storageReference.child(user.getUid());
        picRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if(uri != null){
                    Picasso.get().load(uri).into(pp);
                }
            }
        }); */
    }

    @Override
    protected void onStart() {
        super.onStart();
        //FirebaseUser currentUser = mAuth.getCurrentUser();
        //Log.d("ccu" , currentUser.toString());
        Intent i = getIntent();
        String requestCode = i.getStringExtra("END_ACTIVITY");
        if( requestCode != null && requestCode == "643" ){
            //loggout and finish activity
            FirebaseAuth.getInstance().signOut();
            Intent authIntent = new Intent(this , AuthHandlerMain.class);
            startActivity(authIntent);
            finish();
        }
    }

    private void replaceFregment (Fragment fragment){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.main_frame_layout,fragment);
        transaction.commit();
    }



}