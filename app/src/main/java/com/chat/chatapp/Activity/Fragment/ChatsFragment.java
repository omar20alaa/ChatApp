package com.chat.chatapp.Activity.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.service.autofill.Dataset;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chat.chatapp.Activity.Adapter.UserAdapter;
import com.chat.chatapp.Activity.Model.Chat;
import com.chat.chatapp.Activity.Model.ChatList;
import com.chat.chatapp.Activity.Model.User;
import com.chat.chatapp.Activity.Notification.Token;
import com.chat.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatsFragment extends Fragment {

    //  bind views
    @BindView(R.id.chats_recycler_view)
    RecyclerView chats_recycler_view;

    // vars
    UserAdapter userAdapter;
    List<User> mUsers;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    List<String> userList;
    List<ChatList> chatLists;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        ButterKnife.bind(this, view);
        initFirebase();
        initRecyclerView();
        return view;
    }

    private void initFirebase() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        chatLists = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatLists.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatList chat = snapshot.getValue(ChatList.class);
                    chatLists.add(chat);

                }

                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updateToken(FirebaseInstanceId.getInstance().getToken());
    } // init firebase

    private void chatList() {
        mUsers = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    for (ChatList chatList : chatLists) {
                        try {
                            if (user.getId().equals(chatList.getId())) {
                                mUsers.add(user);
                            }

                            userAdapter = new UserAdapter(getContext(), mUsers, true);
                            chats_recycler_view.setAdapter(userAdapter);
                        } catch (Exception e) {

                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    } // chat list function


    private void initRecyclerView() {
        chats_recycler_view.setHasFixedSize(true);
        chats_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));

    } // init recycler view


    private void updateToken(String token)
    {
         databaseReference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        databaseReference.child(firebaseUser.getUid()).setValue(token1);

    } // update token function

}
