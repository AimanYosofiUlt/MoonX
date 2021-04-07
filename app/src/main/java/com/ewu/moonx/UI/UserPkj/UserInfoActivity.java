package com.ewu.moonx.UI.UserPkj;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ewu.moonx.App.Static;
import com.ewu.moonx.Pojo.DB.Models.Users;
import com.ewu.moonx.Pojo.DB.Tables.SettingTable;
import com.ewu.moonx.R;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoActivity extends AppCompatActivity {
    Users user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        getSupportActionBar().hide();

        user = ((Users) getIntent().getSerializableExtra(Static.UserInfo));
        init();
    }

    private void init() {
        String fullName = user.getFirstName() + " " + user.getSecondName() + " " + user.getThirdName();
        ((TextView) findViewById(R.id.userName)).setText(fullName);

        if (user.getType().equals(SettingTable.hisAdmin)) {
            ((ImageView) findViewById(R.id.appIcon)).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.glasslogo));
            ((TextView) findViewById(R.id.appName)).setText(R.string.moonx_platform);
        } else {
            ((ImageView) findViewById(R.id.appIcon)).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.menu_logo2));
            ((TextView) findViewById(R.id.appName)).setText(R.string.jounralist_app);
        }

        if (!user.getImageName().equals("null"))
            setUserImg();
    }

    private void setUserImg() {
        File file = new File(Static.getProfileImagePath(this), user.getId() + "." + user.getImageName());
        if (file.exists()) {
            Uri uri = Uri.fromFile(file);
            CircleImageView userImage = findViewById(R.id.userImg);
            userImage.setImageURI(null);
            userImage.setImageURI(uri);
            userImage.invalidate();
        }

    }
}
