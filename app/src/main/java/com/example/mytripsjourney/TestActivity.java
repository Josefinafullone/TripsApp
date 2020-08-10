package com.example.mytripsjourney;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class TestActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView textView;
    private FirebaseUser user;
    private FirebaseFirestore fStore;
    private CollectionReference notebookRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        imageView = findViewById(R.id.imagen_perfil);
        textView = findViewById(R.id.nombre_usuario);

        user = FirebaseAuth.getInstance().getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        notebookRef = fStore.collection("Users");
        Picasso.get().load(user.getPhotoUrl()).resize(200,200).into(imageView);
        if(user.getDisplayName() != null) {
            textView.setText(user.getDisplayName());
            Toast.makeText(this, "Dentro de display name no null", Toast.LENGTH_SHORT).show();
        }

    }

    public void logOut(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Are you sure you want to logout?");
        builder.setPositiveButton("Confirmar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();//logout
                        LoginManager.getInstance().logOut();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                    }
                });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }
}