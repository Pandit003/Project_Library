package com.example.project_library;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Book extends AppCompatActivity {
    private  EditText editText;
    private ListView listViewbook;
    private Button button;
    FirebaseAuth auth;
    FirebaseUser user;
    String collectionname;
    
    private List<String> bname5=new ArrayList<>();
    private List<String> imageofbook=new ArrayList<>();
    private List<String> bavl=new ArrayList<>();
    private List<String> idofbook=new ArrayList<>();
    private List<String> documentid=new ArrayList<>();
    String idbook;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        listViewbook=findViewById(R.id.listviewbooks);
        editText=findViewById(R.id.editText2);
        button=findViewById(R.id.searchbuttonforbook);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        collectionname=user.getEmail();


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(collectionname+"Books").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    bname5.clear();
                    imageofbook.clear();
                    bavl.clear();
                    idofbook.clear();
                    documentid.clear();
                    for (DocumentSnapshot doc : task.getResult()) {
                        String nameofbook = doc.getString("Book_name");
                        String bookavailable = doc.getString("Book_avl");
                        String imageuri = doc.getString("img_uri");
                        String bookid123 = doc.getString("Book_Id");
                        String docid5 = doc.getId();
                        bname5.add(nameofbook);
                        bavl.add(bookavailable);
                        imageofbook.add(imageuri);
                        idofbook.add(bookid123);
                        documentid.add(docid5);


                    }
                    Book_Adapter ba = new Book_Adapter(Book.this, R.layout.bookview_layout, bname5, bavl, imageofbook, idofbook, documentid,collectionname);
                    listViewbook.setAdapter(ba);
                }
            }
        });




        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    idbook = editText.getText().toString();
                    auth = FirebaseAuth.getInstance();
                    user = auth.getCurrentUser();
                    collectionname=user.getEmail();
                    
                    if(idbook.isEmpty()){
                        Toast.makeText(Book.this, "Enter the book id", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection(collectionname + "Books").whereEqualTo("Book_Id", idbook).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if (task.isSuccessful()) {
                                    if (task.getResult().isEmpty()) {
                                        Toast.makeText(Book.this, "Book Not found", Toast.LENGTH_SHORT).show();
                                    }
                                    for (QueryDocumentSnapshot doc : task.getResult()) {

                                        bname5.clear();
                                        imageofbook.clear();
                                        bavl.clear();
                                        idofbook.clear();
                                        documentid.clear();
                                        String nameofbook = doc.getString("Book_name");
                                        String bookavailable = doc.getString("Book_avl");
                                        String imageuri = doc.getString("img_uri");
                                        String bookid123 = doc.getString("Book_Id");
                                        String docid5 = doc.getId();
                                        bname5.add(nameofbook);
                                        bavl.add(bookavailable);
                                        imageofbook.add(imageuri);
                                        idofbook.add(bookid123);
                                        documentid.add(docid5);

                                        Book_Adapter ba = new Book_Adapter(Book.this, R.layout.bookview_layout, bname5, bavl, imageofbook, idofbook, documentid, collectionname);
                                        listViewbook.setAdapter(ba);
                                    }
                                }

                            }
                        });
                    }

            }
        });



    }
}