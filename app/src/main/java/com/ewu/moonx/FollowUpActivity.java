package com.ewu.moonx;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

import java.util.Objects;


public class FollowUpActivity extends AppCompatActivity {
    public static final short FOLLOWUP_TAB_ID = 0;
    public static final short MESSAGE_TAB_ID = 1;
    public static final short ATT_TAB_ID = 2;

    MeowBottomNavigation bottomNavigation;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_up);
        Objects.requireNonNull(getSupportActionBar()).hide();

        initViewPager();
        createBottomNavigationBar();
    }

    private void initViewPager() {
        viewPager = findViewById(R.id.viewPager);
        SlidePagerAdapter adapter = new SlidePagerAdapter(getSupportFragmentManager(),SlidePagerAdapter.POSITION_UNCHANGED);
        adapter.addFragment(new FollowFragment());
        adapter.addFragment(new ChatFragment());
        adapter.addFragment(new FP3());

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(MESSAGE_TAB_ID,false);
     }

    private void createBottomNavigationBar() {
        bottomNavigation = findViewById(R.id.navigate);

        bottomNavigation.add(new MeowBottomNavigation.Model(FOLLOWUP_TAB_ID, R.drawable.location));
        bottomNavigation.add(new MeowBottomNavigation.Model(MESSAGE_TAB_ID, R.drawable.add_message));
        bottomNavigation.add(new MeowBottomNavigation.Model(ATT_TAB_ID, R.drawable.checklist));


        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
                viewPager.setCurrentItem(item.getId(),true);
            }
        });

        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
            }
        });

        bottomNavigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {
                // your codes
                if(item.getId() == MESSAGE_TAB_ID)
                {
                    addMessageToContact();
                }
            }

            private void addMessageToContact() {

            }
        });

        bottomNavigation.show(MESSAGE_TAB_ID, false);
    }
}