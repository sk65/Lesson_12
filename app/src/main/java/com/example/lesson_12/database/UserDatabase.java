package com.example.lesson_12.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.lesson_12.dao.UserDao;
import com.example.lesson_12.database.entity.User;
import com.example.lesson_12.database.entity.UserLocation;

@Database(entities = {
        User.class,
        UserLocation.class},
        version = 2)
public abstract class UserDatabase extends RoomDatabase {

    private static final String DB_NAME = "user_database";
    private static volatile UserDatabase instance;

    public abstract UserDao userDao();

    public static synchronized UserDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    UserDatabase.class, DB_NAME)
                    .addMigrations(MIGRATION_1_2)
                    .build();
        }
        return instance;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE user_location ADD COLUMN userOwnerId ");

            database.execSQL("CREATE TABLE new_user(" +
                    "userId INTEGER PRIMARY KEY NOT NULL," +
                    "userName TEXT, " +
                    "userLastName TEXT," +
                    "email TEXT," +
                    "password TEXT," +
                    "imgUri TEXT)");

            database.execSQL("INSERT INTO new_user(userId, userName, userLastName, email, password, imgUri)" +
                    "SELECT userId, userName, userLastName, email, password, imgUri FROM user");

            database.execSQL("DROP TABLE user");
            database.execSQL("ALTER TABLE new_user RENAME TO user");

            database.execSQL("CREATE TABLE new_user_location(" +
                    "locationId INTEGER PRIMARY KEY NOT NULL," +
                    "userOwnerId INTEGER NOT NULL," +
                    "latitude REAL," +
                    "longitude REAL)");

            database.execSQL("INSERT INTO new_user_location(locationId, userOwnerId, latitude,longitude)" +
                    "SELECT locationId, userOwnerId, latitude,longitude FROM user_location");

            database.execSQL("DROP TABLE user_location");
            database.execSQL("ALTER TABLE new_user_location RENAME TO user_location");

        }
    };
}
