package com.tyron.hanapbb.ui;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.tyron.hanapbb.R;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private TextView welcome_text;

    private TextInputEditText edittext_email;
    private TextInputEditText edittext_password;
    private TextInputEditText edittext_password_confirm;

    private Button button;
    private TextView register;
    private TextInputLayout textinput_email;
    private TextInputLayout textinput_password;
    private TextInputLayout textinput_password_confirm;
    private boolean isRegister = false;

    @Override
    protected void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);
        setContentView(R.layout.activity_login);
        init();
        textinput_password_confirm.setVisibility(View.GONE);
        register.setOnClickListener((view) ->{
            textinput_password_confirm.setVisibility(isRegister ? View.GONE : View.VISIBLE);
            button.setText(isRegister ? R.string.btn_login : R.string.btn_register);
            register.setText(isRegister ? R.string.text_register : R.string.text_login);
            welcome_text.setText(isRegister ? R.string.login_welcome : R.string.register_welcome );
            isRegister = !isRegister;
        });

        button.setOnClickListener((view)->{
            if(isFormsValid()) {
                if (!isRegister) {
                    auth.signInWithEmailAndPassword(Objects.requireNonNull(edittext_email.getText()).toString().trim(), Objects.requireNonNull(edittext_password.getText()).toString()).addOnCompleteListener(listener);
                } else {
                    auth.createUserWithEmailAndPassword(Objects.requireNonNull(edittext_email.getText()).toString().trim(), Objects.requireNonNull(edittext_password.getText()).toString()).addOnCompleteListener(listener);
                }
            }
        });
    }

    private boolean isFormsValid() {
        if(Patterns.EMAIL_ADDRESS.matcher(Objects.requireNonNull(edittext_email.getText())).matches()){
            textinput_email.setErrorEnabled(false);
            if(edittext_password.getText().length() > 7){
                textinput_password.setErrorEnabled(false);
                if(!isRegister){
                    return true;
                }else{
                    if(edittext_password.getText().toString().equals(edittext_password_confirm.getText().toString())){
                        textinput_password.setErrorEnabled(false);
                        return true;
                    }else{
                        textinput_password.setError(getString(R.string.passwords_must_match));
                    }
                }
            }else{
                textinput_password.setError(getString(R.string.invalid_password));
            }
        }else{
            textinput_email.setError(getString(R.string.invalid_email));
        }
        return false;
    }

    private void init() {
        auth = FirebaseAuth.getInstance();

        welcome_text = findViewById(R.id.textview_account_create);

        button = findViewById(R.id.button_male);
        register = findViewById(R.id.text_register);

        edittext_email = findViewById(R.id.edittext_email);
        edittext_password = findViewById(R.id.edittext_password);
        edittext_password_confirm = findViewById(R.id.edittext_password_confirm);

        textinput_email = findViewById(R.id.textinput_email);
        textinput_password = findViewById(R.id.textinput_password);
        textinput_password_confirm = findViewById(R.id.textinput_password_confirm);
    }

    OnCompleteListener<AuthResult> listener = new OnCompleteListener<AuthResult>() {
        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                startActivity(new Intent(LoginActivity.this, LaunchActivity.class));
                finish();
            } else {
                try{
                    throw task.getException();
                }catch(FirebaseAuthWeakPasswordException e) {
                    textinput_password.setError(getString(R.string.error_weak_password));
                    edittext_password.requestFocus();
                }catch(FirebaseAuthInvalidCredentialsException e){
                    textinput_email.setError(getString(R.string.error_invalid_credentials));
                    edittext_email.requestFocus();
                }catch(FirebaseAuthUserCollisionException e){
                    textinput_email.setError(getString(R.string.error_user_exists));
                    edittext_email.requestFocus();
                }catch(FirebaseAuthInvalidUserException e){
                    textinput_email.setError(getString(R.string.error_user_not_exist));
                    edittext_email.requestFocus();
                }catch(Exception e){
                    Toast.makeText(LoginActivity.this,e.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }
    };

}
