package com.chat.chatapp.Activity.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.chat.chatapp.Activity.Adapter.MessageAdapter;
import com.chat.chatapp.Activity.Api.ApiService;
import com.chat.chatapp.Activity.Model.Chat;
import com.chat.chatapp.Activity.Model.User;
import com.chat.chatapp.Activity.Notification.Client;
import com.chat.chatapp.Activity.Notification.Data;
import com.chat.chatapp.Activity.Notification.MyResponse;
import com.chat.chatapp.Activity.Notification.Sender;
import com.chat.chatapp.Activity.Notification.Token;
import com.chat.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {

    // bind views
    @BindView(R.id.profile_image)
    CircleImageView profile_image;

    @BindView(R.id.tv_user_name)
    TextView tv_user_name;

    @BindView(R.id.tool_bar)
    android.support.v7.widget.Toolbar tool_bar;

    @BindView(R.id.btn_send)
    ImageButton btn_send;

    @BindView(R.id.et_send)
    EditText et_send;

    @BindView(R.id.chat_recycler_view)
    RecyclerView chat_recycler_view;

    // vars
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    Intent intent;
    String user_id = "", message;
    MessageAdapter messageAdapter;
    List<Chat> chatList;
    LinearLayoutManager layoutManager;
    ValueEventListener valueEventListener;
    ApiService apiService;
    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);
        initToolBar();
        getData();
        initFirebase();
        initRecyclerView();
    }

    private void seenMessage(final String userid) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    try {
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("isSeen", true);
                            snapshot.getRef().updateChildren(hashMap);
                        }
                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    } // check seen messages

    private void initRecyclerView() {
        chat_recycler_view.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        chat_recycler_view.setLayoutManager(layoutManager);
    } // init recycler view

    private void initFirebase() {
        apiService = Client.getClient("https://fcm.googleapis.com/").create(ApiService.class);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user_id);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                try {
                    tv_user_name.setText(user.getUsername());
                    if (user.getImageUrl().equals("default")) {
                        profile_image.setImageResource(R.mipmap.ic_launcher);
                    } else {
                        Glide.with(getApplicationContext())
                                .load(user.getImageUrl())
                                .into(profile_image);
                    }
                    readMessage(firebaseUser.getUid(), user_id, user.getImageUrl());

                } catch (Exception e) {

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seenMessage(user_id);
    } // initialize firebase

    private void getData() {
        intent = getIntent();
        if (intent != null)
            user_id = intent.getStringExtra("user_id");
    } // get intent data

    private void initToolBar() {
        setSupportActionBar(tool_bar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tool_bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MessageActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });


    } // initialize tool bar

    private void sendMessage(String sender, final String receiver, String message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isSeen", false);

        databaseReference.child("Chats").push().setValue(hashMap);

        // addduser to cat fragment
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid())
                .child(user_id);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(user_id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final String msg = message;
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (notify) {
                    sendNotification(receiver, user.getUsername(), msg);

                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    } // sending message

    private void currentUser(String userid)
    {
        SharedPreferences.Editor editor = getSharedPreferences("PREFS",MODE_PRIVATE).edit();
        editor.putString("currentUser",userid);
        editor.apply();
    }

    private void sendNotification(String receiver, final String username, String msg) {
        final DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.drawable.ic_notification, username + " : " + message,
                            "New message", user_id);

                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(MessageActivity.this, "Failes !!!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    } // sending notification

    @OnClick(R.id.btn_send)
    public void sendMessageClick() {
        notify = true;
        message = et_send.getText().toString().trim();
        if (!message.equals("")) {
            sendMessage(firebaseUser.getUid(), user_id, message);
        } else {
            Toast.makeText(this, "You cant send empty message !!!", Toast.LENGTH_SHORT).show();
        }

        et_send.setText("");
    } // click on send button

    private void readMessage(final String myId, final String userId, final String imageUrl) {
        chatList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    try {
                        if (chat.getReceiver().equals(myId) && chat.getSender().equals(userId)
                                || chat.getReceiver().equals(userId) && chat.getSender().equals(myId)) {
                            chatList.add(chat);
                        }

                        messageAdapter = new MessageAdapter(MessageActivity.this, chatList, imageUrl);
                        chat_recycler_view.setAdapter(messageAdapter);

                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    } // read message function

    private void status(String status) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        databaseReference.updateChildren(hashMap);

    } // show user status function

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        currentUser(user_id);
    } // on resume

    @Override
    protected void onPause() {
        super.onPause();
        databaseReference.removeEventListener(valueEventListener);
        status("offline");
        currentUser("none");
    } // on pause fnction
}
