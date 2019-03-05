package com.yuyuforest.a3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Storage1NoteActivity extends AppCompatActivity {
    private EditText fileText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage1_note);

        fileText = findViewById(R.id.fileText);
    }

    public void onSaveClick(View view) {
        try (FileOutputStream fileOutputStream = openFileOutput(getString(R.string.file_name), MODE_PRIVATE)) {
            fileOutputStream.write(fileText.getText().toString().getBytes());
            Toast.makeText(Storage1NoteActivity.this, "Save successfully.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(Storage1NoteActivity.this, "Fail to save file.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onLoadClick(View view) {
        try (FileInputStream fileInputStream = openFileInput(getString(R.string.file_name))) {
            byte[] contents = new byte[fileInputStream.available()];
            fileInputStream.read(contents);
            String text = new String(contents);
            fileText.setText(text);
            Toast.makeText(Storage1NoteActivity.this, "Load successfully.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(Storage1NoteActivity.this, "Fail to load file.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClearClick(View view) {
        fileText.setText("");
    }
}
