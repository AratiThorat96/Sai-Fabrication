package com.example.saifb

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.saifb.databinding.ActivitySignBinding
import com.example.saifb.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class SignActivity : AppCompatActivity() {

    private lateinit var userName:String
    private lateinit var email:String
    private lateinit var password:String
    private lateinit var auth:FirebaseAuth
    private lateinit var database:DatabaseReference
    private val binding:ActivitySignBinding by lazy{
        ActivitySignBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        //initialization firebase auth
        auth= Firebase.auth
        database=Firebase.database.reference

        binding.createUserButton.setOnClickListener{
            userName=binding.userName.text.toString().trim()
            email=binding.email.text.toString().trim()
            password=binding.password.text.toString().trim()

            if(userName.isBlank() || email.isBlank() || password.isBlank()){
                Toast.makeText(this, "Please Fill All Details", Toast.LENGTH_SHORT).show()
            }else{
                createAccount(email,password)
            }
            val intent=Intent(this,MainActivity::class.java)
        }
        binding.alreadyhaveAccountbutton.setOnClickListener{
            val intent=Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{task ->
            if(task.isSuccessful){
                Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show()
                saveUserData()
                val intent=Intent(this,LoginActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this, "Account Creation Failed", Toast.LENGTH_SHORT).show()
                Log.d("Account", "createAccount: Failure",task.exception)
            }
        }
    }

    // save data in to database
    private fun saveUserData() {
        userName=binding.userName.text.toString().trim()
        email=binding.email.text.toString().trim()
        password=binding.password.text.toString().trim()
        val user=UserModel(userName,email,password)
        val userId=FirebaseAuth.getInstance().currentUser!!.uid

        //save user data Firebase database
        database.child("user").child(userId).setValue(user)

    }
}