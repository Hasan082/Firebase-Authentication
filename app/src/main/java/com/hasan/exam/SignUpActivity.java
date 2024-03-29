package com.hasan.exam;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {
    EditText fullName, username, password;
    Button signup;
    TextView gotoLogin;
    FirebaseAuth mAuth;
    DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        fullName = findViewById(R.id.fullName);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        signup = findViewById(R.id.signup);
        gotoLogin = findViewById(R.id.gotoLogin);
        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference("UserDetails");

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.createUserWithEmailAndPassword(username.getText().toString().trim(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            String userRecord = mAuth.getUid();
                            myRef.child(userRecord).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    snapshot.child("name").getRef().setValue(fullName.getText().toString().trim());
                                    Toast.makeText(SignUpActivity.this, "Signup Success!", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }else {
                            Exception exception = task.getException();
                            assert exception != null;
                            Toast.makeText(SignUpActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();

                            if (exception instanceof FirebaseAuthUserCollisionException) {
                                Log.e("SignUpExcept", "Failed to sign up: " + exception.getMessage());
                            } else {
                                Log.e("SignUpError", "Failed to sign up: " + exception.getMessage());
                            }
                        }
                    }
                });
            }
        });

        gotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });

    }
}