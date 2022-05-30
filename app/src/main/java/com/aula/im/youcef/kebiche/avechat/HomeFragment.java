package com.aula.im.youcef.kebiche.avechat;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.Document;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class HomeFragment extends Fragment {

    ListView mainListView;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    FirebaseUser user;
    ConstraintLayout notFoundView ;
    HomeGrpAdapter homeGrpAdapter;

    List<String> ids = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    @Override
    public void onStart() {
        super.onStart();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Intent i = new Intent(getContext(),GroupChat.class);
        //i.putExtra("GRP_CODE","GRP_CODE");
        //i.putExtra("GRP_URI" , "");
        //startActivity(i);

        //FIXME show groups :
        // todo : fetch data from user
        // todo : check length and display
        //CollectionReference ref = db.collection("users");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        DocumentReference usr = db.collection("users").document(user.getUid().toString());
        notFoundView = view.findViewById(R.id.homeNoChatFound);


        /*
        usr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("vvvx" , documentSnapshot.getData().toString() );
            }
        });

         */





        // FIXME ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::.
        db.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                ArrayList<Map<String, Object>> data = (ArrayList<Map<String,Object>>) doc.get("groups");

                //Object [] idk = data.toArray()  ;
                //List<Object> newData = new ArrayList<Object>(data.toArray()));
                //Object [] idk2 = new Object[data.size()+1];
                //idk2[data.size()+1] = "popo";



                //FIXME also check the array length
                if ( data == null) {
                    Log.d("vvvvx", "no groups"  );
                    notFoundView.setVisibility(View.VISIBLE);

                }else {
                    //data.add(<data.size() , "hello">);
                    //Map<String , Object> idk = new HashMap<>();

                    //data.set(data.size() , "popo");

                    /*
                    //TODO final code :
                    ArrayList<Object> temp = new ArrayList<Object>(Arrays.asList(data.toArray()));
                    temp.add("idkMNASNDMA");
                    List<String> strings = new ArrayList<>(temp.size());
                    for (Object object : temp) {
                        strings.add(Objects.toString(object, null));
                    }
                    db.collection("users").document(user.getUid()).update("groups" , temp )
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("updated" , "updated");
                                }
                            });
                            Log.d("vvvvx", " : " + strings.size() );
                     */

                    // turning the group ids to List<String> and passing it to the adapter
                    ArrayList<Object> temp = new ArrayList<Object>(Arrays.asList(data.toArray()));
                    //fixme ids here
                    for (Object object : temp) {
                        ids.add(Objects.toString(object, null));
                    }

                    //sometimes the code runs before the activity is fully created
                    //it tries to get views it cant reach
                    //this simply fix it :
                    if(getActivity()==null) return;


                    mainListView = (ListView) view.findViewById(R.id.listViewHome);
                    homeGrpAdapter = new HomeGrpAdapter(getContext(), ids );
                    mainListView.setAdapter(homeGrpAdapter);
                    registerForContextMenu(mainListView);
                    mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Log.d("itemName" , "id :: " + ids.get(i)  );
                            // ponce a user click we get the info and pass it to the chat activity
                            db.collection("groups").document(ids.get(i)).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()){
                                                DocumentSnapshot doc = task.getResult();
                                                Object data = doc.getData();
                                                if(data == null){
                                                    return;
                                                }
                                                Intent grpIntent = new Intent(getContext(),GroupChat.class);
                                                grpIntent.putExtra("GRP_CODE",ids.get(i));
                                                Object uriValid = doc.get("uri");
                                                if(uriValid != null){
                                                    grpIntent.putExtra("GRP_URI",doc.get("uri").toString());
                                                }
                                                grpIntent.putExtra("GRP_NAME",doc.get("display_name").toString());
                                                startActivity(grpIntent);
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), "Connection Error", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });

                }

                if(task.isSuccessful()) Log.d("vvvvx","succ");
                    else  Log.d("vvvvx","failer");
            }
        });



    }


    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getActivity().getMenuInflater().inflate(R.menu.group_list_menu , menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Log.d("position", "removing item pos=" + ids.get(info.position));
        Log.d("position" ,  "" + item.getActionView() );

        switch (item.getItemId()){
            case R.id.getCodeMenuList:
                //custom class that takes care of showing the code and sharing it
                DialogueClass codeDialogue = new DialogueClass(ids.get(info.position));
                codeDialogue.show(getActivity().getSupportFragmentManager() , "DIAG_TAG");
                return true;
            case R.id.quitGroupMenuList:
                //to eliminate duplicate code
                QuitGroupHandler quitGroupHandler = new QuitGroupHandler(ids.get(info.position) , getContext()  );
                ids.remove(ids.get(info.position));
                homeGrpAdapter.notifyDataSetChanged();

                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }
}