package pe.usil.android.agenda;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    public static final int SIGN_IN_GOOGLE_CODE = 5;

    public static final int BD_IS_LOGGED_BY_GOOGLE = 10;
    public static final int BD_IS_LOGGED_BY_FACEBOOK = 11;

    private GoogleApiClient googleApiClient;
    private CallbackManager callbackManager;

    private ProgressBar progBarLoadSingIn;
    private SignInButton btnGoogleSignIn;
    private LoginButton btnFacebookSignIn;

    private FirebaseAuth mAuth;
    //FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        callbackManager = CallbackManager.Factory.create();

        progBarLoadSingIn = (ProgressBar) findViewById(R.id.progBarLoadSingIn);

        btnGoogleSignIn = (SignInButton) findViewById(R.id.btnGoogleSignIn);

        btnFacebookSignIn = (LoginButton) findViewById(R.id.btnFacebookSignIn);
        btnFacebookSignIn.setReadPermissions(Arrays.asList("email"));


        btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });
        btnFacebookSignIn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Inicio de sesión cancelado", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "Error al iniciar sesión", Toast.LENGTH_LONG).show();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        //currentUser = mAuth.getCurrentUser();
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        //linkCredentialToUser(credential);

        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        Toast.makeText(getApplicationContext(), "Iniciando con Facebook.", Toast.LENGTH_LONG).show();
                        goToPrincipal();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_ini_session, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser;
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            goToPrincipal();
        }
    }

    /*
    public void linkCredentialToUser(final AuthCredential credential) {
        FirebaseUser prevUser = currentUser;
        try {
            currentUser = Tasks.await(mAuth.signInWithCredential(credential)).getUser();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (currentUser != null) {
            goToPrincipal();
        } else {
            mAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        goToPrincipal();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.error_ini_session, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }*/

    public void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        progBarLoadSingIn.setVisibility(View.VISIBLE);
        btnGoogleSignIn.setVisibility(View.GONE);

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        //linkCredentialToUser(credential);

        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        Toast.makeText(getApplicationContext(), "Iniciando con Google.", Toast.LENGTH_LONG).show();
                        goToPrincipal();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_ini_session, Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    public void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, SIGN_IN_GOOGLE_CODE);
    }

    public void goToPrincipal() {
        Intent intentGoToPrincipal = new Intent(this, Principal.class);
        intentGoToPrincipal.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentGoToPrincipal);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, R.string.error_conn, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SIGN_IN_GOOGLE_CODE:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    GoogleSignInAccount account = result.getSignInAccount();
                    firebaseAuthWithGoogle(account);
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
