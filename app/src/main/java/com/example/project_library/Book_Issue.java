package com.example.project_library;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;


import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Book_Issue extends AppCompatActivity {
    private Button rdate, iss;
    private TextView issuedate, returndate, bookname;
    private EditText pname, contact, bokid;
    private ImageView image;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private Calendar calendar = Calendar.getInstance();
    String bid, personname, pcontact, issuedate1, returndate1,collectionname;
    int count = 0;
    long con;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_issue);
        pname = findViewById(R.id.pname);
        contact = findViewById(R.id.contact);
        bokid = findViewById(R.id.bokid);
        rdate = findViewById(R.id.rdate);
        issuedate = findViewById(R.id.issuedate);
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());    // use to get current date
        issuedate.setText(date);
        returndate = findViewById(R.id.returndate);
        bookname = findViewById(R.id.bookname);
        iss = findViewById(R.id.iss);
        image = findViewById(R.id.image);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        collectionname=user.getEmail();

        FirebaseFirestore db=FirebaseFirestore.getInstance();


        rdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(returndate);
            }
        });


//        idate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showDatePickerDialog(issuedate);
//            }
//        });


        iss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count == 1) {
                    Toast.makeText(Book_Issue.this, "Go back and try again", Toast.LENGTH_LONG).show();
                } else {
                    personname = pname.getText().toString();
                    pcontact = contact.getText().toString();

                    if (!pcontact.isEmpty()) {
                        con = Long.parseLong(String.valueOf(pcontact));
                    } else
                        con = 0;
                    bid = bokid.getText().toString();
                    issuedate1 = issuedate.getText().toString();
                    returndate1 = returndate.getText().toString();
                    db.collection(collectionname+"Books") .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                            if (task.getResult().isEmpty()) {
                                    Toast.makeText(Book_Issue.this, "No book is exist", Toast.LENGTH_SHORT).show();
                                }
                            else {
                                db.collection(collectionname+"Books").whereEqualTo("Book_Id", bid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (personname.isEmpty() || pcontact.isEmpty() || bid.isEmpty()  || returndate1.isEmpty()) {
                                                Toast.makeText(Book_Issue.this, "Fill up all details", Toast.LENGTH_SHORT).show();
                                            }
                                            else if (!task.getResult().isEmpty()) {
                                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                Map<String, Object> data = new HashMap<>();
                                                data.put("Person_name", personname);
                                                data.put("Contact", pcontact);
                                                data.put("Book_ID", bid);
                                                data.put("Issue_date", issuedate1);
                                                data.put("Return_date", returndate1);
                                                data.put("Return_Status", "Not returned");
                                                if (!person_name(personname)) {
                                                    Toast.makeText(Book_Issue.this, "Enter Valid name", Toast.LENGTH_SHORT).show();
                                                } else if (con < 999999999 || con > 9999999999L) {
                                                    Toast.makeText(Book_Issue.this, "Enter valid Contact details", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    count++;
                                                    Toast.makeText(Book_Issue.this, "Book issued", Toast.LENGTH_SHORT).show();
                                                    db.collection(collectionname+"Book_Issue").document().set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                // Retrive data from firestore

                                                                FirebaseFirestore.getInstance().collection(collectionname+"Books").whereEqualTo("Book_Id", bid)
                                                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    for (QueryDocumentSnapshot doc : task.getResult()) {
                                                                                        String imguri = doc.getString("img_uri");
                                                                                        String name = doc.getString("Book_name");


                                                                                        String bookavailable = doc.getString("Book_avl");
                                                                                        String documentidofbook = doc.getId();
                                                                                        int noofbook = Integer.parseInt(String.valueOf(bookavailable));
                                                                                        noofbook = noofbook - 1;
                                                                                        Map<String, Object> updates = new HashMap<>();
                                                                                        updates.put("book_avl", Integer.toString(noofbook));
                                                                                        db.collection("Book_Issue").document(documentidofbook)
                                                                                                .update(updates);


                                                                                        // Use Picasso to load and display the image
                                                                                        Picasso.get()
                                                                                                .load(imguri)
                                                                                                .into(image);


                                                                                        bookname.setText(name);
                                                                                    }
                                                                                }
                                                                            }
                                                                        });

                                                                //end of code
                                                            }
                                                        }
                                                    });
                                                }
                                            } else {
                                                Toast.makeText(Book_Issue.this, "This Book not exist", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                    }
                                });
                            }
                        }
                    });
                }
            }

        });


    }


    public static boolean person_name(String str) {
        String regex = "^[a-zA-Z\\s]+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        return m.matches();
    }


    private void showDatePickerDialog(TextView tv) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        // Update the selected date in your TextView or wherever you want to display it.
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        String selectedDate = dateFormat.format(calendar.getTime());
                        tv.setText(selectedDate);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);   //not pick the date before today date
        datePickerDialog.show();
    }
}