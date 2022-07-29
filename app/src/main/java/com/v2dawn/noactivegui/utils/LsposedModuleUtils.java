package com.v2dawn.noactivegui.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

public class LsposedModuleUtils {

    public static final String LSP_MODULES_DB = "/data/adb/lspd/config/modules_config.db";
    public static final String LSP_MODULES_DB_READ_PATH = "lsp.db";

    public static final String MODULE_NAME_COLUMN = "module_pkg_name";
    public static final String MODULE_ENABLE_COLUMN = "enabled";
    public static final String MODULE_TABLE_NAME = "modules";

    public static final String MODULE_QUERY_DB = "select * from %s where %s = '%s'";

    public static Boolean detect(Context context, String moduleName) {
        File privateLspModuleFile = context.getDatabasePath(LSP_MODULES_DB_READ_PATH);

        //cp new
        SuTool.copyFile(LSP_MODULES_DB, privateLspModuleFile.getAbsolutePath());
        SuTool.setFileRead(privateLspModuleFile.getAbsolutePath());

        SQLiteDatabase db = SQLiteDatabase.openDatabase(privateLspModuleFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);

        String sql = String.format(MODULE_QUERY_DB, MODULE_TABLE_NAME, MODULE_NAME_COLUMN, moduleName);
        Cursor cursor = db.rawQuery(sql, null);

        while (cursor.moveToNext()) {
            @SuppressLint("Range")
            String enabled = cursor.getString(cursor.getColumnIndex(MODULE_ENABLE_COLUMN));
            if ("1".equals(enabled)) {
                opClear(privateLspModuleFile.getAbsolutePath(), cursor, db);
                return true;
            }
        }
        opClear(privateLspModuleFile.getAbsolutePath(), cursor, db);
        return false;
    }

    private static void opClear(String fileName, Cursor cursor, SQLiteDatabase sqLiteDatabase) {
        SuTool.removeFile(fileName);
        cursor.close();
        sqLiteDatabase.close();
    }
}
