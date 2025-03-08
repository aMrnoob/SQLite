package com.example.btsqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.btsqlite.model.NotesModel;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "NoteManager";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "Notes";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";

    public DatabaseHandler(@Nullable Context context, @Nullable String name,
                           @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_notes_table = String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT)",
                TABLE_NAME, KEY_ID, KEY_NAME);
        db.execSQL(create_notes_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String drop_notes_table = String.format("DROP TABLE IF EXISTS %s", TABLE_NAME);
        db.execSQL(drop_notes_table);
        onCreate(db);
    }

    public void queryData(String sql) {
        SQLiteDatabase database = getReadableDatabase();
        database.execSQL(sql);
    }

    public Cursor getData(String sql) {
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }

    public void insertNote(NotesModel note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues ct = new ContentValues();
        ct.put(KEY_NAME, note.getNameNote());

        db.insert(TABLE_NAME, null, ct);
        db.close();
    }

    public void updateNote(NotesModel note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, note.getNameNote());
        db.update(TABLE_NAME, values, KEY_ID + " = ?", new String[] { String.valueOf(note.getIdNote())});
        db.close();
    }

    public void deleteNote(int noteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + " = ?", new String[] {String.valueOf(noteId)});
        db.close();
    }

    public NotesModel cursorQuery(int noteId) {
        SQLiteDatabase sqlDB = this.getReadableDatabase();
        NotesModel note = null;
        Cursor cursor = sqlDB.query(TABLE_NAME, null, KEY_ID + " = ?", new String[]{String.valueOf(noteId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            note = new NotesModel(cursor.getInt(0), cursor.getString(1));
            cursor.close();
        }
        sqlDB.close();
        return note;
    }
}