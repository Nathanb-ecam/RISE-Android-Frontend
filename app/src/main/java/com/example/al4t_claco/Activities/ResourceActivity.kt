package com.example.al4t_claco.Activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.graphics.drawable.toDrawable
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.gridlayout.widget.GridLayout
import com.example.al4t_claco.Models.Activity
import com.example.al4t_claco.Models.File
import com.example.al4t_claco.R
import com.example.al4t_claco.databinding.ActivityResourceBinding
import com.example.al4t_claco.Models.sessionManager
import com.example.al4t_claco.dataClasses.DataActivity
import java.io.FileOutputStream
import java.io.IOException

import com.google.android.material.navigation.NavigationView

/* This is the class that shows the page "resources", where information about an activity is shown,
*  as well as the files of the activity like the slides and syllabi.
*/
class ResourceActivity() : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var session: sessionManager
    lateinit var utilisateur: HashMap<String, String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // SESSION INFORMATION

        session = sessionManager(applicationContext)
        session.checkLogin()

        //GET ACTIVITY INFORMATION FROM "COURSEINFORMATION"

        val course = intent.getStringExtra("course")
        val activity = intent.getSerializableExtra("activity") as Activity

        val binding: ActivityResourceBinding = DataBindingUtil.setContentView(this, R.layout.activity_resource)
        binding.activity = DataActivity(activity,course.toString())

        supportActionBar?.title = "Resources"

        //SIDE MENU

        val drawerLayout : DrawerLayout = findViewById<View>(R.id.drawerLayout) as DrawerLayout
        val navView : NavigationView = findViewById<View>(R.id.navView) as NavigationView
        val headerView = navView.getHeaderView(0)
        val user = headerView.findViewById<TextView>(R.id.user)

        utilisateur = session.getUserDetails()
        var name :String = utilisateur.get(sessionManager.companion.KEY_NAME)!!

        user.text = name

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_home -> startActivity(Intent(this, Dashboard::class.java))
                R.id.nav_calendar -> startActivity(Intent(applicationContext, CalendarActivity::class.java))
                R.id.nav_forum -> Toast.makeText(applicationContext,"Clicked Forum", Toast.LENGTH_SHORT).show()
                R.id.password -> startActivity(Intent(applicationContext, ChangePassword::class.java))
                R.id.logout -> session.logoutdUser()
            }
            true
        }

        //download a file from the assets to phone storage
        fun downloadFile(file: File){
            //TODO : find another method to download (work with API instead of having the resources in the assets)
            val outDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val outFile = java.io.File(outDir, file.fullName)

            val myInput = this.assets.open(file.fullName)

            try{
                val myOutput = FileOutputStream(outFile.path)
                try {
                    myInput.copyTo(myOutput)
                }
                catch (e: IOException){
                    Toast.makeText(applicationContext,e.toString(),Toast.LENGTH_SHORT).show()
                }
                finally {
                    Toast.makeText(applicationContext,"downloaded File to ${outFile}",Toast.LENGTH_SHORT).show()
                    myInput.close()
                    myOutput.close()
                }
            } catch (e: IOException){
                Toast.makeText(applicationContext,e.toString(),Toast.LENGTH_LONG).show()
            }
        }

        //open and read pdf
        fun openFile(file: File) {
            if(file.type =="pdf"){
                val intent = Intent(this, PdfViewActivity::class.java)
                intent.putExtra("file", file)
                startActivity(intent)
            }
            else{
                Toast.makeText(applicationContext,"The file type is unknown",Toast.LENGTH_LONG).show()
            }
        }

        //popup listener
        fun onPopupListener(dialog: Int, whichFile: File){
            //Toast.makeText(applicationContext,dialog, Toast.LENGTH_SHORT).show()

            when(dialog){
                R.string.download -> downloadFile(whichFile)
                R.string.open -> openFile(whichFile)
            }
        }

        //popup for proposing to open or download the file
        fun createAndShowDialog(file: File) {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setTitle("Do you want to download the following file?")
            dialogBuilder.setMessage("Name : ${file.name}\nType : ${file.type}")
            dialogBuilder.setNegativeButton(android.R.string.cancel,
                DialogInterface.OnClickListener { dialog, whichButton ->
                    onPopupListener(android.R.string.cancel,file)
                })
            dialogBuilder.setPositiveButton(R.string.download,
                DialogInterface.OnClickListener { dialog, whichButton ->
                    onPopupListener(R.string.download,file)
                })
            dialogBuilder.setNeutralButton(R.string.open,
                DialogInterface.OnClickListener { dialog, whichButton ->
                    onPopupListener(R.string.open,file)
                })

            val b = dialogBuilder.create()
            b.show()
        }

        //Dynamically show resources in a grid
        fun showResources(files: List<File>) {
            val gridlayout = findViewById<GridLayout>(R.id.gridResources)

            //CREATE BUTTONS

            for (file in files) {
                val newButton = Button(this, null, android.R.attr.borderlessButtonStyle)
                val buttonImage: Int

                //if file type is known
                when (file.type) {
                    "pdf" -> buttonImage = R.drawable.pdf_medium
                    else -> buttonImage = R.drawable.not_found_medium
                }
                newButton.setCompoundDrawablesWithIntrinsicBounds(0, buttonImage, 0, 0)
                newButton.text = file.name
                newButton.isAllCaps = false
                newButton.maxWidth = R.drawable.pdf_medium.toDrawable().intrinsicWidth

                //On short click : popup to download file
                newButton.setOnClickListener(View.OnClickListener {
                    openFile(file)
                })
                //On long click :
                newButton.setOnLongClickListener(View.OnLongClickListener {
                    createAndShowDialog(file)
                    true
                })

                gridlayout.addView(newButton)
            }
        }
        showResources(activity.resources)
    }

    //edit text in the activity information if user is a teacher
    fun editTextDialog(text: TextView, whichText: String){
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Edit the ${whichText}")

        val input = EditText(this)

        input.setText(text.text)
        input.inputType = InputType.TYPE_CLASS_TEXT
        dialogBuilder.setView(input)

        dialogBuilder.setNegativeButton(android.R.string.cancel,
            DialogInterface.OnClickListener { dialog, whichButton -> })
        dialogBuilder.setPositiveButton(R.string.ok,
            DialogInterface.OnClickListener { dialog, whichButton ->
                //TODO : set new info in the activity with an API call
                text.text = input.text.toString()
            })
        val b = dialogBuilder.create()
        b.show()
    }

    //adds rollup menu and top right button to edit text
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.mod_description -> {
                Toast.makeText(applicationContext,"Clicked on modify description",Toast.LENGTH_SHORT).show()
                val text = findViewById<TextView>(R.id.activityDescription)
                editTextDialog(text,"description")
                true
            }
            R.id.mod_teachers ->{

                Toast.makeText(applicationContext,"Clicked on teachers",Toast.LENGTH_SHORT).show()
                val text = findViewById<TextView>(R.id.activityTeachersValue)
                editTextDialog(text,"teachers")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)


    }

    //If the user is a Teacher, display options to modify the activity details
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        session = sessionManager(applicationContext)
        session.checkLogin()

        var type : String = utilisateur.get(sessionManager.companion.KEY_TYPE)!!

        if (type == "Teacher") {
            menuInflater.inflate(R.menu.modify_activity, menu)
            return true
        }else{
            return false
        }
    }
}
