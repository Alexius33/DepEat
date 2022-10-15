package com.alexiusdev.depeat.ui.activities;

import static com.alexiusdev.depeat.ui.Utility.EMAIL_KEY;
import static com.alexiusdev.depeat.ui.Utility.MIN_LENGTH_PSW;
import static com.alexiusdev.depeat.ui.Utility.isValidEmail;
import static com.alexiusdev.depeat.ui.Utility.showToast;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alexiusdev.depeat.R;
import com.alexiusdev.depeat.datamodels.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SignInActivity extends AppCompatActivity implements View.OnClickListener/*, Response.Listener<String>, Response.ErrorListener*/ {

    private EditText nameET;
    private EditText surnameET;
    private EditText emailET;
    private EditText passwordET;
    private EditText confirmPasswordET;
    private Button signInBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        mAuth = FirebaseAuth.getInstance();

        nameET = findViewById(R.id.name_et);
        surnameET = findViewById(R.id.surname_et);
        emailET = findViewById(R.id.email_et);
        passwordET = findViewById(R.id.password_et);
        confirmPasswordET = findViewById(R.id.confirm_password_et);
        signInBtn = findViewById(R.id.signin_btn);

        signInBtn.setOnClickListener(this);

        nameET.addTextChangedListener(signInButtonTextWatcher);
        surnameET.addTextChangedListener(signInButtonTextWatcher);
        emailET.addTextChangedListener(signInButtonTextWatcher);
        passwordET.addTextChangedListener(signInButtonTextWatcher);
        confirmPasswordET.addTextChangedListener(signInButtonTextWatcher);

        if(isValidEmail(getIntent().getStringExtra(EMAIL_KEY)))
            emailET.setText(getIntent().getStringExtra(EMAIL_KEY));
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case (R.id.signin_btn):
                createAccount(emailET.getText().toString(),passwordET.getText().toString());
                break;
        }
    }

    private TextWatcher signInButtonTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        @Override
        public void afterTextChanged(Editable editable) {
            boolean check = isValidEmail(emailET.getText().toString()) &&  //enable button only if the mail is valid and
                    !passwordET.getText().toString().isEmpty() &&   //password field isn't empty and
                    passwordET.getText().toString().equals(confirmPasswordET.getText().toString()) &&   //password and confirmPassword are equal
                    !nameET.getText().toString().isEmpty() &&   //name isn't empty and
                    !surnameET.getText().toString().isEmpty() &&  //surname isn't empty
                    passwordET.getText().toString().length() >= MIN_LENGTH_PSW;
            signInBtn.setEnabled(check);
            int colour = check ? getApplication().getResources().getColor(R.color.primary_text) : getApplication().getResources().getColor(R.color.disabled_text);
            signInBtn.setTextColor(colour);
        }
    };

    private void createAccount(final String email, final String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                //Sign in success, update UI with the signed-in user's information
                Log.d("TAG", "createUserWithEmail:success");
                FirebaseUser user = mAuth.getCurrentUser();
                new User(email,"", nameET.getText().toString(), surnameET.getText().toString());
                finish();
            } else {
                //If sign in fails, display a message to the user
                Log.w("TAG", "createUserWithEmail:failure", task.getException());
                showToast(SignInActivity.this, "Authentication failed.");
            }
        });
/*

        RestController restController = new RestController(this);
        String endPoint = "auth/local/register/";
        Map<String, String> mapBody = new HashMap<>();
        mapBody.put("username",email);
        mapBody.put("email", email);
        mapBody.put("password", password);
        JSONObject body = new JSONObject(mapBody);
        restController.postRequest(endPoint,body,this,this);*/
    }

    /*@Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(String response) {

    }*/
}
