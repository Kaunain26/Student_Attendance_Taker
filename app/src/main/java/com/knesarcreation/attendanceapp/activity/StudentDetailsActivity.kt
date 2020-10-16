package com.knesarcreation.attendanceapp.activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.knesarcreation.attendanceapp.R
import com.knesarcreation.attendanceapp.database.Database
import com.knesarcreation.attendanceapp.database.DatabaseInstance
import kotlinx.android.synthetic.main.activity_student_details.*
import kotlinx.android.synthetic.main.dialog_box_edit.view.*


class StudentDetailsActivity : AppCompatActivity() {

    var id = 0
    var isExecuted = false

    var mDatabase: Database? = null
    private var stdPercentage: String = ""
    private var attendancePercentage = 0.0

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_details)
        setDB()

        val stdUsn = intent?.getStringExtra("stdUsn")

        for (i in mDatabase?.mDao()?.getStudentDetails(stdUsn!!)!!) {
            id = i.id

            try {
                attendancePercentage = ((i.present.toDouble() / (i.present + i.absent)) * 100)
                stdPercentage = String.format("%.3f", attendancePercentage, 1)
                if (i.absent == 0) {
                    mDatabase?.mDao()?.updateStudentPercentageStatus(0.0, i.studUsn)
                } else {
                    mDatabase?.mDao()
                        ?.updateStudentPercentageStatus(attendancePercentage, i.studUsn)
                }

                if (!isExecuted) {
                    if (attendancePercentage > 85.000) {
                        txtStdPercentage.setTextColor(resources.getColor(R.color.darkGreen))
                    } else {
                        txtStdPercentage.setTextColor(Color.RED)
                        stdPercentageMessage.text =
                            "Your attendance percentage is less \nIt should be above 85%"
                    }
                    txtStdPercentage.text = stdPercentage
                    isExecuted = true

                }
            } catch (e: ArithmeticException) {
                e.printStackTrace()
            }

            txtStudentNameStdDetails.text = i.studName
            txtStdUsnStudDetails.text = i.studUsn

            txtPresentStdDetails.text = i.present.toString()
            txtAbsentStdDetails.text = i.absent.toString()

            if (i.studCourse.isBlank()) {
                txtStdCourseStudDetails.text = "N/A"
            } else {
                txtStdCourseStudDetails.text = i.studCourse
            }
            if (i.studAddress.isBlank()) {
                txtStdAddressStudDetails.text = "N/A"
            } else {
                txtStdAddressStudDetails.text = i.studAddress
            }
            if (i.studContact.isBlank()) {
                txtStdContactNoStudDetails.text = "N/A"
            } else {
                txtStdContactNoStudDetails.text = i.studContact
            }
        }
        editingStudentDetails()
        btnArrowBackStdDetails.setOnClickListener {
            super.onBackPressed()
        }
    }

    private fun editingStudentDetails() {
        editStdUsn.setOnClickListener {
            updateUsn()
        }
        editCourse.setOnClickListener {
            updateCourse()
        }
        editAddress.setOnClickListener {
            updateAddress()
        }
        editContactNo.setOnClickListener {
            updateContact()
        }
    }

    private fun updateUsn() {
        alertDialog("USN or Roll No.", "Edit USN", txtStdUsnStudDetails.text.toString(), 1)
    }

    private fun updateCourse() {
        alertDialog(
            "Branch and Course",
            "Edit Branch And Course",
            txtStdCourseStudDetails.text.toString(),
            2
        )
    }

    private fun updateContact() {
        alertDialog(
            "Contact No.",
            "Edit Contact No.",
            txtStdContactNoStudDetails.text.toString(),
            3
        )
    }

    private fun updateAddress() {
        alertDialog("Address", "Edit Address", txtStdAddressStudDetails.text.toString(), 4)
    }

    private fun alertDialog(hint: String, title: String, stdDetail: String, mode: Int) {
        val builder = AlertDialog.Builder(this)
        val dialogLayout: View =
            layoutInflater.inflate(R.layout.dialog_box_edit, null)
        val txtTitle = dialogLayout.txtEdit
        val etEditDialog = dialogLayout.etEditDialog
        val btnEditDialog = dialogLayout.btnEditDialog
        val btnCancelEditDialog = dialogLayout.btnCancelEditDialog
        builder.setView(dialogLayout)
        val dialog = builder.create()

        etEditDialog.hint = hint
        txtTitle.text = title

        etEditDialog.setText(stdDetail)
        btnEditDialog.setOnClickListener {
            when (mode) {
                1 -> {
                    txtStdUsnStudDetails.text = etEditDialog.text.toString()

                    mDatabase?.mDao()
                        ?.updateUSNStudentDetails(id, txtStdUsnStudDetails.text as String)

                    mDatabase?.mDao()
                        ?.updateUSNStudentListsFromID(id, txtStdUsnStudDetails.text as String)

                    mDatabase?.mDao()
                        ?.updateUSNStudentAttendanceHistory(
                            id,
                            txtStdUsnStudDetails.text as String
                        )

                    dialog.dismiss()
                }
                2 -> {

                    val editedStdCourse = etEditDialog.text.toString()
                    txtStdCourseStudDetails.text = editedStdCourse

                    mDatabase?.mDao()?.updateCourseStudentDetails(id, editedStdCourse)
                    dialog.dismiss()
                }
                3 -> {

                    val editedContactNo = etEditDialog.text.toString()
                    txtStdContactNoStudDetails.text = editedContactNo

                    mDatabase?.mDao()?.updateContactStudentDetails(id, editedContactNo)
                    dialog.dismiss()
                }
                4 -> {

                    val editedStdAddress = etEditDialog.text.toString()
                    txtStdAddressStudDetails.text = editedStdAddress

                    mDatabase?.mDao()?.updateAddressStudentDetails(id, editedStdAddress)
                    dialog.dismiss()
                }
            }
        }
        btnCancelEditDialog.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun setDB() {
        mDatabase = DatabaseInstance().newInstance(this)
    }
}