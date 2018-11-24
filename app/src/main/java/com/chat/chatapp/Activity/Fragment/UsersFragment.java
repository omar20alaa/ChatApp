package com.chat.chatapp.Activity.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.chat.chatapp.Activity.Adapter.UserAdapter;
import com.chat.chatapp.Activity.Model.User;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UsersFragment extends Fragment {

    // bind views
    @BindView(R.id.users_recycler_view)
    RecyclerView users_recycler_view;

    @BindView(R.id.et_search_users)
    EditText et_search_users;

    // vars
    List<User> userList;
    UserAdapter userAdapter;
    FirebaseUser firebaseUser;
    FirebaseUser firebaseUserSearch;
    DatabaseReference databaseReference;
    Query query;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        ButterKnife.bind(this, view);
        initRecyclerView();
        searchUsers();
        return view;
    }

    private void searchUsers() {
        et_search_users.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searching(charSequence.toString());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searching(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    } // search for existing users function

    private void searching(String s) {
        firebaseUserSearch = FirebaseAuth.getInstance().getCurrentUser();
        query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
                .startAt(s)
                .endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    try {
                        if (!user.getId().equals(firebaseUser.getUid())) {
                            userList.add(user);
                        }

                        userAdapter = new UserAdapter(getContext(), userList, false);
                        users_recycler_view.setAdapter(userAdapter);
                    } catch (Exception e) {

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    } // searching for users

    private void initRecyclerView() {
        users_recycler_view.setHasFixedSize(true);
        users_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
        userList = new ArrayList<>();
        readUsers();
    } // init recycler view

    private void readUsers() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                if (et_search_users.getText().toString().equals("")) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        String key = snapshot.getKey();
                        User user = snapshot.getValue(User.class);

                        Log.i("QP", "key : " + key + " : user : " + user.getUsername());
                        if (user != null) {
                            try {
                                if (!user.getId().equals(firebaseUser.getUid())) {
                                    userList.add(user);
                                }
                            } catch (Exception e) {

                            }
                        }

                    }


                    userAdapter = new UserAdapter(getContext(), userList, false);
                    users_recycler_view.setAdapter(userAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    } // read users function


}
