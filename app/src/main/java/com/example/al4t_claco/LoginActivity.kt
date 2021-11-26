package com.example.al4t_claco

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import com.example.al4t_claco.model.*
import com.google.android.material.textfield.TextInputEditText


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val teachers = listOf("Teacher1","Teacher2")
        val activity =  Activity("AL4T","4MIN-AL4T",teachers)
        val course =  Course ("Mobile","14", 5, "Teacher1",listOf(activity))
        val teach = Teacher("Test", "tst", "123", listOf(course))
        val student = Student("test","1","123",listOf(course))

        val email = findViewById<TextInputEditText>(R.id.EmailId)
        val pass = findViewById<TextInputEditText>(R.id.PassId)
        val button = findViewById<Button>(R.id.button2)
        button.setOnClickListener {
            if ((email.text.toString() == student.login) and (pass.text.toString() == student.Password)){
                val intent = Intent(this, MainActivity::class.java)

                startActivity(intent)
            }
        }
    }
}