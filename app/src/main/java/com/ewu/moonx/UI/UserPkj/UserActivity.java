package com.ewu.moonx.UI.UserPkj;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ewu.moonx.App.Firebase;
import com.ewu.moonx.App.Status;
import com.ewu.moonx.Pojo.DB.Template.Str;
import com.ewu.moonx.Pojo.DB.Template.Users;
import com.ewu.moonx.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class UserActivity extends AppCompatActivity {

    RefreshButton newUserRB, platfromRB, journalistRB;
    ArrayList<NewUserView> newUserViews;
    ArrayList<UserShowView> UsersViews;
    TextView newUser_emptyMsg, newUser_offline;
    TextView platform_emptyMsg, platfrom_offline;
    TextView journalist_emptyMsg, journalist_offline;

    LinearLayout newUserLL, plafromLL, journalistLL;
    final int A_B = -1, B_A = 1, A_A = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        init();
        initEvent();
        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    private void initEvent() {

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

        UsersViews = new ArrayList<>();
        platfromRB = new RefreshButton(this, R.id.platfromRefersh, R.id.platformAVI);
        platfromRB.setListener(this::odredTrain);

        journalistRB = new RefreshButton(this, R.id.journalistRefersh, R.id.journalistAVI);
        journalistRB.setListener(this::addUsersFromFDB);

        names = new ArrayList<>();
        addNewUserFromFDB();
        odredTrain();
    }

    ArrayList<Name> names;

    private void odredTrain() {

        Firebase.FireCloudRef("Todel").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        addUsersViews(queryDocumentSnapshots.getDocuments());
                    }

                    private void addUsersViews(List<DocumentSnapshot> documents) {

                        Collections.sort(documents, new Comparator<DocumentSnapshot>() {
                            @Override
                            public int compare(DocumentSnapshot o1, DocumentSnapshot o2) {
                                Name o1Name = o1.toObject(Name.class);
                                Name o2Name = o2.toObject(Name.class);
                                return o1Name.getName().compareToIgnoreCase(o2Name.getName());
                            }
                        });


                        if (names.size() == 0) {
                            for (DocumentSnapshot snapshot : documents) {
                                Name name = snapshot.toObject(Name.class);
                                addUser(name, names.size());
                            }
                        } else {
                            int d = 0;
                            for (DocumentSnapshot snapshot : documents) {
                                boolean isNewUserBT = false;
                                Name name = snapshot.toObject(Name.class);

//                                if (names.size() < d) {
//                                    addUser(name, names.size());
//                                    d++;
//                                    Toast.makeText(UserActivity.this, "Add " + d + "  " + names.size(), Toast.LENGTH_SHORT).show();
//                                } else {

                                while (names.size() > d) {
                                    int compare = name.getName().compareTo(names.get(d).getName());
                                    Toast.makeText(UserActivity.this, "" + d + "  " + names.size(), Toast.LENGTH_SHORT).show();
                                    if (compare == 0) {
                                        isNewUserBT = true;
                                    } else if (compare < 0) {
                                        addUser(name, d);
                                        isNewUserBT = true;
                                    } else if (compare > 0) {
//                                        removeUser(d++);
                                    }

                                    d++;

                                }
//                                }
                            }
                        }

                        platfromRB.done(false);
                    }

                    private void removeUser(int d) {
                        names.add(names.get(d));
                    }

                    private void addUser(Name name, int index) {
                        Users user = new Users("2", name.getName(), "", "", "", "", 0);
                        UserShowView userView = new UserShowView(UserActivity.this, user);
//                        names.add(index, name);
                        plafromLL.addView(userView.getMainView(), index);

                    }

                    private int compare(Users user1, Users user2) {
                        int compareFirstName = user1.getFirstName().compareTo(user2.getFirstName());
                        if (compareFirstName == 0) {
                            int compareThirdName = user1.getThirdName().compareTo(user2.getThirdName());
                            if (compareThirdName == 0)
                                return A_A;
                            else if (compareThirdName < 0)
                                return A_B;
                            else
                                return B_A;
                        } else if (compareFirstName < 0)
                            return A_B;
                        else
                            return B_A;
                    }
                })
                .addOnFailureListener(e -> {
                    newUserRB.done(true);
                    Status.startShowBalloonMessage(UserActivity.this, newUserRB.geRefreshImg(), getString(R.string.weak_internet_connection));
                    if (newUserViews.isEmpty())
                        newUser_emptyMsg.setVisibility(View.VISIBLE);
                });
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
                        if (queryDocumentSnapshots.isEmpty()) {
                            if (UsersViews.isEmpty())
                                platform_emptyMsg.setVisibility(View.VISIBLE);
                            platfromRB.done(false);
                        } else {
                            addUsersViews(queryDocumentSnapshots.getDocuments());
                        }
                    }

                    private void addUsersViews(List<DocumentSnapshot> documents) {
                        platform_emptyMsg.setVisibility(View.GONE);

                        if (UsersViews.isEmpty()) {
                            Users user = documents.get(0).toObject(Users.class);
                            addUser(user, 0);
                        }

                        for (DocumentSnapshot snapshot : documents) {
                            boolean isNeedAdd = true;
                            Users newUser = snapshot.toObject(Users.class);
                            for (int i = 0; i < UsersViews.size(); i++) {
                                assert newUser != null;
                                int state = compare(UsersViews.get(i).getUser(), newUser);
                                if (state == A_A) {
                                    isNeedAdd = false;
                                    break;
                                } else if (state == B_A) {
                                    addUser(newUser, i);
                                    isNeedAdd = false;
                                    break;
                                }
                            }

                            if (isNeedAdd)
                                addUser(newUser, UsersViews.size());
                        }

                        platfromRB.done(false);
                    }

                    private void addUser(Users user, int index) {
                        UserShowView userView = new UserShowView(UserActivity.this, user);
                        UsersViews.add(index, userView);
                        plafromLL.addView(userView.getMainView(), index);

                    }

                    private int compare(Users user1, Users user2) {
                        int compareFirstName = user1.getFirstName().compareTo(user2.getFirstName());
                        if (compareFirstName == 0) {
                            int compareThirdName = user1.getThirdName().compareTo(user2.getThirdName());
                            if (compareThirdName == 0)
                                return A_A;
                            else if (compareThirdName < 0)
                                return A_B;
                            else
                                return B_A;
                        } else if (compareFirstName < 0)
                            return A_B;
                        else
                            return B_A;
                    }
                })
                .addOnFailureListener(e -> {
                    newUserRB.done(true);
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
                        if (queryDocumentSnapshots.isEmpty()) {
                            if (newUserViews.isEmpty())
                                newUser_emptyMsg.setVisibility(View.VISIBLE);
                            newUserRB.done(false);
                        } else {
                            addNewUserViews(queryDocumentSnapshots.getDocuments());
                        }
                    }

                    private void addNewUserViews(List<DocumentSnapshot> documents) {
                        newUser_emptyMsg.setVisibility(View.GONE);

                        if (newUserViews.isEmpty()) {
                            Users user = documents.get(0).toObject(Users.class);
                            addUser(user, 0);
                        }

                        for (DocumentSnapshot snapshot : documents) {
                            boolean isNeedAdd = true;
                            Users newUser = snapshot.toObject(Users.class);
                            for (int i = 0; i < newUserViews.size(); i++) {
                                assert newUser != null;
                                int state = compare(newUserViews.get(i).getUser(), newUser);
                                if (state == A_A) {
                                    isNeedAdd = false;
                                    break;
                                } else if (state == B_A) {
                                    addUser(newUser, i);
                                    isNeedAdd = false;
                                    break;
                                }
                            }

                            if (isNeedAdd)
                                addUser(newUser, newUserViews.size());
                        }

                        newUserRB.done(false);
                    }

                    private void addUser(Users user, int index) {
                        NewUserView userView = new NewUserView(UserActivity.this, user);
                        userView.setListener(view -> {
                            newUserViews.remove(view);
                            view.getMainView().animate().alpha(0).withEndAction(() -> {
                                newUserLL.removeView(view.getMainView());
                                if (newUserViews.isEmpty())
                                    newUser_emptyMsg.setVisibility(View.VISIBLE);
                            });
                        });
                        newUserViews.add(index, userView);
                        newUserLL.addView(userView.getMainView(), index);
                    }

                    private int compare(Users user1, Users user2) {
                        int compareFirstName = user1.getFirstName().compareTo(user2.getFirstName());
                        if (compareFirstName == 0) {
                            int compareSecondName = user1.getSecondName().compareTo((user2.getSecondName()));
                            if (compareSecondName == 0) {
                                int compareThirdName = user1.getThirdName().compareTo(user2.getThirdName());
                                if (compareThirdName == 0)
                                    return A_A;
                                else if (compareThirdName < 0)
                                    return A_B;
                                else
                                    return B_A;
                            } else if (compareSecondName < 0)
                                return A_B;
                            else
                                return B_A;
                        } else if (compareFirstName < 0)
                            return A_B;
                        else
                            return B_A;
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

