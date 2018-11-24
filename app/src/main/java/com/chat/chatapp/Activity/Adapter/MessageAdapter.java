package com.chat.chatapp.Activity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chat.chatapp.Activity.Activity.MessageActivity;
import com.chat.chatapp.Activity.Model.Chat;
import com.chat.chatapp.Activity.Model.User;
import com.chat.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {


    // vars
    private Context mContext;
    private List<Chat> chats;
    private String imageUrl;
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_Right = 1;
    FirebaseUser firebaseUser;


    public MessageAdapter(Context mContext, List<Chat> chats, String imageUrl) {
        this.mContext = mContext;
        this.chats = chats;
        this.imageUrl = imageUrl;
    } // constructor

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MSG_TYPE_Right) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MyViewHolder(view);

        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MyViewHolder(view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        Chat chat = chats.get(position);
        holder.tv_show_message.setText(chat.getMessage());

        if (position == chats.size() - 1) {
            if (chat.getIsSeen()) {
                holder.last_seen.setText("Seen");
                holder.last_seen.setTextColor(mContext.getResources().getColor(R.color.green));
            } else {
                holder.last_seen.setText("Delivered");
                holder.last_seen.setTextColor(mContext.getResources().getColor(R.color.black));

            }
        } else {
            holder.last_seen.setVisibility(View.GONE);

        }

        if (imageUrl.equals("default")) {
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext)
                    .load(imageUrl)
                    .into(holder.profile_image);

        }

    }

    @Override
    public int getItemCount() {
        return chats.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        // bind views
        @BindView(R.id.profile_image)
        ImageView profile_image;

        @BindView(R.id.tv_show_message)
        TextView tv_show_message;

        @BindView(R.id.last_seen)
        TextView last_seen;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chats.get(position).getSender().equals(firebaseUser.getUid())) {
            return MSG_TYPE_Right;
        } else {
            return MSG_TYPE_LEFT;

        }
    }
}
