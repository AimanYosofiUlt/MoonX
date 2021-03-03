package com.ewu.moonx.UI.UserPkj;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ewu.moonx.App.PublicVariable;
import com.ewu.moonx.Pojo.DB.Template.Users;
import com.ewu.moonx.Pojo.DB.UsersTable;
import com.ewu.moonx.R;

public class UserInfoActivity extends AppCompatActivity {
    Users user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        getSupportActionBar().hide();

        user = ((Users) getIntent().getSerializableExtra(PublicVariable.UserInfo));
        init();
    }

    private void init() {
        String fullName = user.getFirstName() + " " + user.getSecondName() + " " + user.getThirdName();
        ((TextView) findViewById(R.id.userName)).setText(fullName);

        if (user.getType().equals(UsersTable.hisAdmin)) {
            ((ImageView) findViewById(R.id.appIcon)).setImageDrawable(getResources().getDrawable(R.drawable.glasslogo));
            ((TextView) findViewById(R.id.appName)).setText(R.string.moonx_platform);
        } else {
            ((ImageView) findViewById(R.id.appIcon)).setImageDrawable(getResources().getDrawable(R.drawable.menu_logo2));
            ((TextView) findViewById(R.id.appName)).setText(R.string.jounralist_app);
        }
    }
}
