package com.example.btsqlite;

import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.btsqlite.Adapter.NoteAdapter;
import com.example.btsqlite.model.NotesModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DatabaseHandler databaseHandler;
    private ListView listView;
    private ArrayList<NotesModel> noteList;
    private NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHandler = new DatabaseHandler(this, "Notes.SQLite", null, 1);

        listView = findViewById(R.id.listView1);
        noteList = new ArrayList<>();
        noteAdapter = new NoteAdapter(this, R.layout.row_note, noteList);
        listView.setAdapter(noteAdapter);

        ImageView imageViewAdd = findViewById(R.id.imageViewAdd);
        imageViewAdd.setOnClickListener(v -> DialogThem());

        noteAdapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(int position) {
                NotesModel selectedNote = noteList.get(position);
                DialogCapNhat(selectedNote.getIdNote(), selectedNote.getNameNote());
            }

            @Override
            public void onDeleteClick(int position) {
                NotesModel selectedNote = noteList.get(position);
                DialogXoa(selectedNote.getIdNote());
            }
        });

        createDatabaseSQLite();
        InitDatabaseSQLite();
    }

    private void createDatabaseSQLite() {
        databaseHandler.queryData("CREATE TABLE IF NOT EXISTS Notes(id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR)");
    }

    private void InitDatabaseSQLite() {
        noteList.clear();
        Cursor cursor = null;
        try {
            cursor = databaseHandler.getData("SELECT * FROM Notes");
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                noteList.add(new NotesModel(id, name));
            }
            noteAdapter.notifyDataSetChanged();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menuAddNotes) {
            DialogThem();
        }
        return super.onOptionsItemSelected(item);
    }

    private void DialogThem() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_notes);

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

            layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
            layoutParams.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.32);
            layoutParams.gravity = Gravity.CENTER;

            window.setAttributes(layoutParams);
        }

        EditText edtText = dialog.findViewById(R.id.edtTextName);
        Button btnThem = dialog.findViewById(R.id.btnThem);
        Button btnHuy = dialog.findViewById(R.id.btnHuy);

        btnThem.setOnClickListener(v -> {
            String name = edtText.getText().toString().trim();
            if(name.isEmpty()) {
                Toast.makeText(MainActivity.this, "Vui lòng nhập Notes", Toast.LENGTH_SHORT).show();
            } else {
                databaseHandler.queryData("INSERT INTO Notes VALUES(null, '" + name + "')");
                Toast.makeText(MainActivity.this, "Đã thêm Notes", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                InitDatabaseSQLite();
            }
        });

        btnHuy.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    public void DialogCapNhat(int id, String currentName) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_update_notes);

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

            layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
            layoutParams.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.32);
            layoutParams.gravity = Gravity.CENTER;

            window.setAttributes(layoutParams);
        }

        EditText edtText = dialog.findViewById(R.id.edtTextName);
        Button btnCapNhat = dialog.findViewById(R.id.btnCapNhat);
        Button btnHuy = dialog.findViewById(R.id.btnHuy);

        edtText.setText(currentName);

        btnCapNhat.setOnClickListener(v -> {
            String name = edtText.getText().toString().trim();
            if(name.isEmpty()) {
                Toast.makeText(MainActivity.this, "Vui lòng nhập Notes", Toast.LENGTH_SHORT).show();
            } else {
                databaseHandler.queryData("UPDATE Notes SET name = '" + name + "' WHERE id = " + id);
                Toast.makeText(MainActivity.this, "Đã cập nhật Notes", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                InitDatabaseSQLite();
            }
        });

        btnHuy.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    public void DialogXoa(int id) {
        databaseHandler.queryData("DELETE FROM Notes WHERE id = " + id);
        Toast.makeText(this, "Đã xóa Notes", Toast.LENGTH_SHORT).show();
        InitDatabaseSQLite();
    }
}