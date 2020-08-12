package com.example.mytripsjourney;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity implements TextWatcher,

        CompoundButton.OnCheckedChangeListener{
    Button btn_ir_crearCuenta,iniciar_sesion;
    EditText et_correo, et_contraseña;
    FirebaseAuth mAuth;
    TextView forgotTextLink;
    CheckBox checkBox;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private static final String PREF_NAME ="prefs";
    private static final String KEY_REMEMBER = "remember";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASS = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        et_correo = (EditText)findViewById(R.id.et_correo);
        et_contraseña=(EditText)findViewById(R.id.et_contrasena);
        btn_ir_crearCuenta=findViewById(R.id.btn_ir_crearCuenta);
        iniciar_sesion=findViewById(R.id.iniciar_sesion);
        forgotTextLink = findViewById(R.id.et_contrasena_olvidada);
        checkBox = findViewById(R.id.checkBoxUser);

        mAuth = FirebaseAuth.getInstance();

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(sharedPreferences.getBoolean(KEY_REMEMBER, false)){
            checkBox.setChecked(true);
        } else checkBox.setChecked(false);

        et_correo.setText(sharedPreferences.getString(KEY_USERNAME,""));
        et_contraseña.setText(sharedPreferences.getString(KEY_PASS,""));

        et_correo.addTextChangedListener(this);
        et_contraseña.addTextChangedListener(this);
        checkBox.setOnCheckedChangeListener(this);

        iniciar_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = et_correo.getText().toString().trim();
                String contraseña = et_contraseña.getText().toString().trim();

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

                mAuth.signInWithEmailAndPassword(email,contraseña).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SignIn.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainAppActivity.class));
                            //startActivity(new Intent(getApplicationContext(),TestActivity.class));
                        }else{
                            Toast.makeText(SignIn.this,"Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        btn_ir_crearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignIn.this,SignUp.class);
                startActivity(intent);
                finish();
            }
        });

        forgotTextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText resetMail = new EditText(v.getContext());
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password ?");
                passwordResetDialog.setMessage("Enter Your Email To Received Reset Link.");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // extract the email and send reset link
                        String mail = resetMail.getText().toString();
                        mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(SignIn.this, "Reset Link Sent To Your Email.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignIn.this, "Error ! Reset Link is Not Sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close the dialog
                    }
                });

                passwordResetDialog.create().show();

            }
        });



    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        managePrefs();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private void managePrefs(){
        if(checkBox.isChecked()){
            editor.putString(KEY_USERNAME,et_correo.getText().toString().trim());
            editor.putString(KEY_PASS,et_contraseña.getText().toString().trim());
            editor.putBoolean(KEY_REMEMBER,true);
        }else{
            editor.putBoolean(KEY_REMEMBER,false);
            editor.remove(KEY_USERNAME);
            editor.remove(KEY_PASS);
        }
        editor.apply();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        managePrefs();
    }
}