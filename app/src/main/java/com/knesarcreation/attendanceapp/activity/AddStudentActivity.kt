package com.knesarcreation.attendanceapp.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.knesarcreation.attendanceapp.R
import com.knesarcreation.attendanceapp.database.Database
import com.knesarcreation.attendanceapp.database.DatabaseInstance
import kotlinx.android.synthetic.main.activity_add_student.*


class AddStudentActivity : AppCompatActivity() {
    private var mDatabase: Database? = null

    /* access modifiers changed from: protected */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_student)
        setDB()

        saveBtnAddStd.setOnClickListener {
            if (etStudentName.text.toString().isNotEmpty() && etStdUsn.text.toString()
                    .isNotEmpty()
            ) {
                val intent = Intent()
                intent.putExtra("stdName", etStudentName.text.toString())
                intent.putExtra("stdUsn", etStdUsn.text.toString())
                intent.putExtra("stdId", System.currentTimeMillis().toInt())
                intent.putExtra("stdBranch", etBranchName.text.toString())
                intent.putExtra("stdContact", etContactNo.text.toString())
                intent.putExtra("stdAddress", etAddress.text.toString())
                setResult(-1, intent)
                finish()
            } else {
                Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show()
            }
        }
        imgBackArrowAddStd.setOnClickListener {
            super.onBackPressed();
        }
    }

    private fun setDB() {
        mDatabase = DatabaseInstance().newInstance(this)
    }
}