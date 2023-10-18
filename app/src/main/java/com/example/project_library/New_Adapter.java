package com.example.project_library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class New_Adapter extends ArrayAdapter<String> {
    String boid, kid, collectionname;
    private List<String> pername;
    private List<String> pho;
    private List<String> retdate;
    private List<String> returnstatus;
    private Context context;


    public New_Adapter(@NonNull Context context, int resource, List<String> pername, List<String> pho, List<String> retdate, List<String> returnstatus, String boid, String collectionname) {
        super(context, resource);
        this.context = context;
        this.pername = pername;
        this.pho = pho;
        this.retdate = retdate;
        this.returnstatus = returnstatus;
        this.boid = boid;
        this.collectionname = collectionname;
    }


    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.rohit_layout, parent, false);
        ImageView imageView = convertView.findViewById(R.id.imageView5);
        TextView name = convertView.findViewById(R.id.name);
        TextView phone = convertView.findViewById(R.id.phocontact);
        TextView book_nametextview = convertView.findViewById(R.id.booknametextview);
        TextView redate = convertView.findViewById(R.id.dateofreturn);
        TextView returnstatus1 = convertView.findViewById(R.id.returnstatus);
        Button returnbtn = convertView.findViewById(R.id.returnbtn);

        FirebaseFirestore db = FirebaseFirestore.getInstance();


        // Retrive data from firestore
        FirebaseFirestore.getInstance().collection(collectionname+"Books").whereEqualTo("Book_Id", boid)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                String imguri = doc.getString("img_uri");
                                String name = doc.getString("Book_name");

                                returnbtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        db.collection(collectionname+"Book_Issue").whereEqualTo("Book_ID", boid)
                                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                                                String docid = doc.getId();
                                                                Map<String, Object> updates = new HashMap<>();
                                                                updates.put("Return_Status", "Returned");
                                                                db.collection(collectionname+"Book_Issue").document(docid)
                                                                        .update(updates)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                // Document updated successfully
                                                                                Toast.makeText(context, "Book returned", Toast.LENGTH_SHORT).show();
                                                                                Log.d("Firestore", "Document with student ID " + docid + " updated successfully.");
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                // Handle errors here
                                                                                Log.w("Firestore", "Error updating document", e);
                                                                            }
                                                                        });

                                                            }
                                                        }
                                                    }
                                                });
                                        returnstatus1.setText("Return status :  Returned");

                                    }
                                });

                                // Use Picasso to load and display the image
                                Picasso.get()
                                        .load(imguri)
                                        .into(imageView);


                                book_nametextview.setText("Book name :   " + name);
                            }
                        }
                    }
                });
        //end of code

        name.setText("Issued by :   " + pername.get(position));
        phone.setText("Contact :   " + pho.get(position));
        redate.setText("Return date :   " + retdate.get(position));
        returnstatus1.setText("Return status :   " + returnstatus.get(position));


        return convertView;
    }

    @Override
    public int getCount() {
        // Return the size of the data source
        return pername.size();
    }
}

