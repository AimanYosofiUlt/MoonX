package com.ewu.moonx.UI.LoginPkj;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.ewu.moonx.App.Firebase;
import com.ewu.moonx.App.Static;
import com.ewu.moonx.App.Status;
import com.ewu.moonx.Pojo.DB.DBPkj.Executive.DB;
import com.ewu.moonx.Pojo.DB.FireBaseTemplate.Folder;
import com.ewu.moonx.Pojo.DB.FireBaseTemplate.Str;
import com.ewu.moonx.Pojo.DB.Models.Users;
import com.ewu.moonx.Pojo.DB.Tables.UsersTable;
import com.ewu.moonx.R;
import com.ewu.moonx.UI.MainPkj.MainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Login_setUserImgActivity extends AppCompatActivity {

    private final int RESULT_LOAD_IMAGE = 154;
    ActionProcessButton saveBtn;
    Uri uri;
    UploadTask uploadTask;
    boolean isNeedToSendImg = true;
    String imageName;
    private final int PERMISSON_CODE = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_set_user_img);

        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        saveBtn = findViewById(R.id.save);
        initEvent();
    }

    private void initEvent() {
        findViewById(R.id.pickImgBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(Login_setUserImgActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, RESULT_LOAD_IMAGE);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSON_CODE);
                    }

                }

            }
        });

        findViewById(R.id.pickImgBtn).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    findViewById(R.id.pickImgBtnIcon).setBackground(ContextCompat.getDrawable(Login_setUserImgActivity.this, R.drawable.round_btn2_inclick));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    findViewById(R.id.pickImgBtnIcon).setBackground(ContextCompat.getDrawable(Login_setUserImgActivity.this, R.drawable.round_btn2));
                }
                return false;
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage();
            }

            private void saveImage() {
                if (isNeedToSendImg) {
                    uploadTask = Firebase.StorageRef(Folder.Users).child(Folder.UsersImage).child(Static.getUid()).putFile(uri);
                    initUploadEvent();
                } else {
                    updateImageName();
                }
            }

            private void initUploadEvent() {
                uploadTask.addOnProgressListener(snapshot -> {
                    saveBtn.setProgress(1);
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        saveBtn.setProgress(100);
                        updateImageName();
                    }
                }).addOnFailureListener(e -> Status.showErrorMessage(Login_setUserImgActivity.this, saveBtn, getString(R.string.weak_internet_connection)));
            }
        });
    }

    private void updateImageName() {
        DocumentReference reference = Firebase.FireCloudRef(Str.Users).document(Static.getUid());
        reference.get().addOnSuccessListener(documentSnapshot -> {
            Users users = documentSnapshot.toObject(Users.class);
            assert users != null;
            users.setImageName(imageName);

            reference.set(users)
                    .addOnSuccessListener(aVoid -> showMainActivity())
                    .addOnFailureListener(e -> {
                        isNeedToSendImg = false;
                        Status.showErrorMessage(Login_setUserImgActivity.this, saveBtn, getString(R.string.try_again));
                    });
        }).addOnFailureListener(e -> {
            isNeedToSendImg = false;
            Status.showErrorMessage(Login_setUserImgActivity.this, saveBtn, getString(R.string.try_again));
        });
    }

    private void showMainActivity() {
        UsersTable table = new UsersTable(this);
        DB.set(table.allowUserCol, table.hisAllowed_WithImg).update(table).exec();

        Intent intent = new Intent(Login_setUserImgActivity.this, MainActivity.class);
        Login_setUserImgActivity.this.startActivity(intent);
        Login_setUserImgActivity.this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "Opration field " + requestCode + " " + Activity.RESULT_CANCELED + " " + " " + Activity.RESULT_OK + " " + " " + Activity.RESULT_FIRST_USER + " ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (requestCode == RESULT_LOAD_IMAGE) {
            assert data != null;
            isNeedToSendImg = true;
            cropImage(data.getData());
            Toast.makeText(this, "cut Image", Toast.LENGTH_SHORT).show();
        } else if (requestCode == UCrop.REQUEST_CROP) {
            setUserImage(data);
        }
    }

    private void setUserImage(Intent data) {
        ((CircleImageView) findViewById(R.id.userImg)).setImageURI(null);
        uri = UCrop.getOutput(data);
        ((CircleImageView) findViewById(R.id.userImg)).setImageURI(uri);
        findViewById(R.id.userImg).invalidate();
        saveBtn.setVisibility(View.VISIBLE);
    }

    void cropImage(Uri uri) {
        imageName = Firebase.FireCloudRef(Str.Users).document(Static.getUid()).collection("imageName").document().getId();

        String path = Static.getProfileImagePath(this);
        File folder = new File(path);
        if (!folder.exists())
            folder.mkdir();

        String imageFullName = Static.getUid() + "." + imageName;
        Uri destinationUri = Uri.fromFile(new File(folder, imageFullName));

        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(100);

        // applying UI theme
        options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.colorPrimary));


        UCrop.of(uri, destinationUri)
                .withOptions(options)
                .withAspectRatio(1, 1)
                .withMaxResultSize(500, 500)
                .start(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSON_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            }
        }
    }
}