package com.aula.im.youcef.kebiche.avechat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HomeGrpAdapter extends BaseAdapter {

    Context context;
    List<String> ids;
    LayoutInflater layoutInflater;
    private FirebaseFirestore db;

    public HomeGrpAdapter(Context context , List<String> ids ){
        this.context = context;
        this.ids = ids;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return ids.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {

        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        db = FirebaseFirestore.getInstance();
        view = layoutInflater.inflate(R.layout.group_home_item , null );
        TextView title = (TextView) view.findViewById(R.id.homeGrpName);
        TextView members = (TextView) view.findViewById(R.id.homeGrpCount);
        ImageView grpImg = (ImageView) view.findViewById(R.id.homeGrpImg);




        db.collection("groups").document(ids.get(i)).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot doc = task.getResult();
                            //ArrayList<Map<String, Object>> data = (ArrayList<Map<String,Object>>) doc.get("groups");
                            Object data = doc.getData();
                            if(data == null){
                                return;
                            }

                            title.setText(doc.get("display_name").toString());
                            Object uri = doc.get("uri");
                            if(uri != null ){
                                Picasso.get().load(doc.get("uri").toString()).into(grpImg);
                            }
                            ArrayList<Map<String, Object>> count = (ArrayList<Map<String,Object>>) doc.get("members");
                            members.setText(Integer.toString(count.size()) + " Member" );


                        }
                    }
                });
        return view;
    }


}
