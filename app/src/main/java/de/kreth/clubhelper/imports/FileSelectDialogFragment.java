package de.kreth.clubhelper.imports;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.kreth.clubhelper.R;

/**
 * Created by markus on 08.03.15.
 */
public class FileSelectDialogFragment {
    private Context context;
    private File currentDir;
    private FileSelectListener listener;

    public FileSelectDialogFragment(Context context, FileSelectListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void showSelectDialog(File targetDir) {
        if(targetDir == null)
            targetDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        currentDir = targetDir;

        List<String> list = new ArrayList<>(Arrays.asList(targetDir.list()));
        list.add(0, "..");
        String[] items = new String[list.size()];
        list.toArray(items);
        new AlertDialog.Builder(context)
                .setTitle(targetDir.getPath())
                .setItems(items, new FileNameSelectListener())
                .setNeutralButton(R.string.lblCancel, null)
                .show();
    }

    private class FileNameSelectListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(which == 0) {
                showSelectDialog(currentDir.getParentFile());
            }else {

                File file = currentDir.listFiles()[which-1];
                if(file.isDirectory())
                    showSelectDialog(file);
                else {
                    listener.fileSelected(file);
                }
            }
        }
    }

    public interface FileSelectListener {
        void fileSelected(File selected);
    }
}