package com.aula.im.youcef.kebiche.avechat;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class QuitGroupHandler extends Application {
    String code;
    FirebaseUser user;
    FirebaseFirestore db;
    Context context;

    public QuitGroupHandler(String code , Context context) {
        this.code = code;
        this.context = context;
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.db = FirebaseFirestore.getInstance();

        //todo update the user and send a toasty msg
        db.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();
                        ArrayList<Map<String, Object>> data = (ArrayList<Map<String,Object>>) doc.get("groups");
                        ArrayList<Object> newList = new ArrayList<Object>(Arrays.asList(data.toArray()));

                        newList.remove(code);

                        //updating with the new array:
                        db.collection("users").document(user.getUid()).update("groups" , newList )
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toasty.warning(context, "Group Quited.", Toast.LENGTH_SHORT, true).show();
                                    }
                                });

                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toasty.error(context, "Network Error.", Toast.LENGTH_SHORT, true).show();
            }
        });


        //todo updated the group members
        db.collection("groups").document(code).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();

                    ArrayList<Map<String, Object>> data = (ArrayList<Map<String,Object>>) doc.get("members");
                    ArrayList<Object> newList = new ArrayList<Object>(Arrays.asList(data.toArray()));

                    newList.remove(user.getUid());

                    //updating with the new array:

                    db.collection("groups").document(code).update("members" , newList )
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    return;
                                }
                            });


                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toasty.error(context, "Network Error.", Toast.LENGTH_SHORT, true).show();
            }
        });

    }


}
