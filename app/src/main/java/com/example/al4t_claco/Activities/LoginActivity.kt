package com.example.al4t_claco.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.util.Log
import android.widget.EditText
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.al4t_claco.R
import com.example.al4t_claco.Models.*
import kotlinx.coroutines.launch

/* This is the class that shows the page Login, where the user is can put his email and password
*  in order to have access to the workspace
*/
class LoginActivity : AppCompatActivity() {

    lateinit var session: sessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //Session
        session = sessionManager(applicationContext)
        if (session.isLoggedIn()) {
            var intent = Intent(applicationContext, Dashboard::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        //TODO : Get this from the API instead of creating them here

        // trying to implement ViewModel
        val activityModel :LoginViewModel by viewModels()
        //lifecycleScope.launch {
        //    repeatOnLifecycle(Lifecycle.State.STARTED) {
        //        activityModel.loginUIState.collect { loginUIState  ->
                    // Update UI elements
        //            val secret = loginUIState.name.toString() + loginUIState.password.toString()



          //      }
          //  }
        //}
        activityModel.user_authentification()


        val teachers = listOf("Teacher1", "Teacher2")
        val activity = Activity("Architecture", "4MIN-AL4T", teachers, "lalalla")
        var course = Course("Electronics", "14", 5, 5, "Teacher1", "description 1", listOf(activity))
        var course2 = Course("Computer Science", "14", 5, 5, "Teacher1", "description 2", listOf(activity))

        val teach = Teacher("Jean", "J3L@ecam.be", "123", listOf(course))
        val student = Student("Amine", "17@ecam.be", "123", listOf(course, course2))

        val listVal: List<String> = student.workspace.map { it.name }
        var cou: String = listVal.joinToString(" - ")


        val email = findViewById<EditText>(R.id.edt_email)
        val pass = findViewById<EditText>(R.id.edt_password)
        val button = findViewById<Button>(R.id.btnLogIn)

        // check if the user has put the correct email and password
        button.setOnClickListener {
            if ((email.text.toString() == student.email) and (pass.text.toString() == student.password)) {
                //user information saved in session
                session.createLoginSession(student.name, student.email, cou, "Teacher")
                val intent = Intent(this, Cars::class.java).apply {
                    putExtra("name", "Amine")
                    putExtra("matricule", "17")
                }

                startActivity(intent)
            }
        }

    }
}

