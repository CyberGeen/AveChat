package com.aula.im.youcef.kebiche.avechat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.nio.charset.Charset;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class CreateFragment extends Fragment {

    MaterialButton createGrpBtn ;
    Button joinGrpBtn ;
    EditText joinGrpET , nameGrpET ;
    ImageView grpImg ;
    Uri grpImgUri = null ;
    private FirebaseFirestore db;
    private FirebaseDatabase fdb;
    private FirebaseAuth mAuth;
    StorageReference storageReference;
    FirebaseUser user;
    final int galleryReqCode = 396;
    String generatedString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        fdb = FirebaseDatabase.getInstance("https://avechat-b0e8a-default-rtdb.europe-west1.firebasedatabase.app");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




        createGrpBtn = (MaterialButton) view.findViewById(R.id.createGrbBtn);
        joinGrpBtn = (Button) view.findViewById(R.id.joinGrpBtn);
        joinGrpET = (EditText) view.findViewById(R.id.joinGrpET);
        grpImg = (ImageView) view.findViewById(R.id.grpImgCreate);
        nameGrpET = (EditText) view.findViewById(R.id.nameGrpET);


        //FIXME on create :
        // todo : go the user id table and retreive chat group codes
        // todo : insert the new code
        // todo : create new chat table and add the user to it
        // todo : start the intent to the page with the code given


        //FIXME on join
        // todo : check if the code exists on the table if not show error
        // todo : add the code to the user
        // todo : goto chat table and add the new user to the list

        createGrpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String grpNameValue = nameGrpET.getText().toString();
                Boolean grpNameIsValid = validateGrpName(grpNameValue);
                if(!grpNameIsValid){
                    nameGrpET.setError("invalid name");
                    return;
                }
                //TODO start loading
                // start the create process :
                generatedString = genGroupCode(10);
                initCreateGroupChat(grpNameValue);
            }

        });

        joinGrpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String joinGrpCode = joinGrpET.getText().toString();
                if(joinGrpCode.isEmpty()){
                    joinGrpET.setError("code cant be null");
                    return;
                }
                if(joinGrpCode.length() < 9){
                    joinGrpET.setError("invalid code");
                    return;
                }

                //TODO join frp btn

                db.collection("groups").document(joinGrpCode).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    DocumentSnapshot doc = task.getResult();
                                    Object data = doc.getData();
                                    if(data == null ) {
                                        // no group found
                                        joinGrpET.setError("no such a group exist");
                                    }else {
                                        // first we update the list on the group collection field :
                                        ArrayList<Map<String, Object>> users = (ArrayList<Map<String,Object>>) doc.get("users");
                                        ArrayList<Object> newUsers = new ArrayList<Object>(Arrays.asList(users.toArray()));
                                        if(newUsers.contains(user.getUid())){
                                            Toast.makeText(getContext(), "you are already in the group", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        newUsers.add(user.getUid());
                                        List<String> strings = new ArrayList<>(newUsers.size());
                                        for (Object object : newUsers) {
                                            strings.add(Objects.toString(object, null));
                                        }
                                        db.collection("groups").document(joinGrpCode).update("users" , strings )
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Log.d("updated" , "updated the users list on group");
                                                    }
                                                });

                                        //we create new instance in realtime database
                                        DatabaseReference dbr = fdb.getReference();
                                        Date date = new Date();
                                        Messages messages = new Messages("Hello!", user.getUid() , date.getTime() );
                                        dbr.child(generatedString).push().setValue(messages);


                                        // lastly we update the user own group collection
                                        db.collection("users").document(user.getUid()).get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful()){
                                                                    DocumentSnapshot doc = task.getResult();
                                                                    ArrayList<Map<String, Object>> data = (ArrayList<Map<String,Object>>) doc.get("groups");
                                                                    if ( data == null) {
                                                                        //update the user groups
                                                                        db.collection("users").document(user.getUid()).update("groups" , Arrays.asList(joinGrpCode) )
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {
                                                                                        Log.d("updated" , "new group array");
                                                                                    }
                                                                                });
                                                                    }else {
                                                                        //push the new code to the users groups
                                                                        ArrayList<Object> newList = new ArrayList<Object>(Arrays.asList(data.toArray()));
                                                                        if(newList.contains(joinGrpCode)){
                                                                            // the user is already in the group so no need to join
                                                                            Toast.makeText(getContext(), "you are already on this group", Toast.LENGTH_SHORT).show();
                                                                            return;
                                                                        }
                                                                        newList.add(joinGrpCode);
                                                                        List<String> strings = new ArrayList<>(newList.size());
                                                                        for (Object object : newList) {
                                                                            strings.add(Objects.toString(object, null));
                                                                        }
                                                                        db.collection("users").document(user.getUid()).update("groups" , newList )
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {
                                                                                        Log.d("updated" , "updated existing group array");
                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            }
                                                        });

                                        Toast.makeText(getContext(), "maybe this one does", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "check your internet", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

        grpImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, galleryReqCode);
                } else {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, galleryReqCode);
                }
            }
        });
    }

    private void initCreateGroupChat(String grpName) {
        // this function serves as a handler for group image if the user selected one to upload it and save the image uri
        if(grpImgUri != null ){
            StorageReference fileRef = storageReference.child(generatedString);
            fileRef.putFile(grpImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            createGroupChat( grpName , uri);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    createGroupChat( grpName ,null);
                    Toast.makeText(getContext(), "Couldn't upload the image, try again", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            createGroupChat( grpName ,null);
        }
    }

    private void createGroupChat ( String grpName ,Uri grpUri){
        String participants [] = {user.getUid()};
        Map<String,Object> newGrp = new HashMap<>();
        newGrp.put("display_name" , grpName);
        if(grpUri != null){
            newGrp.put("uri" , grpUri);
        }
        newGrp.put("users" , Arrays.asList(user.getUid()));

        db.collection("groups").document(generatedString).set(newGrp)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        db.collection("users").document(user.getUid()).get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            DocumentSnapshot doc = task.getResult();
                                            ArrayList<Map<String, Object>> data = (ArrayList<Map<String,Object>>) doc.get("groups");
                                            if ( data == null) {
                                                //update the user groups
                                                db.collection("users").document(user.getUid()).update("groups" , Arrays.asList(generatedString) )
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                Log.d("updated" , "new group array");
                                                            }
                                                        });
                                            }else {
                                                //push the new code to the users groups
                                                ArrayList<Object> newList = new ArrayList<Object>(Arrays.asList(data.toArray()));
                                                newList.add(generatedString);
                                                List<String> strings = new ArrayList<>(newList.size());
                                                for (Object object : newList) {
                                                    strings.add(Objects.toString(object, null));
                                                }
                                                db.collection("users").document(user.getUid()).update("groups" , newList )
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                Log.d("updated" , "updated existing group array");
                                                            }
                                                        });
                                            }
                                        }
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage() , Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == galleryReqCode){
            if(resultCode == Activity.RESULT_OK){
                grpImgUri = data.getData();
                grpImg.setImageURI(grpImgUri);
            }
        }
    }

    static String genGroupCode(int n)
    {
        // length is bounded by 256 Character
        byte[] array = new byte[256];
        new Random().nextBytes(array);

        String randomString = new String(array, Charset.forName("UTF-8"));

        // Create a StringBuffer to store the result
        StringBuffer r = new StringBuffer();

        // Append first 20 alphanumeric characters
        // from the generated random String into the result
        for (int k = 0; k < randomString.length(); k++) {
            char ch = randomString.charAt(k);
            if (((ch >= 'a' && ch <= 'z')
                    || (ch >= 'A' && ch <= 'Z')
                    || (ch >= '0' && ch <= '9'))
                    && (n > 0)) {

                r.append(ch);
                n--;
            }
        }
        // return the resultant string
        return r.toString();
    }

    private boolean validateGrpName(String name){
        name = name.trim();
        if(!name.isEmpty() && name.length()>2 ){
            return true;
        } else {
            return false;
        }

    }
}