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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.nio.charset.Charset;
import java.util.Random;

public class CreateFragment extends Fragment {

    MaterialButton createGrpBtn ;
    Button joinGrpBtn ;
    EditText joinGrpET ;
    ImageView grpImg ;
    Uri grpImgUri = null ;
    private FirebaseFirestore db;
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

        createGrpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            //TODO create group btn
                generatedString = genGroupCode(10);
                //initCreateGroupChat();
                createGroupChat(null);
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

    private void initCreateGroupChat() {
        if(grpImgUri != null ){
            StorageReference fileRef = storageReference.child(generatedString);
            fileRef.putFile(grpImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            createGroupChat(uri);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    createGroupChat(null);
                    Toast.makeText(getContext(), "Couldn't upload the image, try again", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            createGroupChat(null);
        }
    }

    private void createGroupChat (Uri grpUri){
        CollectionReference groups =  db.collection("groups");
        CollectionReference users =  db.collection("users");

        users.document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //Log.d("vvvx" , .toString());
                Object grps = documentSnapshot.get("groups");
                if (grps == null) {
                    Log.d("ddd" , "umpty");
                }

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
}