package com.example.project_library;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Book_Return extends AppCompatActivity {
    ListView listView;
    EditText searchbookid;
    Button searchbtn;
    private FirebaseAuth auth;
    private FirebaseUser user;
    String personname,personcontact,returndate,retstatus,collectionname,book_id1,book_nametextview,book_imagetextview;
    private List<String> pername = new ArrayList<>(); // Initialize lists to store data
    private List<String> pho = new ArrayList<>();
    private List<String> retdate = new ArrayList<>();

    private List<String> returnstatus = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_return);
        listView=findViewById(R.id.listviewbooks);
        searchbookid=findViewById(R.id.searchbook);
        searchbtn=findViewById(R.id.searchbutton);
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        collectionname=user.getEmail();




        FirebaseFirestore.getInstance().collection(collectionname+"Book_Issue").whereEqualTo("Return_Status", "Not returned")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            pername.clear();
                            pho.clear();
                            retdate.clear();
                            returnstatus.clear();
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                personname = doc.getString("Person_name");
                                personcontact = doc.getString("Contact");
                                returndate = doc.getString("Return_date");
                                retstatus = doc.getString("Return_Status");
                                String boid= doc.getString("Book_ID");
                                String collectionname=user.getEmail();


                                pername.add(personname);
                                pho.add(personcontact);
                                retdate.add(returndate);
                                returnstatus.add(retstatus);
                                New_Adapter ra = new New_Adapter(Book_Return.this, R.layout.rohit_layout, pername, pho, retdate,returnstatus,boid,collectionname);
                                listView.setAdapter(ra);



                            }
                        }
                    }
                });


        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String boid=searchbookid.getText().toString();
                if(boid.isEmpty()){
                    Toast.makeText(Book_Return.this, "Enter book id", Toast.LENGTH_SHORT).show();
                }
                else {
                    FirebaseFirestore.getInstance().collection(collectionname + "Book_Issue").whereEqualTo("Book_ID", boid)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().isEmpty()) {
                                            Toast.makeText(Book_Return.this, "Book not found", Toast.LENGTH_SHORT).show();
                                        }
                                        pername.clear();
                                        pho.clear();
                                        retdate.clear();
                                        returnstatus.clear();
                                        for (QueryDocumentSnapshot doc : task.getResult()) {
                                            personname = doc.getString("Person_name");
                                            personcontact = doc.getString("Contact");
                                            returndate = doc.getString("Return_date");
                                            retstatus = doc.getString("Return_Status");

                                            pername.add(personname);
                                            pho.add(personcontact);
                                            retdate.add(returndate);
                                            returnstatus.add(retstatus);
                                            New_Adapter ra = new New_Adapter(Book_Return.this, R.layout.rohit_layout, pername, pho, retdate, returnstatus, boid, collectionname);
                                            listView.setAdapter(ra);


                                        }
                                    }
                                }
                            });
                }



            }
        });

    }
}