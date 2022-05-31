package com.aula.im.youcef.kebiche.avechat;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.L;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Messages> messagesArrayList;

    final int MSG_OUT = 0 ;
    final  int MSG_IN = 1 ;

    public ChatAdapter(Context context, ArrayList<Messages> messagesArrayList) {
        this.context = context;
        this.messagesArrayList = messagesArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == MSG_OUT){
            //the user is the one sending the message
            View view = LayoutInflater.from(context).inflate(R.layout.user_message_relative,parent,false);
            return new msgOutViewHolder(view);
        }else{
            //the user is receiving the message
            View view = LayoutInflater.from(context).inflate(R.layout.other_message_relative,parent,false);
            return new msgInViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Messages message = messagesArrayList.get(position);
        if(holder.getClass() == msgInViewHolder.class){
            msgInViewHolder inViewHolder = (msgInViewHolder) holder;
            inViewHolder.otherMsgTV.setText(message.getMessage());
            inViewHolder.otherUNTV.setText(message.getUsername());
            //inViewHolder.uri
            if(message.getUri() != null) {
                Picasso.get().load(message.getUri()).into(inViewHolder.profileImg);
            }
        }else {
            msgOutViewHolder outViewHolder = (msgOutViewHolder) holder;
            outViewHolder.msgTV.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {

        Messages message =  messagesArrayList.get(position);
        // identifying weather we are receiving or sending a message
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(message.getUid())) return MSG_OUT ;
        else return MSG_IN;
    }



    class msgOutViewHolder extends RecyclerView.ViewHolder {

        TextView msgTV;

        public msgOutViewHolder(@NonNull View itemView) {
            super(itemView);

            msgTV = itemView.findViewById(R.id.userMessageTV);

        }
    }

    class msgInViewHolder extends RecyclerView.ViewHolder {

        TextView otherMsgTV , otherUNTV ;
        ImageView profileImg;

        public msgInViewHolder(@NonNull View itemView) {
            super(itemView);

            otherMsgTV = itemView.findViewById(R.id.otherMessageTV);
            otherUNTV = itemView.findViewById(R.id.otherMessageUsername);
            profileImg = itemView.findViewById(R.id.chatProfilePic);

        }
    }


}
