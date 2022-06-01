package com.aula.im.youcef.kebiche.avechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.dmoral.toasty.Toasty;

public class AuthHandlerMain extends AppCompatActivity {

    EditText email , password ;
    private FirebaseAuth mAuth;
    LoadingDialogue loadingDialogue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_handler_main);

        mAuth = FirebaseAuth.getInstance();

        //initialising email and password for
        email = findViewById(R.id.editTextEmailRegister);
        password = findViewById(R.id.editTextPasswordRegister);

        //initialising Loading dialogue :
        loadingDialogue = new LoadingDialogue(AuthHandlerMain.this);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
            Log.d("AUTH_APP", "There is already a user logged in");
        }
    }

    private void reload () {
        // pass the user to the main menu
        Log.d("AUTH_APP", "Reload() was called" );
        Intent home = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(home);
        finish();
    }

    //btn handlers

    public void onLoginHandler(View view){
        boolean boolMail = validateEmail();
        boolean boolPass = validatePassword();

        //quit because fields arent valid
        if( !(boolMail && boolPass) ){
            return;
        }

        String emailVal = email.getText().toString();
        String passVal = password.getText().toString();

        loadingDialogue.startLoadingAnimation();

        mAuth.signInWithEmailAndPassword(emailVal, passVal)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user'sinformation
                            Log.d("AUTH_APP", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            email.setText("");
                            password.setText("");
                            reload();
                            //updateUI(user);
                            loadingDialogue.stopLoadingAnimation();
                        } else {
                            // If sign in fails, display a message to the user.
                            loadingDialogue.stopLoadingAnimation();
                            Log.w("AUTH_APP", "signInWithEmail:failure",
                                    task.getException());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toasty.error(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG, true).show();
                        loadingDialogue.stopLoadingAnimation();
                    }
                });

    }

    public void onRegisterClickHandler(View view){
        //pass it to the register activity
        Intent register = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(register);
    }


    // fields validators

    private boolean validateEmail(){
        String emailVal = email.getText().toString();
        if(!emailVal.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailVal).matches()){
            email.setError(null);
            return true;
        } else {
            email.setError("invalid email");
            return false;
        }

    }

    private boolean validatePassword(){
        String passVal = password.getText().toString();
        if(!passVal.isEmpty() ){
            password.setError(null);
            return true;
        } else {
            password.setError("This field is required");
            return false;
        }
    }



}