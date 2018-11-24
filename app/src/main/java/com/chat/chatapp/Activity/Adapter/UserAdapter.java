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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {


    // vars
    private Context mContext;
    private List<User> users;
    private boolean isChat;
    String theLastMessage = "";
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    public UserAdapter(Context mContext, List<User> users, boolean isChat) {
        this.mContext = mContext;
        this.users = users;
        this.isChat = isChat;
    } // constructor

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        holder.tv_user_name.setText(users.get(position).getUsername());

        if (isChat)
        {
            lastMessage(users.get(position).getId(),holder.last_msg);

        }
        else
        {
            holder.last_msg.setVisibility(View.GONE);

        }


        if (isChat) {
            if (users.get(position).getStatus().equals("online")) {
                holder.status_online.setVisibility(View.VISIBLE);
                holder.status_offline.setVisibility(View.GONE);
            } else {
                holder.status_online.setVisibility(View.GONE);
                holder.status_offline.setVisibility(View.VISIBLE);

            }
        } else {
            holder.status_online.setVisibility(View.GONE);
            holder.status_offline.setVisibility(View.GONE);

        }

        if (users.get(position).getImageUrl().equals("default")) {
            holder.user_profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext)
                    .load(users.get(position).getImageUrl())
                    .into(holder.user_profile_image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent message_activity = new Intent(mContext, MessageActivity.class);
                message_activity.putExtra("user_id", users.get(position).getId());
                mContext.startActivity(message_activity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        // bind views
        @BindView(R.id.user_profile_image)
        ImageView user_profile_image;

        @BindView(R.id.status_online)
        ImageView status_online;

        @BindView(R.id.status_offline)
        ImageView status_offline;

        @BindView(R.id.tv_user_name)
        TextView tv_user_name;

        @BindView(R.id.last_msg)
        TextView last_msg;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private void lastMessage(final String userid, final TextView last_message) {
        theLastMessage = "default";
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    try {
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                                chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {
                            theLastMessage = chat.getMessage();
                        }
                        switch (theLastMessage) {
                            case "default":
                                last_message.setText("No message");
                                break;

                            default:
                                last_message.setText(theLastMessage);
                                break;
                        }
                        theLastMessage = "default";
                    } catch (Exception e) {

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    } // show last message
}
