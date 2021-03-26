package com.ewu.moonx.UI.UserPkj;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ewu.moonx.App.CustomView;
import com.ewu.moonx.App.Firebase;
import com.ewu.moonx.App.Status;
import com.ewu.moonx.Pojo.DB.FireBaseTemplate.Str;
import com.ewu.moonx.Pojo.DB.Models.UserConfig;
import com.ewu.moonx.Pojo.DB.Models.Users;
import com.ewu.moonx.Pojo.DB.Tables.UsersTable;
import com.ewu.moonx.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.wang.avi.AVLoadingIndicatorView;

public class NewUserView extends CustomView {

    AVLoadingIndicatorView avi;
    ImageView refreshImg;
    Users user;

    public NewUserView(Activity con, Users user) {
        super(con, R.layout.view_newuser);
        this.user = user;
        this.avi = mainView.findViewById(R.id.avi);
        this.refreshImg = mainView.findViewById(R.id.refreshImg);

        String fullName = user.getFirstName() + " " + user.getSecondName() + " " + user.getThirdName();
        ((TextView) _f(R.id.fullName)).setText(fullName);
        ((TextView) _f(R.id.phone)).setText(user.getPhone());
    }

    NewUserListener listener;

    public void setListener(NewUserListener listener) {
        this.listener = listener;
    }

    public Users getUser() {
        return user;
    }

    interface NewUserListener {
        void afterUserProgress(NewUserView view);
    }

    @Override
    protected void initEvent() {
        _f(R.id.mainRL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupDialog();
            }

            private void showPopupDialog() {
                PopupMenu popupMenu = new PopupMenu(con, _f(R.id.mainRL));
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.confirm:
                                showIndater();
                                confirmUser();
                                break;
                            case R.id.block:
                                showIndater();
                                blockUser();
                                break;
                            case R.id.chat_with:
                                chatWithUser();
                                break;
                        }
                        return false;
                    }

                    private void showIndater() {
                        avi.setVisibility(View.VISIBLE);
                        avi.animate().alpha(1);
                        refreshImg.setVisibility(View.INVISIBLE);
                        refreshImg.animate().alpha(0);
                    }
                });
                popupMenu.inflate(R.menu.newuser_option_menu);

                for (int i = 0; i < popupMenu.getMenu().size(); i++)
                    popupMenu.getMenu().getItem(i).setTitle(popupMenu.getMenu().getItem(i).getTitle() + " " + user.getFirstName());

                popupMenu.show();
            }

            private void chatWithUser() {

            }

            private void blockUser() {
                UserConfig userConfig = new UserConfig(user.getId(), UsersTable.hisBlock);
                Firebase.FireCloudRef(Str.UsersConf).document(user.getId()).set(userConfig)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                deleteUser();
                            }

                            private void deleteUser() {
                                endConfirm();
                                Firebase.FireCloudRef(Str.TempUsers).document(user.getId()).delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(con, "Delete Done", Toast.LENGTH_SHORT).show();
                                            listener.afterUserProgress(NewUserView.this);
                                        })
                                        .addOnFailureListener(e -> Status.startShowBalloonMessage(con, _f(R.id.mainRL), con.getString(R.string.weak_internet_connection)));
                            }

                            private void endConfirm() {
                                avi.animate().alpha(0).withEndAction(() -> {
                                    refreshImg.setVisibility(View.VISIBLE);
                                    refreshImg.animate().alpha(1).setDuration(1000).withEndAction(() -> listener.afterUserProgress(NewUserView.this));
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        disableIndater();
                        Status.startShowBalloonMessage(con, _f(R.id.mainRL), con.getString(R.string.weak_internet_connection));
                    }

                    private void disableIndater() {
                        avi.setVisibility(View.INVISIBLE);
                        avi.animate().alpha(0);
                        refreshImg.setVisibility(View.INVISIBLE);
                        refreshImg.animate().alpha(0);
                    }
                });
            }

            private void confirmUser() {
                UserConfig userConfig = new UserConfig(user.getId(), user.getType());
                Firebase.FireCloudRef(Str.UsersConf).document(user.getId()).set(userConfig)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                shiftUserToUsers();
                            }

                            private void shiftUserToUsers() {
                                Firebase.FireCloudRef(Str.Users).document(user.getId()).set(user)
                                        .addOnSuccessListener(aVoid -> removeFromTempUser())
                                        .addOnFailureListener(e -> Status.startShowBalloonMessage(con, _f(R.id.mainRL), con.getString(R.string.weak_internet_connection)));
                            }

                            private void removeFromTempUser() {
                                Firebase.FireCloudRef(Str.TempUsers).document(user.getId()).delete()
                                        .addOnSuccessListener(aVoid -> endConfirm())
                                        .addOnFailureListener(e -> Status.startShowBalloonMessage(con, _f(R.id.mainRL), con.getString(R.string.weak_internet_connection)));
                            }

                            private void endConfirm() {
                                avi.animate().alpha(0).withEndAction(() -> {
                                    refreshImg.setVisibility(View.VISIBLE);
                                    refreshImg.animate().alpha(1).setDuration(1000).withEndAction(() -> listener.afterUserProgress(NewUserView.this));
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        disableIndater();
                        Status.startShowBalloonMessage(con, _f(R.id.mainRL), con.getString(R.string.weak_internet_connection));
                    }

                    private void disableIndater() {
                        avi.setVisibility(View.INVISIBLE);
                        avi.animate().alpha(0);
                        refreshImg.setVisibility(View.INVISIBLE);
                        refreshImg.animate().alpha(0);
                    }
                });
            }
        });
    }
}
