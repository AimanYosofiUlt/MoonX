package com.ewu.moonx.UI.UserPkj;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ewu.moonx.App.Firebase;
import com.ewu.moonx.App.FirebaseOrderHandler;
import com.ewu.moonx.App.Static;
import com.ewu.moonx.App.Status;
import com.ewu.moonx.Pojo.DB.FireBaseTemplate.Str;
import com.ewu.moonx.Pojo.DB.Models.Users;
import com.ewu.moonx.Pojo.DB.Tables.UsersTable;
import com.ewu.moonx.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserActivity extends AppCompatActivity {

    RefreshButton newUserRB, platfromRB, journalistRB;
    ArrayList<NewUserView> newUserViews;
    ArrayList<UserShowView> usersViews;
    TextView newUser_emptyMsg, newUser_offline;
    TextView platform_emptyMsg, platfrom_offline;
    TextView journalist_emptyMsg, journalist_offline;

    LinearLayout newUserLL, plafromLL, journalistLL;
    RelativeLayout shareAppBtn;
    final int A_B = -1, B_A = 1, A_A = 0;
    public static boolean isForUserChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        init();
        initEvent();
        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    private void initEvent() {
        shareAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserActivity.this, "Share App", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init() {
        newUser_emptyMsg = findViewById(R.id.newUser_emptyMsg);
        newUser_offline = findViewById(R.id.newUser_offline);
        newUserLL = findViewById(R.id.newUserLL);
        plafromLL = findViewById(R.id.plafromLL);
        platform_emptyMsg = findViewById(R.id.platform_emptyMsg);
        platfrom_offline = findViewById(R.id.platfrom_offline);

        journalistLL = findViewById(R.id.journalistLL);
        journalist_emptyMsg = findViewById(R.id.journalist_emptyMsg);
        journalist_offline = findViewById(R.id.journalist_offline);

        newUserViews = new ArrayList<>();
        newUserRB = new RefreshButton(this, R.id.newUserRefersh, R.id.newUserAVI);
        newUserRB.setListener(this::addNewUserFromFDB);

        usersViews = new ArrayList<>();
        platfromRB = new RefreshButton(this, R.id.platfromRefersh, R.id.platformAVI);
        platfromRB.setListener(this::addUsersFromFDB);

        journalistRB = new RefreshButton(this, R.id.journalistRefersh, R.id.journalistAVI);
        journalistRB.setListener(this::addUsersFromFDB);

        platfromRB.setConnectButton(journalistRB);
        journalistRB.setConnectButton(platfromRB);

        shareAppBtn = findViewById(R.id.shareAppBtn);

        isForUserChat = getIntent().getBooleanExtra(Static.isForGetChatUser, false);
        if (isForUserChat) {
            findViewById(R.id.startChatTitle).setVisibility(View.VISIBLE);
            shareAppBtn.setVisibility(View.GONE);
        }

        addNewUserFromFDB();
        addUsersFromFDB();
    }

    private void addUsersFromFDB() {
        if (Status.isNetConnected(this)) {
            platfrom_offline.setVisibility(View.GONE);
            journalist_offline.setVisibility(View.GONE);
        } else {
            platfrom_offline.setVisibility(View.VISIBLE);
            journalist_offline.setVisibility(View.VISIBLE);
        }

        Firebase.FireCloudRef(Str.Users).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        addUsersViews(queryDocumentSnapshots.getDocuments());
                    }

                    private void addUsersViews(List<DocumentSnapshot> documents) {
                        FirebaseOrderHandler handler = new FirebaseOrderHandler(usersViews, documents);

                        handler.setListener(new FirebaseOrderHandler.OrderListener() {
                            @Override
                            public int onPreDocsOrder(DocumentSnapshot o1, DocumentSnapshot o2) {
                                Users o1Users = o1.toObject(Users.class);
                                Users o2Users = o2.toObject(Users.class);
                                return startCompare(o1Users, o2Users);
                            }

                            @Override
                            public void onAdd(int firebaseIndex, int localIndex) {
                                Users user = documents.get(firebaseIndex).toObject(Users.class);
                                addUser(user, localIndex);
                            }

                            @Override
                            public void onRemove(int localIndex) {
                                removeUser(localIndex);
                            }

                            @Override
                            public int onCompare(int firebaseIndex, int localIndex) {
                                Users Fuser = documents.get(firebaseIndex).toObject(Users.class);
                                Users Luser = usersViews.get(localIndex).getUser();
                                return startCompare(Fuser, Luser);
                            }
                        }).startOrder();

                        setEmptyMsgVisible();
                        platfromRB.done(false);
                    }

                    private void setEmptyMsgVisible() {
                        if (plafromLL.getChildCount() > 0) {
                            platform_emptyMsg.setVisibility(View.GONE);
                        } else {
                            platform_emptyMsg.setVisibility(View.VISIBLE);
                        }

                        if (journalistLL.getChildCount() > 0) {
                            journalist_emptyMsg.setVisibility(View.GONE);
                        } else {
                            journalist_emptyMsg.setVisibility(View.VISIBLE);
                        }
                    }

                    private void removeUser(int localIndex) {
                        String type = usersViews.get(localIndex).getUser().getType();
                        if (type.equals(UsersTable.hisAdmin)) {
                            plafromLL.removeView(usersViews.get(localIndex).getMainView());
                        } else {
                            journalistLL.removeView(usersViews.get(localIndex).getMainView());
                        }
                        usersViews.remove(localIndex);

                    }

                    private void addUser(Users user, int index) {
                        UserShowView userView = new UserShowView(UserActivity.this, user);
                        usersViews.add(index, userView);
                        if (user.getType().equals(UsersTable.hisAdmin))
                            plafromLL.addView(userView.getMainView(), index);
                        else
                            journalistLL.addView(userView.getMainView());
                    }

                    private int startCompare(Users user1, Users user2) {
                        int compareFirstName = user1.getFirstName().compareTo(user2.getFirstName());
                        if (compareFirstName == 0) {
                            return user1.getThirdName().compareTo(user2.getThirdName());
                        } else
                            return compareFirstName;
                    }
                })
                .addOnFailureListener(e -> {
                    platfromRB.done(true);
                    Status.startShowBalloonMessage(UserActivity.this, newUserRB.geRefreshImg(), getString(R.string.weak_internet_connection));
                    if (newUserViews.isEmpty())
                        newUser_emptyMsg.setVisibility(View.VISIBLE);
                });
    }

    private void addNewUserFromFDB() {
        if (Status.isNetConnected(this))
            newUser_offline.setVisibility(View.GONE);
        else
            newUser_offline.setVisibility(View.VISIBLE);

        Firebase.FireCloudRef(Str.TempUsers).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        addNewUserViews(queryDocumentSnapshots.getDocuments());
                    }

                    private void addNewUserViews(List<DocumentSnapshot> documents) {
                        newUser_emptyMsg.setVisibility(View.GONE);

                        FirebaseOrderHandler handler = new FirebaseOrderHandler(newUserViews, documents);

                        handler.setListener(new FirebaseOrderHandler.OrderListener() {
                            @Override
                            public int onPreDocsOrder(DocumentSnapshot o1, DocumentSnapshot o2) {
                                Users o1User = o1.toObject(Users.class);
                                Users o2Users = o2.toObject(Users.class);
                                return compare(o1User, o2Users);
                            }

                            @Override
                            public void onAdd(int firebaseIndex, int localIndex) {
                                Users user = documents.get(firebaseIndex).toObject(Users.class);
                                NewUserView userView = new NewUserView(UserActivity.this, user);
                                userView.setListener(view -> {
                                    newUserViews.remove(view);
                                    view.getMainView().animate().alpha(0).withEndAction(() -> {
                                        newUserLL.removeView(view.getMainView());
                                        if (newUserViews.isEmpty())
                                            newUser_emptyMsg.setVisibility(View.VISIBLE);
                                    });

                                    newUserRB.startAnimation();
                                    addUsersFromFDB();
                                });
                                newUserViews.add(localIndex, userView);
                                newUserLL.addView(userView.getMainView(), localIndex);
                            }

                            @Override
                            public void onRemove(int localIndex) {
                                newUserLL.removeView(usersViews.get(localIndex).getMainView());
                                newUserViews.remove(localIndex);
                            }

                            @Override
                            public int onCompare(int firebaseIndex, int localIndex) {
                                Users FUsers = documents.get(firebaseIndex).toObject(Users.class);
                                Users LUsers = newUserViews.get(localIndex).getUser();
                                return compare(FUsers, LUsers);
                            }
                        }).startOrder();

                        setEmptyMsgVisible();
                        newUserRB.done(false);
                    }

                    private void setEmptyMsgVisible() {
                        if (newUserViews.isEmpty())
                            newUser_emptyMsg.setVisibility(View.VISIBLE);
                        else
                            newUser_emptyMsg.setVisibility(View.GONE);
                    }

                    private int compare(Users user1, Users user2) {
                        int firstNameCompare = user1.getFirstName().compareTo(user2.getFirstName());
                        if (firstNameCompare == 0) {

                            int SecondNameCompare = user1.getSecondName().compareTo((user2.getSecondName()));
                            if (SecondNameCompare == 0)
                                return user1.getThirdName().compareTo(user2.getThirdName());
                            else
                                return SecondNameCompare;

                        } else
                            return firstNameCompare;
                    }
                })
                .addOnFailureListener(e -> {
                    newUserRB.done(true);
                    Status.startShowBalloonMessage(UserActivity.this, newUserRB.geRefreshImg(), getString(R.string.weak_internet_connection));
                    if (newUserViews.isEmpty())
                        newUser_emptyMsg.setVisibility(View.VISIBLE);
                });
    }

}

