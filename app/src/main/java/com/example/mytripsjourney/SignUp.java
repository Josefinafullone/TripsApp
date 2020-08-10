package com.example.mytripsjourney;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class SignUp extends AppCompatActivity {

    public static final String TAG = "SignUp";
    Button btn_ir_inicioSesion,crear_cuenta;
    EditText et_nombre, et_telefono, et_correo, et_contraseña;
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = fStore.collection("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        btn_ir_inicioSesion=findViewById(R.id.btn_ir_inicioSesion);
        crear_cuenta=findViewById(R.id.crear_cuenta);
        et_nombre=(EditText)findViewById(R.id.et_nombre);
        et_telefono=(EditText)findViewById(R.id.et_telefono);
        et_correo=(EditText)findViewById(R.id.et_correo);
        et_contraseña=(EditText)findViewById(R.id.et_contrasena);

        if(fAuth.getCurrentUser() != null){

        }

        btn_ir_inicioSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUp.this,SignIn.class));
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        notebookRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    DocumentSnapshot documentSnapshot = dc.getDocument();
                    String id = documentSnapshot.getId();

                    switch (dc.getType()) {
                        case ADDED:
                            //   textViewData.append("\nAdded: " + id );
                            break;
                        case MODIFIED:
                            //    textViewData.append("\nModified: " + id );
                            break;
                        case REMOVED:
                            //   textViewData.append("\nRemoved: " + id );
                            break;
                    }
                }
            }
        });
    }

    public void crearCuenta (View view){
        final String email = et_correo.getText().toString().trim();
        String contraseña = et_contraseña.getText().toString().trim();
        final String name = et_nombre.getText().toString();
        final String number = et_telefono.getText().toString();

        if(TextUtils.isEmpty(email)){
            et_correo.setError("Email is Required.");
            return;
        }

        if(TextUtils.isEmpty(contraseña)){
            et_contraseña.setError("Password is Required.");
            return;
        }

        if(contraseña.length() < 6){
            et_contraseña.setError("Password Must be >= 6 Characters");
            return;
        }

        fAuth.createUserWithEmailAndPassword(email,contraseña).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    // send verification link

                    FirebaseUser fuser = fAuth.getCurrentUser();
                    fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(SignUp.this, "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Email not sent " + e.getMessage());
                        }
                    });

                    UserInfo user = new UserInfo(email, name, number);
                    notebookRef.document(email).set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });

                   // startActivity(new Intent(getApplicationContext(),MainAppActivity.class));
                    startActivity(new Intent(getApplicationContext(),TestActivity.class));

                }else {
                    Toast.makeText(SignUp.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}