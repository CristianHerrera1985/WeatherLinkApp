package com.saintleo.weatherlinkapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var tvWelcome: TextView
    private lateinit var tvUserName: TextView
    private lateinit var imgProfile: ImageView
    private lateinit var btnSignIn: Button
    private lateinit var btnSignOut: Button

    // Lista simple de favoritos
    private val favorites = mutableListOf<String>()

    private val signInLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
            if (idToken != null) {
                firebaseAuthWithGoogle(idToken)
            }
        } catch (e: ApiException) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar vistas
        tvWelcome = findViewById(R.id.tvWelcome)
        tvUserName = findViewById(R.id.tvUserName)
        imgProfile = findViewById(R.id.imgProfile)
        btnSignIn = findViewById(R.id.btnSignIn)
        btnSignOut = findViewById(R.id.btnSignOut)

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Configurar Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Acciones de botones
        btnSignIn.setOnClickListener { signIn() }
        btnSignOut.setOnClickListener { signOut() }

        // Revisar si hay usuario activo
        updateUI(auth.currentUser)
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    updateUI(auth.currentUser)
                } else {
                    updateUI(null)
                }
            }
    }

    private fun signOut() {
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener {
            updateUI(null)
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            tvWelcome.text = "Bienvenido"
            tvUserName.text = user.displayName
            tvUserName.visibility = TextView.VISIBLE
            imgProfile.visibility = ImageView.VISIBLE
            Glide.with(this).load(user.photoUrl).into(imgProfile)

            btnSignIn.visibility = Button.GONE
            btnSignOut.visibility = Button.VISIBLE

            // Redirigir a HomeActivity después de iniciar sesión correctamente
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // Cierra esta actividad para que no se pueda volver atrás con el botón de retroceso
        } else {
            tvWelcome.text = "Por favor, inicia sesión"
            tvUserName.visibility = TextView.GONE
            imgProfile.visibility = ImageView.GONE
            btnSignIn.visibility = Button.VISIBLE
            btnSignOut.visibility = Button.GONE
        }
    }

    // --- Funciones para manejar favoritos ---
    fun addFavorite(city: String) {
        if (!favorites.contains(city)) {
            favorites.add(city)
        }
    }

    fun removeFavorite(city: String) {
        favorites.remove(city)
    }

    fun getFavorites(): List<String> = favorites
}