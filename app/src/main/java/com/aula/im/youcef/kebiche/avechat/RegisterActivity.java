package com.aula.im.youcef.kebiche.avechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText email , password , username ;
    private FirebaseAuth mAuth;
    final int galleryReqCode = 420;
    Uri profilePicUri;
    ImageView profilePic;
    StorageReference storageReference;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // set up views for validators
        email = findViewById(R.id.editTextEmailRegister);
        password = findViewById(R.id.editTextPasswordRegister);
        username = findViewById(R.id.editTextUserRegister);

        profilePic = findViewById(R.id.testProfilePic);

        // firebase init
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
    }


    // btn handlers

    public void onRegisterClick (View view) {
        boolean emailValid = validateEmail();
        boolean passValid = validatePassword();
        boolean usernameValid = validateUserName();

        //quit because fields arent valid
        if(!(emailValid && passValid && usernameValid)){
            return;
        }
        else registerHandler();
    }

    public void onProfileClick (View view){
        if (ActivityCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, galleryReqCode);
        } else {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, galleryReqCode);
        }
    }

    public void onBackClick (View view){
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == galleryReqCode){

            if(resultCode == Activity.RESULT_OK){
                profilePicUri = data.getData();
                profilePic.setImageURI(profilePicUri);
            }
        }
    }

    // Auth handlers

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }

    private void registerHandler(){

        String emailVal = email.getText().toString();
        String passVal = password.getText().toString();
        String usernameVal = username.getText().toString();

        mAuth.createUserWithEmailAndPassword(emailVal, passVal)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d("AUTH_APP", "createUserWithEmail:success");
                            //Toast.makeText(newUserActivity.this, "User account added.", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();

                            //boolean emailVerified = false ;
                            String chats[] = {} ;

                            Map<String , Object> usr =  new HashMap<>();
                            usr.put("verified" , false );
                            usr.put("role" , "user");
                            usr.put("display_name" , usernameVal);

                            // create a node in db
                            CollectionReference users =  db.collection("users");
                            users.document(user.getUid()).set(usr).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("vvx" , "user node created") ;
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("vvx" , e.toString()) ;
                                }
                            });



                            // upload picture if exist
                            if(profilePicUri != null){
                                    StorageReference fileRef = storageReference.child(user.getUid());
                                    fileRef.putFile(profilePicUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                            // here we retrieve the uri of the pic from the db and then we set it for the profile

                                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    //update the profile ;
                                                    // set the new values
                                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                            .setDisplayName(usernameVal)
                                                            .setPhotoUri(uri)
                                                            .build();

                                                    user.updateProfile(profileUpdates)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Log.d("vvx", "User profile updated.");
                                                                        reload();
                                                                    }
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    reload();
                                                                }
                                                            });
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    reload();
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(RegisterActivity.this, "Could'n upload the image", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            }else{
                                // set the new values without the uri
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(usernameVal)
                                        .build();

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("vvx", "User profile updated.");
                                                    reload();
                                                }
                                            }
                                        });
                            }




                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("AUTH_APP", "createUserWithEmail:failure",task.getException());
                            //Toast.makeText(newUserActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }


    private void reload() {
        // pass the user to the main menu
        Intent home = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(home);
        finish();
    }

    private void updateUI(FirebaseUser user ){
        // either pass the action to reload and create a new user in DB or show an error message
        if(user != null){
            reload();
        }
    }

    // permission handlers

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case galleryReqCode:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, galleryReqCode);
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    // fields validators

    private boolean validateEmail(){
        String emailVal = email.getText().toString();
        if(!emailVal.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailVal).matches()){
            email.setError(null);
            return true;
            //FIXME: later add fetching for existing emails
        } else {
            email.setError("invalid email");
            return false;
        }

    }

    private boolean validatePassword(){
        String passVal = password.getText().toString();
        String passPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{6,20}$";
        if(!passVal.isEmpty() && passVal.matches(passPattern) ){
            password.setError(null);
            return true;
        } else {
            password.setError("weak password");
            return false;
        }
    }

    private boolean validateUserName(){
        String userNameValue = username.getText().toString();
        String userNamePattern = "^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){2,18}[a-zA-Z0-9]$";
        if(!userNameValue.isEmpty() && userNameValue.matches(userNamePattern)){
            username.setError(null);
            return true;
        } else {
            username.setError("invalid username");
            return false;
        }

    }
}