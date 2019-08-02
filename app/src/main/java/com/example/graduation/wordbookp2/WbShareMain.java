package com.example.graduation.wordbookp2;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WbShareMain extends AppCompatActivity implements WbShareAdapter.OnItemClickListener{
    private RecyclerView mRecyclerView;
    private WbShareAdapter mAdapter;

    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef; //DB주소
    private StorageReference mStorageRef;//스토리지 주소
    private ValueEventListener mDBListener;

    private List<WbShareUploadDB> mUploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wb_share_main);


        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mUploads = new ArrayList<>();

        mAdapter = new WbShareAdapter(WbShareMain.this, mUploads);

        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(WbShareMain.this);
        //파이어 베이스 저장소 위치들 설정
        mStorage = FirebaseStorage.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        //mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://fir-uploadexample-a1c6d.appspot.com");//


        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mUploads.clear(); // ???

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    WbShareUploadDB upload = postSnapshot.getValue(WbShareUploadDB.class);
                    upload.setKey(postSnapshot.getKey());
                    mUploads.add(upload);
                }

                mAdapter.notifyDataSetChanged();

                //mAdapter = new WbShareAdapter(WbShareMain.this, mUploads);

                //mRecyclerView.setAdapter(mAdapter);
                //mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(WbShareMain.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
               // mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });


        //업로드 버튼 클릭시 ->업로드 화면으로 이동
        Button GotoUpload = (Button)findViewById(R.id.wb_share_upload_main);
        GotoUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),WbShareUpload.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Normal click at position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWhatEverClick(int position) {
        Toast.makeText(this, "Whatever click at position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDownloadClick(int position) {
        WbShareUploadDB selectedItem = mUploads.get(position);
        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getmFileUrl());//위치 지정

        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {//이미지 URL을 받아옴
            @Override
            public void onSuccess(Uri uri) {
                //Toast.makeText(getApplicationContext(), "다운로드 성공 : "+ uri, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "다운로드 실패", Toast.LENGTH_SHORT).show();
            }
        });
        File localFile = null;
        try {
            localFile = File.createTempFile("images", ".xls", getExternalFilesDir(null));
        } catch (IOException e) {
            e.printStackTrace();
        }
        final File finalLocalFile = localFile;
        imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(), "파일 저장 성공", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), finalLocalFile.getPath()+"", Toast.LENGTH_SHORT).show();
                //System.out.println("getpath : " + finalLocalFile.getPath());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), "파일 저장 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDeleteClick(int position) {
        WbShareUploadDB selectedItem = mUploads.get(position);
        final String selectedKey = selectedItem.getKey();

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getmFileUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabaseRef.child(selectedKey).removeValue(); // 선택값 삭제
                Toast.makeText(WbShareMain.this, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }
}
