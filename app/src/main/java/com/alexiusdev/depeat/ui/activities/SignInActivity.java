package com.alexiusdev.depeat.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;;

import com.alexiusdev.depeat.R;
import com.alexiusdev.depeat.datamodels.Restaurant;
import com.alexiusdev.depeat.datamodels.User;
import com.alexiusdev.depeat.services.RestController;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.alexiusdev.depeat.ui.Utility.*;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener, Response.Listener<String>, Response.ErrorListener{

    private EditText nameET;
    private EditText surnameET;
    private EditText emailET;
    private EditText passwordET;
    private EditText confirmPasswordET;
    private Button signInBtn;
    RestController restController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

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

        restController = new RestController(this);

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
            signInBtn.setEnabled(isValidEmail(emailET.getText().toString()) &&  //enable button only if the mail is valid and
                    !passwordET.getText().toString().isEmpty() &&   //password field isn't empty and
                    passwordET.getText().toString().equals(confirmPasswordET.getText().toString()) &&   //password and confirmpassword are equal
                    !nameET.getText().toString().isEmpty() &&   //name isn't empty and
                    !surnameET.getText().toString().isEmpty());    //surname isn't empty
        }
    };





    private void createAccount(final String email, final String password){
        String registrationEndpoint = "auth/local/register/";
        Map<String,String> body = new HashMap<>();
        body.put("username", email);
        body.put("email", email);
        body.put("password", password);
        restController.postRequest(registrationEndpoint, body, this,this);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        showToast(this, "Wops! Something went wrong!");
    }

    @Override
    public void onResponse(String response) {
        String session ="";
        String email = "";
        String id = "";
        try {
            JSONObject jsonResponse = new JSONObject(response);
            session = jsonResponse.getString("jwt");
            email = jsonResponse.getJSONObject("user").getString("email");
            id = jsonResponse.getJSONObject("user").getString("_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("postResponse",session);
        Log.d("postResponse",email);
        Log.d("postResponse",id);
        startActivity(new Intent(SignInActivity.this,LoginActivity.class));
    }
}
