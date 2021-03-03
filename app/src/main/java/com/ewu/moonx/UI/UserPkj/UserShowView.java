package com.ewu.moonx.UI.UserPkj;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ewu.moonx.App.PublicVariable;
import com.ewu.moonx.Pojo.DB.Template.Users;
import com.ewu.moonx.R;

public class UserShowView {
    View mainView;
    Activity con;
    Users user;

    public UserShowView(Activity con, Users user) {
        this.con = con;
        this.user = user;
        mainView = con.getLayoutInflater().inflate(R.layout.view_users_show, null);
        ((TextView) mainView.findViewById(R.id.userName))
                .setText(user.getFirstName() + " " + user.getThirdName());
        initEvent();
    }

    private void initEvent() {
        mainView.findViewById(R.id.view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(con,UserInfoActivity.class);
                intent.putExtra(PublicVariable.UserInfo,user);
                con.startActivity(intent);
            }
        });
    }

    public View getMainView() {
        return mainView;
    }

    public Users getUser() {
        return user;
    }
}
