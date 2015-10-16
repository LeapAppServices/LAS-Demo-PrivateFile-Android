package com.maxleap.demo.privatefile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.maxleap.LASLog;
import com.maxleap.LASPrivateFile;
import com.maxleap.LASPrivateFileManager;
import com.maxleap.callback.*;
import com.maxleap.exception.LASException;
import com.maxleap.utils.FileHandle;
import com.maxleap.utils.FileHandles;

import java.io.File;
import java.util.List;


public class PrivateFileActivity extends Activity {

    private static final String TAG = PrivateFileActivity.class.getName();

    private TextView contentTextView;
    private EditText uploadEditText;
    private EditText downloadEditText;
    private EditText getMetaDataEditText;
    private EditText createFolderEditText;
    private EditText moveFromEditText;
    private EditText moveToEditText;
    private EditText copyFromEditText;
    private EditText copyToEditText;
    private EditText deleteFileEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privatefile);

        contentTextView = (TextView) findViewById(R.id.content_text_view);
        uploadEditText = (EditText) findViewById(R.id.upload_edit_text);
        downloadEditText = (EditText) findViewById(R.id.download_edit_text);
        getMetaDataEditText = (EditText) findViewById(R.id.meta_data_edit_text);
        createFolderEditText = (EditText) findViewById(R.id.create_folder_edit_text);
        moveFromEditText = (EditText) findViewById(R.id.move_from_edit_text);
        moveToEditText = (EditText) findViewById(R.id.move_to_edit_text);
        copyFromEditText = (EditText) findViewById(R.id.copy_from_edit_text);
        copyToEditText = (EditText) findViewById(R.id.copy_to_edit_text);
        deleteFileEditText = (EditText) findViewById(R.id.delete_file_edit_text);

        findViewById(R.id.upload_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });

        findViewById(R.id.download_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFile();
            }
        });

        findViewById(R.id.create_folder_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFolder();
            }
        });

        findViewById(R.id.get_meta_data_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMetaData();
            }
        });

        findViewById(R.id.delete_file_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFile();
            }
        });

        findViewById(R.id.move_file_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveFile();
            }
        });
        findViewById(R.id.copy_file_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyFile();
            }
        });

        findViewById(R.id.get_usage_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUsage();
            }
        });

    }

    private void getUsage() {
        contentTextView.setText("statistics...");
        LASPrivateFileManager.getUsageInBackground(new GetUsageCallback() {
            @Override
            public void done(int fileNum, long fileCap, LASException exception) {
                if (exception != null) {
                    exception.printStackTrace();
                    contentTextView.setText(exception.getMessage());
                    return;
                }
                contentTextView.setText("fileNum is " + fileNum + "\nfileCap is " + fileCap);
            }
        });
    }

    private void copyFile() {
        contentTextView.setText("copy...");
        String fromPath = copyFromEditText.getText().toString();
        String toPath = copyToEditText.getText().toString();
        final LASPrivateFile src = LASPrivateFile.createFile(fromPath);
        final LASPrivateFile target = LASPrivateFile.createFile(toPath);
        LASPrivateFileManager.copyInBackground(src, target, false, new SaveCallback() {
            @Override
            public void done(LASException exception) {
                if (exception != null) {
                    exception.printStackTrace();
                    contentTextView.setText(exception.getMessage());
                    return;
                }

                contentTextView.setText("Copy file successfully");
            }
        });
    }

    private void moveFile() {
        contentTextView.setText("move...");
        String fromPath = moveFromEditText.getText().toString();
        String toPath = moveToEditText.getText().toString();
        final LASPrivateFile src = LASPrivateFile.createFile(fromPath);
        final LASPrivateFile target = LASPrivateFile.createFile(toPath);
        LASPrivateFileManager.moveInBackground(src, target, false, new SaveCallback() {
            @Override
            public void done(LASException exception) {
                if (exception != null) {
                    exception.printStackTrace();
                    contentTextView.setText(exception.getMessage());
                    return;
                }

                contentTextView.setText("Move file successfully");
            }
        });
    }

    private void deleteFile() {
        contentTextView.setText("delete...");
        String remotePath = deleteFileEditText.getText().toString();
        final LASPrivateFile privateFile = LASPrivateFile.createFile(remotePath);
        LASPrivateFileManager.deleteInBackground(privateFile, new DeleteCallback() {
            @Override
            public void done(LASException exception) {
                if (exception != null) {
                    exception.printStackTrace();
                    contentTextView.setText(exception.getMessage());
                    return;
                }

                contentTextView.setText("Delete file successfully");
            }
        });
    }

    private void getMetaData() {
        contentTextView.setText("get meta data...");
        String remotePath = getMetaDataEditText.getText().toString();
        final LASPrivateFile privateFile = LASPrivateFile.createDirectory(remotePath);
        LASPrivateFileManager.getMetaDataInBackground(privateFile, true, new GetMetaDataCallback() {
            @Override
            public void done(LASPrivateFile file, LASException exception) {
                if (exception != null) {
                    exception.printStackTrace();
                    contentTextView.setText(exception.getMessage());
                    return;
                }

                contentTextView.setText("Get meta data successfully");

                LASLog.d(TAG, "parent-->" + privateFile.toString());
                if (file.hasChildren()) {
                    List<LASPrivateFile> children = file.getChildren();
                    for (LASPrivateFile child : children) {
                        LASLog.d(TAG, "child-->" + child.toString());
                    }
                }
            }
        });
    }

    private void createFolder() {
        contentTextView.setText("create directory...");
        String remotePath = createFolderEditText.getText().toString();
        final LASPrivateFile privateFile = LASPrivateFile.createDirectory(remotePath);
        LASPrivateFileManager.createDirectoryInBackground(privateFile, new SaveCallback() {
            @Override
            public void done(LASException exception) {
                if (exception != null) {
                    exception.printStackTrace();
                    contentTextView.setText(exception.getMessage());
                    return;
                }
                contentTextView.setText("Create directory successfully");
                LASLog.d(TAG, privateFile.toString());
            }
        });
    }

    private void downloadFile() {
        contentTextView.setText("downloading...");
        String remotePath = downloadEditText.getText().toString();
        FileHandle target = FileHandles.sdcard("test_download.txt");
        final LASPrivateFile privateFile = LASPrivateFile.createFile(remotePath);
        final ProgressDialog progressDialog = createProgressDialog();
        LASPrivateFileManager.getDataWithPathInBackground(privateFile, target.getFile().getAbsolutePath(), new DownloadCallback() {
            @Override
            public void done(String path, LASException exception) {
                contentTextView.setText("");
                progressDialog.dismiss();
                if (exception != null) {
                    exception.printStackTrace();
                    contentTextView.setText(exception.getMessage());
                    return;
                }
                FileHandle handle = FileHandles.absolute(new File(path));
                contentTextView.setText("Download successfully at " + handle.getFile().getAbsolutePath() + "\n" + handle.tryReadString());

                LASLog.d(TAG, privateFile.toString());
            }
        }, new ProgressCallback() {
            @Override
            public void done(int percentDone) {
                progressDialog.setProgress(percentDone);
            }
        });
    }

    private void uploadFile() {
        contentTextView.setText("uploading...");
        String file = "test.txt";
        FileHandle source = FileHandles.assets(file);
        FileHandle target = FileHandles.sdcard(file);
        if (target.notExist()) {
            source.copyTo(target);
        }

        String remotePath = uploadEditText.getText().toString();
        final LASPrivateFile privateFile = LASPrivateFile.createFile(target.getFile().getAbsolutePath(), remotePath);
        final ProgressDialog progressDialog = createProgressDialog();
        LASPrivateFileManager.saveInBackground(privateFile, false, new SaveCallback() {
            @Override
            public void done(LASException exception) {
                contentTextView.setText("");
                progressDialog.dismiss();
                if (exception != null) {
                    exception.printStackTrace();
                    contentTextView.setText(exception.getMessage());
                    return;
                }
                contentTextView.setText("Upload successfully");

                LASLog.d(TAG, privateFile.toString());

            }
        }, new ProgressCallback() {
            @Override
            public void done(int percentDone) {
                progressDialog.setProgress(percentDone);
            }
        });
    }

    private ProgressDialog createProgressDialog() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgress(0);
        progressDialog.setMessage("Load...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                LASPrivateFileManager.cancel();
                dialog.cancel();
            }
        });
        progressDialog.show();
        return progressDialog;
    }
}
