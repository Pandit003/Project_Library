package com.example.project_library;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Book_Adapter extends ArrayAdapter<String> {
    private List<String> bname5;
    private List<String> imageuri5;
    private List<String> bavl;
    private List<String> idofbook;
    private List<String> documentid;
    private Context context;
    String collectionname;
    public Book_Adapter(@NonNull Context context, int resource, @NonNull List<String> bname5,List<String> bavl,List<String> imageuri5,List<String>idofbook,List<String> documentid,String collectionname) {
        super(context, resource, bname5);
        this.context = context;
        this.bname5=bname5;
        this.idofbook=idofbook;
        this.bavl=bavl;
        this.imageuri5=imageuri5;
        this.documentid=documentid;
        this.collectionname=collectionname;
    }
    @NonNull
    @SuppressLint({"ViewHolder", "SetTextI18n"})
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.bookview_layout, parent, false);
        TextView textView=convertView.findViewById(R.id.textView);
        TextView textView1=convertView.findViewById(R.id.textView1);
        TextView textView2=convertView.findViewById(R.id.textView2);
        ImageView imageView=convertView.findViewById(R.id.imageView);
        ImageView editdetail=convertView.findViewById(R.id.editdetails);
        ImageView deletebook=convertView.findViewById(R.id.deletebook);


        textView.setText("Book name :   " + bname5.get(position));
        textView1.setText("Book id :   " + idofbook.get(position));
        textView2.setText("Book avl :   " + bavl.get(position));
        String imageofabook=imageuri5.get(position);
        Picasso.get()
                .load(imageofabook)
                .into(imageView);



        editdetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String collectionname=auth.getCurrentUser().getEmail();
                String docid=documentid.get(position);
                FirebaseFirestore db=FirebaseFirestore.getInstance();
                db.collection(collectionname+"Books").document(docid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Intent intent = new Intent(context, AddBooks.class);
                        context.startActivity(intent);
                    }
                });
            }
        });

        deletebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String collectionname=auth.getCurrentUser().getEmail();
                String docid=documentid.get(position);
                FirebaseFirestore db=FirebaseFirestore.getInstance();
                db.collection(collectionname+"Books").document(docid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Book Deleted", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return convertView;
    }
    @Override
    public int getCount() {
        // Return the size of the data source
        return bname5.size();
    }
}
