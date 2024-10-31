package com.example.helloproject;


import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private static final int SMS_PERMISSION_CODE = 101;
    private TextView tvMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvNameAndId = findViewById(R.id.tvNameAndId);
        Button btnReadMessages = findViewById(R.id.btnReadMessages);
        tvMessages = findViewById(R.id.tvMessages);

        btnReadMessages.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                readMessages();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, SMS_PERMISSION_CODE);
            }
        });
    }

    private void readMessages() {
        Uri smsUri = Uri.parse("content://sms/inbox");
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(smsUri, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            StringBuilder smsBuilder = new StringBuilder();
            do {
                String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                smsBuilder.append("From: ").append(address).append("\nMessage: ").append(body).append("\n\n");
            } while (cursor.moveToNext());
            tvMessages.setText(smsBuilder.toString());
            cursor.close();
        } else {
            tvMessages.setText("Không có tin nhắn nào.");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readMessages();
            } else {
                Toast.makeText(this, "Quyền đọc tin nhắn bị từ chối!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
