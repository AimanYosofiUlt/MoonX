package com.ewu.moonx;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ewu.moonx.Pojo.DB.DBPkj.Executive.DB;
import com.ewu.moonx.Pojo.DB.Tables.TrainTable;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class TrainActvity extends AppCompatActivity {
    ImageView pic;
    ImageView pic2;
    Button button;
    Button button2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train);
        pic = findViewById(R.id.pic);
        pic2 = findViewById(R.id.pic2);
        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);

        button.setOnClickListener(v -> {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Bitmap bitmap = ((BitmapDrawable) pic.getDrawable()).getBitmap();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                byte[] byteArray = outputStream.toByteArray();
                String string = Base64.getEncoder().encodeToString(byteArray);
                TrainTable table = new TrainTable(TrainActvity.this);
                DB.insert(table.image, string).inTo(table);
            }
        });

        button2.setOnClickListener(v -> {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                TrainTable table = new TrainTable(TrainActvity.this);
                Cursor cursor = DB.select(table.image).from(table).start();
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        byte[] byteArray = Base64.getDecoder().decode(cursor.getString(0));
                        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                        pic2.setImageBitmap(bitmap);
                    }
                }
            }
        });
    }

    public byte[] getUByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }

}