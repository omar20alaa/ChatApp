package com.chat.chatapp.Activity.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chat.chatapp.Activity.Fragment.ChatsFragment;
import com.chat.chatapp.Activity.Fragment.ProfileFragment;
import com.chat.chatapp.Activity.Fragment.UsersFragment;
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

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    // bind views

    @BindView(R.id.profile_image)
    CircleImageView profile_image;

    @BindView(R.id.tv_user_name)
    TextView tv_user_name;

    @BindView(R.id.tool_bar)
    android.support.v7.widget.Toolbar tool_bar;

    @BindView(R.id.tab_layout)
    TabLayout tab_layout;

    @BindView(R.id.view_pager)
    ViewPager view_pager;

    // variables

    FirebaseUser firebaseUser;
    DatabaseReference firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initToolbar();
        initFirebase();
    }

    private void initToolbar() {
        setSupportActionBar(tool_bar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    } // init tool bar

    private void initFirebase() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                if (user != null) {
                    try {
                        tv_user_name.setText(user.getUsername());
                        if (user.getImageUrl().equals("default")) {
                            profile_image.setImageResource(R.mipmap.ic_launcher);
                        } else {
                            Glide.with(getApplicationContext())
                                    .load(user.getImageUrl())
                                    .into(profile_image);
                        }

                    } catch (Exception e) {

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        firebaseDatabase = FirebaseDatabase.getInstance().getReference("Chats");
        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
                int unread = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);

                    try {
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && !chat.getIsSeen()) {
                            unread++;

                        }
                    }
                    catch (Exception e)
                    {

                    }

                }

                if (unread == 0) {
                    viewPagerAdapter.addFragment(new ChatsFragment(), "Chats");

                } else {
                    viewPagerAdapter.addFragment(new ChatsFragment(), unread + " " + "Chats");

                }
                viewPagerAdapter.addFragment(new UsersFragment(), "Users");
                viewPagerAdapter.addFragment(new ProfileFragment(), "Profile");
                view_pager.setAdapter(viewPagerAdapter);
                tab_layout.setupWithViewPager(view_pager);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    } // initialize firebase

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;

    } // on create option menu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
        }
        return false;

    } // on select item

    class ViewPagerAdapter extends FragmentPagerAdapter {

        // variables
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        } // constructor


        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        } // add fragment function

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    } // set view pager pages

    private void status(String status) {
        firebaseDatabase = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        firebaseDatabase.updateChildren(hashMap);

    } // show user status function

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    } // on resume

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    } // on pause fnction
}
