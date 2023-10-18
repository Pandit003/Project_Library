package com.example.project_library;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddBooks extends AppCompatActivity {
    private static final int IMAGE_REQUEST = 2;
    private Button addbookbtn;
    private EditText bookid,bname,bavl;
    private TextView uploadimg;
    private ImageView bookimage;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private Uri imageUri;
    int count,count1=0;
    String imageName,collectionname;
    Uri uri1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_books);
        addbookbtn = findViewById(R.id.button);
        bname = findViewById(R.id.bname);
        bookid = findViewById(R.id.bookid);
        bavl = findViewById(R.id.bavl);
        bookimage = findViewById(R.id.bookimage);
        uploadimg = findViewById(R.id.uploadimg);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        collectionname=user.getEmail();

        uploadimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count=1;
                openImage();
            }
        });


        addbookbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(count1==1){
                    Toast.makeText(AddBooks.this, "Already Exist", Toast.LENGTH_SHORT).show();
                }
                else {
                    String bookname = bname.getText().toString();
                    String bookavl = bavl.getText().toString();
                    String bid = bookid.getText().toString();

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        Map<String, Object> data = new HashMap<>();
                        data.put("Book_name", bookname);
                        data.put("Book_avl", bookavl);
                        data.put("Book_Id", bid);
                        data.put("img_uri", uri1);
                        if (bookname.isEmpty() || bookavl.isEmpty() || bid.isEmpty()) {
                            Toast.makeText(AddBooks.this, "Fill up all details", Toast.LENGTH_SHORT).show();
                        } else if (!containletterwithspace(bookname)) {
                            Toast.makeText(AddBooks.this, "Book name write in alphabet", Toast.LENGTH_SHORT).show();
                        } else {
                            if (count == 1) {

                                db.collection(collectionname+"Books").whereEqualTo("Book_Id",bid) .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                                        if (task.getResult().isEmpty()) {
                                                db.collection(collectionname+"Books").document().set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            count1 = 1;
                                                            Toast.makeText(AddBooks.this, "Book added", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                        } else {
                                                Toast.makeText(AddBooks.this, "This id already register", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });

                            } else {
                                Toast.makeText(AddBooks.this, "Upload an Image", Toast.LENGTH_SHORT).show();
                            }
                        }

                }
            }
        });


    }


    public static boolean containletterwithspace(String str){
        String regex="^[a-zA-Z\\s]+$";
        Pattern p=Pattern.compile(regex);
        Matcher m=p.matcher(str);
        return m.matches();
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
                            uri1=uri;
                            imageName = fileRef.getName();
                            bookimage.setImageURI(imageUri);
                            uploadimg.setText(imageName);
                            pd.dismiss();
                        }
                    });
                }
            });
        }
    }
}