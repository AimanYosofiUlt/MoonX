package com.ewu.moonx.UI.UserPkj;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ewu.moonx.App.CustomView;
import com.ewu.moonx.App.Firebase;
import com.ewu.moonx.App.Static;
import com.ewu.moonx.Pojo.DB.FireBaseTemplate.Folder;
import com.ewu.moonx.Pojo.DB.Models.Users;
import com.ewu.moonx.R;

import java.io.File;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserShowView extends CustomView {
    Users user;

    public UserShowView(Activity con, Users user) {
        super(con, R.layout.view_users_show);
        this.user = user;

        if (user.getId().equals(Static.getUid()))
            _f(R.id.you).setVisibility(View.VISIBLE);

        ((TextView) _f(R.id.userName))
                .setText(String.format("%s %s", user.getFirstName(), user.getThirdName()));

        if (!user.getImageName().equals("null"))
            setImageFromFiles();
    }

    private void setImageFromFiles() {
        Toast.makeText(con, "setImageFrom " + user.getFirstName(), Toast.LENGTH_SHORT).show();
        File profileImageDir = new File(Static.getProfileImagePath(con));
        if (!profileImageDir.exists() || profileImageDir.listFiles() == null) {
            downloadImage(profileImageDir);
        } else {
            boolean isFileNotFound = true;
            for (File file : Objects.requireNonNull(profileImageDir.listFiles())) {

                if (file.getName().startsWith(user.getId())) {
                    isFileNotFound = false;

                    String imageFullName = user.getId() + "." + user.getImageName();
                    if (imageFullName.equals(file.getName())) {
                        setImageFrom(file);
                    } else {
                        file.delete();
                        downloadImage(profileImageDir);
                    }

                    break;

                }
            }

            if (isFileNotFound)
                downloadImage(profileImageDir);
        }

    }


    private void setImageFrom(File file) {
        Uri uri = Uri.fromFile(file);

        CircleImageView userImage = ((CircleImageView) _f(R.id.userImg));
        userImage.setImageURI(null);
        userImage.setImageURI(uri);
        userImage.invalidate();
    }

    int dc = 0;

    private void downloadImage(File dir) {
        String imageFullName = user.getId() + "." + user.getImageName();
        File file = new File(dir, imageFullName);

        Firebase.StorageRef(Folder.Users)
                .child(Folder.UsersImage)
                .child(user.getId())
                .getFile(file)
                .addOnSuccessListener(taskSnapshot -> setImageFrom(file))
                .addOnFailureListener(e -> {
                    file.delete();
                    if (dc < 2)
                        downloadImage(dir);
                    dc++;
                });
    }


    @Override
    protected void initEvent() {
        _f(R.id.view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(con, UserInfoActivity.class);
                intent.putExtra(Static.UserInfo, user);
                con.startActivity(intent);
            }
        });
    }

    public Users getUser() {
        return user;
    }
}
