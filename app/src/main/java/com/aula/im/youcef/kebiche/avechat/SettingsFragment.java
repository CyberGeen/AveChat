package com.aula.im.youcef.kebiche.avechat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class SettingsFragment extends Fragment  {

    private FirebaseAuth mAuth;
    Uri photoUri ;
    ImageView pp;
    StorageReference storageReference;
    FirebaseUser user;
    TextView username , email ;
    EditText usernameET , emailET ;
    Button updateBtn ;
    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        photoUri = user.getPhotoUrl();


        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference picRef = storageReference.child(user.getUid());
        picRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if(uri != null){
                    Picasso.get().load(uri).into(pp);
                }
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        user = mAuth.getCurrentUser();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //View view = inflater.inflate(R.layout.fragment_settings, container, false);

        return inflater.inflate(R.layout.fragment_settings, container, false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final MaterialButton logoutBtn  = (MaterialButton) view.findViewById(R.id.loggoutBtnSettings);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //FIXME: onbackClick need fix
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(getContext() , AuthHandlerMain.class);
                startActivity(i);
                getActivity().getFragmentManager().popBackStack();
            }
        });


        pp = (ImageView) view.findViewById(R.id.settingsProfilePic);
        username = (TextView) view.findViewById(R.id.usernameTVSettings);
        email = (TextView) view.findViewById(R.id.emailTVSettings);
        usernameET = (EditText) view.findViewById(R.id.updateUsernameSettings);
        emailET = (EditText) view.findViewById(R.id.updateEmailSettings);
        updateBtn = (Button) view.findViewById(R.id.updateBtnSettings);

        db = FirebaseFirestore.getInstance();

        username.setText(user.getDisplayName());
        email.setText(user.getEmail());
        usernameET.setHint(user.getDisplayName());
        emailET.setHint(user.getEmail());

        usernameET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBtn.setClickable(true);
            }
        });

        emailET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBtn.setClickable(true);
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean validUN = validateUserName();
                Boolean validE = validateEmail();
                if( !(validE&&validUN)){
                    return;
                }
                String emailVal = emailET.getText().toString();
                String usernameVal = usernameET.getText().toString();

                if(!emailVal.isEmpty()) {
                    user.updateEmail(emailVal).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            emailET.setText("");
                            email.setText(emailVal);
                            emailET.setHint(emailVal);
                            Toast.makeText(getContext(), "update successful", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                if(!usernameVal.isEmpty()) {

                    //FIXME update db too
                    UserProfileChangeRequest userUpdate = new UserProfileChangeRequest.Builder().setDisplayName(usernameVal).build();
                    user.updateProfile(userUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            usernameET.setText("");
                            username.setText(usernameVal);
                            usernameET.setHint(usernameVal);
                            Toast.makeText(getContext(), "update successful", Toast.LENGTH_SHORT).show();

                            Map<String , Object> usr =  new HashMap<>();
                            usr.put("display_name" , usernameVal);

                            CollectionReference users =  db.collection("users");
                            users.document(user.getUid()).update(usr).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d("updatx" , "updated db");
                                }
                            });
                        }
                    });

                }


            }
        });

    }

    private boolean validateUserName(){
        String userNameValue = usernameET.getText().toString();
        String userNamePattern = "^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){2,18}[a-zA-Z0-9]$";
        if(userNameValue.isEmpty() || userNameValue.matches(userNamePattern)){
            usernameET.setError(null);
            return true;
        } else {
            usernameET.setError("invalid username");
            return false;
        }
    }

    private boolean validateEmail(){
        String emailVal = emailET.getText().toString();
        if(emailVal.isEmpty() || Patterns.EMAIL_ADDRESS.matcher(emailVal).matches()){
            emailET.setError(null);
            return true;
            //FIXME: later add fetching for existing emails
        } else {
            emailET.setError("invalid email");
            return false;
        }
    }

}