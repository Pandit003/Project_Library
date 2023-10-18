package com.example.project_library;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

public class Admin extends AppCompatActivity {
    private static final int IMAGE_REQUEST = 2;
    private Uri imageUri;
    String docid;
    TextView textView,showemail;
    ImageView addbook,viewbook,bissue,book_returnbtn,profileimage;
    FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        textView=findViewById(R.id.logout);
        addbook=findViewById(R.id.addbook);
        book_returnbtn=findViewById(R.id.book_returnbtn);
        viewbook=findViewById(R.id.viewbook);
        bissue=findViewById(R.id.bissue);
        profileimage=findViewById(R.id.profileimage);

        mAuth=FirebaseAuth.getInstance();
        String uid=mAuth.getCurrentUser().getEmail();
        showemail=findViewById(R.id.showemail);
        showemail.setText(uid);


        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection(uid+"Book_Issue").whereEqualTo("Profile_uid",uid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()) {

                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        String imageuri = doc.getString("Profile_uri");
                        Picasso.get()
                                .load(imageuri)
                                .into(profileimage);
                    }
                }
            }
        });

        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });




        book_returnbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Book_Return.class);
                startActivity(intent);
            }
        });



        bissue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Book_Issue.class);
                startActivity(intent);
            }
        });



        addbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddBooks.class);
                startActivity(intent);
            }
        });

        viewbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Book.class);
                startActivity(intent);
            }
        });

    }




    private void openImage() {
        Intent intent=new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGE_REQUEST && resultCode==RESULT_OK){
            imageUri=data.getData();

            uploadImage();
        }
    }

    private String getFileExtension (Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadImage() {
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();

        if(imageUri!=null){
            StorageReference fileRef= FirebaseStorage.getInstance().getReference().child("uploads").child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
            fileRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@androidx.annotation.NonNull Task<UploadTask.TaskSnapshot> task) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            mAuth=FirebaseAuth.getInstance();
                            String uid=mAuth.getCurrentUser().getEmail();

                            FirebaseFirestore.getInstance().collection(uid+"Book_Issue").whereEqualTo("Profile_uid", uid)
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                                     docid = doc.getId();
                                                }

                                                if(task.getResult().isEmpty()) {
                                                    Map<String, Object> data = new HashMap<>();
                                                    data.put("Profile_uri", uri);
                                                    data.put("Profile_uid", uid);
                                                    FirebaseFirestore.getInstance().collection(uid+"Book_Issue").document().set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                pd.dismiss();
                                                                profileimage.setImageURI(imageUri);
                                                                Toast.makeText(Admin.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                }
                                                else {
                                                    Map<String, Object> updates = new HashMap<>();
                                                    updates.put("Profile_uri", uri);
                                                    FirebaseFirestore.getInstance().collection(uid + "Book_Issue").document(docid)
                                                            .update(updates)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    // Document updated successfully
                                                                    pd.dismiss();
                                                                    profileimage.setImageURI(imageUri);
                                                                    Toast.makeText(Admin.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                                                                    Log.d("Firestore", "Document with student ID " + docid + " updated successfully.");
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@androidx.annotation.NonNull Exception e) {
                                                                    // Handle errors here
                                                                    Log.w("Firestore", "Error updating document", e);
                                                                }
                                                            });
                                                }


                                            }
                                        }
                                    });

                        }
                    });
                }
            });
        }
    }


}