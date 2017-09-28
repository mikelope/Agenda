package pe.usil.android.agenda;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Principal extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient googleApiClient;

    //private FirebaseAuth mFirebaseAuth;
    //private FirebaseAuth.AuthStateListener mAuthStateListener;

    //private FirebaseAuth firebaseAuthGoogle, firebaseAuthFacebook;
    //private FirebaseAuth.AuthStateListener authStateListenerGoogle, authStateListenerFacebook;

    private TextView textNomUsuario;
    private TextView textEmailUsuario;
    private TextView textIdUsuario;
    private Button btnLogout;
    private Button btnRevocarUsuario;

    private int isLoggedByFacebook;
    private int isLoggedByGoogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        textNomUsuario = (TextView) findViewById(R.id.textNomUsuario);
        textEmailUsuario = (TextView) findViewById(R.id.textEmailUsuario);
        textIdUsuario = (TextView) findViewById(R.id.textIdUsuario);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnRevocarUsuario = (Button) findViewById(R.id.btnRevocarUsuario);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                goToLogin();
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            textNomUsuario.setText(user.getDisplayName());
            textEmailUsuario.setText(user.getEmail());
            textIdUsuario.setText(user.getUid());
        } else {
            goToLogin();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void goToLogin() {
        Intent intentGoToLogin = new Intent(this, Login.class);
        intentGoToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentGoToLogin);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
