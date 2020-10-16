package com.knesarcreation.attendanceapp.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.knesarcreation.attendanceapp.R
import kotlinx.android.synthetic.main.activity_create_attendance_sheet.*

class CreateAttendanceSheet : AppCompatActivity() {
    companion object {
        const val EXTRA_CLASS_YEAR = "com.knesarcreation.attendanceapp_EXTRA_CLASS"
        const val EXTRA_PROF_NAME = "com.knesarcreationcreation.attendanceapp_EXTRA_PROF_NAME"
        const val EXTRA_SUB_CODE = "com.knesarcreation.attendanceapp_EXTRA_SUB_CODE"
        const val EXTRA_SUB_NAME = "com.knesarcreation.attendanceapp_EXTRA_SUB_NAME"
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_attendance_sheet)

        if (intent.hasExtra(EXTRA_SUB_NAME)) {
            txtTitleNameCreateAttendSheet.text = "Edit Attendance Sheet"
            etSubjectName.setText(intent?.getStringExtra(EXTRA_SUB_NAME))
            etSubjectCode.setText(intent?.getStringExtra(EXTRA_SUB_CODE))
            etLectureName.setText(intent?.getStringExtra(EXTRA_PROF_NAME))
        } else {
            txtTitleNameCreateAttendSheet.text = "Create Attendance Sheet"
        }
        val arrayAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            arrayOf("First Year", "Second Year", "Third Year", "Fourth Year")
        )
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        mSpinner.adapter = arrayAdapter

        imgSaveBtn.setOnClickListener {
            val profName = etLectureName.text.toString()
            val subName = etSubjectName.text.toString()
            val subCode = etSubjectCode.text.toString()
            val chooseYear = mSpinner.selectedItem.toString()

            if (profName.isNotBlank() && subName.isNotBlank()) {
                val intent1 = Intent()
                intent1.putExtra(EXTRA_PROF_NAME, profName)
                intent1.putExtra(EXTRA_SUB_NAME, subName)
                intent1.putExtra(EXTRA_SUB_CODE, subCode)
                intent1.putExtra(EXTRA_CLASS_YEAR, chooseYear)
                setResult(RESULT_OK, intent1)
                finish()
            } else {
                Toast.makeText(this, "Please fill the required field", Toast.LENGTH_SHORT).show()
            }
        }
        btnArrowBackCreateAttend.setOnClickListener {
            super.onBackPressed()
        }
    }
}