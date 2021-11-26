package com.example.al4t_claco.controller

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.databinding.DataBindingUtil
import androidx.gridlayout.widget.GridLayout
import com.example.al4t_claco.model.Activity
import com.example.al4t_claco.model.File
import com.example.al4t_claco.R
import com.example.al4t_claco.databinding.ActivityResourceBinding
import com.example.al4t_claco.view.DataActivity

class ResourceActivity() : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val course = intent.getStringExtra("course")
        val activity = intent.getSerializableExtra("activity") as Activity

        val binding: ActivityResourceBinding = DataBindingUtil.setContentView(this, R.layout.activity_resource)
        binding.activity = DataActivity(activity,course.toString())
        supportActionBar?.title = "Resources"


        fun showResources(files: List<File>) {
            val gridlayout = findViewById<GridLayout>(R.id.gridResources)

            //POPUP
            fun createAndShowDialog(file: File) {

                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setTitle("Do you want to open the following file?")
                dialogBuilder.setMessage("Name : ${file.name}\nType : ${file.type}")
                dialogBuilder.setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, whichButton -> })
                dialogBuilder.setNeutralButton("Download",
                    DialogInterface.OnClickListener { dialog, whichButton ->
                        Toast.makeText(
                            applicationContext,
                            "Downloading", Toast.LENGTH_SHORT
                        ).show()
                    })
                dialogBuilder.setPositiveButton("Open",
                    DialogInterface.OnClickListener { dialog, whichButton -> })

                val b = dialogBuilder.create()
                b.show()

            }
            //CREATE BUTTONS
            for (file in files) {
                val newButton = Button(this, null, android.R.attr.borderlessButtonStyle)
                val buttonImage: Int

                when (file.type) {
                    "pdf" -> buttonImage = R.drawable.pdf_medium
                    else -> buttonImage = R.drawable.not_found_medium
                }
                newButton.setCompoundDrawablesWithIntrinsicBounds(0, buttonImage, 0, 0)
                newButton.text = file.name
                newButton.isAllCaps = false
                newButton.maxWidth = R.drawable.pdf_medium.toDrawable().intrinsicWidth

                //On short click : show file
                newButton.setOnClickListener(View.OnClickListener {
                    //TODO("Show file")
                })
                //On long click : popup to download file
                newButton.setOnLongClickListener(View.OnLongClickListener {
                    createAndShowDialog(file)
                    true
                })

                gridlayout.addView(newButton)
            }
        }
        showResources(activity.resources)
    }
}