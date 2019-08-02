package com.example.graduation.wordbookp2;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class WbShareUpload extends AppCompatActivity {

    private static final int PICK_File_REQUEST = 1;//업로드할 파일 수?

    private Button mButtonChooseFile;
    private Button mButtonUpload;
    private EditText mEditTextFileName;
    private EditText mEditTextFileKind;
    private EditText mEditTextFileExplain;

    private ProgressBar mProgressBar;

    private Uri mFileUri;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wb_share_upload);

        //각 오브젝트 설정
        mButtonChooseFile = findViewById(R.id.button_choose_wb); //업로드할 파일을 선택하는 버튼
        mButtonUpload = findViewById(R.id.button_file_upload);//업로드
        mEditTextFileName = findViewById(R.id.edit_text_file_name);
        mEditTextFileKind = findViewById(R.id.edit_text_kind);
        mEditTextFileExplain = findViewById(R.id.edit_text_explain);

       mProgressBar = findViewById(R.id.wb_share_progress_bar);


        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");//
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        //파일 선택 버튼 클릭
        mButtonChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openFileChooser();
                browseDocuments();
            }
        });

        //업로드 버튼 클릭
        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(WbShareUpload.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });
    }

    //파일 선택 화면으로
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");//파일 확장자 결정
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(intent, PICK_File_REQUEST);//
    }

    //파일 선택 화면에서 작업한 결과물을 이 액티비티로 가져옴
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_File_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mFileUri = data.getData();
        }
    }

    //뭔지 모름 아마 URL관련된 무언가임
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    //파일 업로드
    private void uploadFile() { // 파트3
        if (mFileUri != null) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mFileUri));

            mUploadTask = fileReference.putFile(mFileUri) // 이미ㅣ지 저장위치
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);//전송완료후 프로그레스바를 다시 0으로 초기화
                                }
                            }, 500);

                            Toast.makeText(WbShareUpload.this, "Upload successful", Toast.LENGTH_LONG).show();

                            //DB로 해당 데이터들을 전송
                            WbShareUploadDB upload = new WbShareUploadDB(mEditTextFileName.getText().toString().trim(),
                                    taskSnapshot.getDownloadUrl().toString(),mEditTextFileKind.getText().toString().trim(),
                                    mEditTextFileExplain.getText().toString().trim());
                            String uploadId = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(uploadId).setValue(upload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(WbShareUpload.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) { //진행상황?, 프로그레스바
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void browseDocuments(){

        String[] mimeTypes =
                {"application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                        "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                        "text/plain",
                        "application/pdf",
                        "application/zip"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0,mimeTypesStr.length() - 1));
        }
        startActivityForResult(Intent.createChooser(intent,"ChooseFile"),PICK_File_REQUEST);

    }

}
