package com.ewu.moonx.UI.FollowUpPkj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.ewu.moonx.App.Static;
import com.ewu.moonx.R;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.ChatFragment;
import com.ewu.moonx.UI.UserPkj.UserActivity;

import java.util.Objects;


public class FollowUpActivity extends AppCompatActivity {
    public static final short FOLLOWUP_TAB_ID = 0;
    public static final short MESSAGE_TAB_ID = 1;
    public static final short ATT_TAB_ID = 2;

    MeowBottomNavigation bottomNavigation;
    ViewPager viewPager;
    ImageView addMessageBtn;
    boolean isShowFromViewPager = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_up);
        Objects.requireNonNull(getSupportActionBar()).hide();

        init();
        initViewPager();
        initEvent();
        createBottomNavigationBar();
    }

    private void init() {
        addMessageBtn = findViewById(R.id.addMessageBtn);
        viewPager = findViewById(R.id.viewPager);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initEvent() {
        addMessageBtn.setOnClickListener(v -> {
            Intent intent = new Intent(FollowUpActivity.this, UserActivity.class);
            intent.putExtra(Static.isForGetChatUser, true);
            FollowUpActivity.this.startActivityForResult(intent, 1);
        });

        addMessageBtn.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                addMessageBtn.setImageDrawable(ContextCompat.getDrawable(FollowUpActivity.this, R.drawable.add_message_inclick));
            else
                addMessageBtn.setImageDrawable(ContextCompat.getDrawable(FollowUpActivity.this, R.drawable.add_message2));

            return false;
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                isShowFromViewPager = true;
                bottomNavigation.show(position, true);
                setAddChatImg(position);
                isShowFromViewPager =false;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initViewPager() {
        SlidePagerAdapter adapter = new SlidePagerAdapter(getSupportFragmentManager(), SlidePagerAdapter.POSITION_UNCHANGED);
        adapter.addFragment(new FollowFragment());
        adapter.addFragment(new ChatFragment(FollowUpActivity.this));
        adapter.addFragment(new FP3());

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(MESSAGE_TAB_ID, false);
    }

    private void createBottomNavigationBar() {
        bottomNavigation = findViewById(R.id.navigate);

        bottomNavigation.add(new MeowBottomNavigation.Model(FOLLOWUP_TAB_ID, R.drawable.location));
        bottomNavigation.add(new MeowBottomNavigation.Model(MESSAGE_TAB_ID, R.drawable.add_message));
        bottomNavigation.add(new MeowBottomNavigation.Model(ATT_TAB_ID, R.drawable.checklist));


        bottomNavigation.setOnClickMenuListener(item -> {
            if (!isShowFromViewPager)
                viewPager.setCurrentItem(item.getId(), true);
        });

        bottomNavigation.setOnShowListener(item -> {

            if (!isShowFromViewPager) {
                setAddChatImg(item.getId());
            }
        });

        bottomNavigation.setOnReselectListener(item -> {
        });

        bottomNavigation.show(MESSAGE_TAB_ID, false);
    }

    private void setAddChatImg(int id) {
        if (id == MESSAGE_TAB_ID) {
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> addMessageBtn.setVisibility(View.VISIBLE), 200);
        } else
            addMessageBtn.setVisibility(View.GONE);
    }
}