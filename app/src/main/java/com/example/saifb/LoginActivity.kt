package com.example.saifb

import android.app.Activity
import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ActivityChooserView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.saifb.databinding.ActivityLoginBinding
import com.example.saifb.model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider



class LoginActivity : AppCompatActivity() {

    private lateinit var email:String
    private lateinit var password:String
    private lateinit var auth:FirebaseAuth
    private lateinit var database:DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient


    private val binding:ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        val googelSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("YOUR_WEB_CLIENT_ID")
            .requestEmail()
            .build()


        //Initialize Firebase Auth
        auth=Firebase.auth
        //initialize Firebase database
        database=Firebase.database.reference

        googleSignInClient = GoogleSignIn.getClient(this, googelSignInOptions)



        binding.loginbutton.setOnClickListener{
            //get text form edit text
            email=binding.email.text.toString().trim()
            password=binding.password.text.toString().trim()

            if(email.isBlank() || password.isBlank()){
                Toast.makeText(this, "Please Fill All Details", Toast.LENGTH_SHORT).show()
            }else{
                createUserAccount(email,password)
            }

        }
        binding.googlebutton.setOnClickListener{
            val signIntent=googleSignInClient.signInIntent
            launcher.launch(signIntent)

        }
        binding.donthavebutton.setOnClickListener{
            val intent=Intent(this,SignActivity::class.java)
            startActivity(intent)

        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun createUserAccount(email: String, password: String) {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener{ task ->
            if(task.isSuccessful){
                val user=auth.currentUser
                Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show()
                updateUi(user)
            }else{
                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        val user=auth.currentUser
                        Toast.makeText(this, "Create User and Login Successfully", Toast.LENGTH_SHORT).show()
                        saveUserData()
                        updateUi(user)
                    }else{
                        Toast.makeText(this, "Authentification Failed", Toast.LENGTH_SHORT).show()
                        Log.d("Account", "createUserAccount: Authentication Failed",task.exception)
                    }
                }
            }
        }
    }

    private fun saveUserData() {
        email=binding.email.text.toString().trim()
        password=binding.password.text.toString().trim()

        val user=UserModel(email,password)
        val userId=FirebaseAuth.getInstance().currentUser?.uid
        userId?.let {
            database.child("user").child(it).setValue(user)
        }

    }


    private val launcher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
       result ->
        if(result.resultCode== Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if(task.isSuccessful){
                val account : GoogleSignInAccount =task.result
                val credential=GoogleAuthProvider.getCredential(account.idToken,null)
                auth.signInWithCredential(credential).addOnCompleteListener{authTask ->
                    if(authTask.isSuccessful){
                        //successful sign ind with Google
                        Toast.makeText(this, "Successfully Sign-In With Google", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this,MainActivity::class.java))
                        finish()
                    }else{
                        Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show()
                    }

                }
            }else{
                Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show()
            }
        }

    }
    //check if user is already logged in

    private fun updateUi(user: FirebaseUser?) {
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }

}
