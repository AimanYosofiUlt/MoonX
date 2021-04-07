package com.ewu.moonx.UI.MainPkj;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.ewu.moonx.App.Firebase;
import com.ewu.moonx.App.Static;
import com.ewu.moonx.App.Status;
import com.ewu.moonx.Pojo.DB.FireBaseTemplate.Str;
import com.ewu.moonx.Pojo.DB.Tables.SettingTable;
import com.ewu.moonx.R;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.ChatActivity;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.PublicChatPkj.ServicesPkj.PublicService.PublicChatService;
import com.google.firebase.database.DatabaseReference;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class StartPublicChattingHandler {

    Activity activity;
    String id, name;

    public StartPublicChattingHandler(Activity activity) {
        this.activity = activity;
    }

    public void startChatting() {
        if (fileIsExists()) {
            getInfoFromTsetFile();
            showChatActivity();
        } else
            setTsetFile();
    }

    public void startChatting(String id, String name) {
        this.id = id;
        this.name = name;
        writeTsetFile();
        showChatActivity();
    }

    private void getInfoFromTsetFile() {
        File root = new File(Environment.getExternalStorageDirectory(), "DCIM");
        File file = new File(root, ".tset");
        try {
            FileInputStream fis = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            id = br.readLine();
            name = br.readLine();

            br.close();
            in.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setTsetFile() {
        if (Status.isNetConnected(activity)) {
            DatabaseReference refId = Firebase.RealTimeRef(Str.PublicMessages).child(Str.ForUsers).push();
            id = refId.getKey();
            showNameDialog();
        } else {
            Status.startShowBalloonMessage(activity, activity.findViewById(R.id.sendMsgsBtn), activity.getString(R.string.no_internet_connection));
        }
    }

    private void showNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Your name:");
        EditText nameEditText = new EditText(activity);
        builder.setView(nameEditText);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                name = nameEditText.getText().toString();
                writeTsetFile();
                showChatActivity();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void writeTsetFile() {
        File root = new File(Environment.getExternalStorageDirectory(), "DCIM");
        File file = new File(root, ".tset");
        try {
            FileWriter writer = new FileWriter(file);
            writer.append(id).append("\n").append(name);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            Toast.makeText(activity, "User NotGarnted", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private boolean fileIsExists() {
        File root = new File(Environment.getExternalStorageDirectory(), "DCIM");
        if (!root.exists()) {
            root.mkdir();
            return false;
        } else {
            File file = new File(root, ".tset");
            return file.exists();
        }
    }

    private void showChatActivity() {
        if (!PublicChatService.isRunning) {
            Intent serIntent = new Intent(activity, PublicChatService.class);
            serIntent.putExtra(Static.UserType, SettingTable.hisPublic);
            serIntent.putExtra(Static.UserId,id);
            activity.startService(serIntent);
        }

        Toast.makeText(activity, " PublicChatService is " + PublicChatService.isRunning, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra(Static.isPublicMsg, true);
        intent.putExtra(Static.UserId, id);
        intent.putExtra(Static.UserName, name);
        intent.putExtra(Static.UserType, SettingTable.hisPublic);
        activity.startActivity(intent);
    }
}
