/*
package com.knesarcreation.attendanceapp.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.knesarcreation.attendanceapp.R
import com.knesarcreation.attendanceapp.adapter.AdapterStudentList
import com.knesarcreation.attendanceapp.database.*
import kotlinx.android.synthetic.main.activity_student_list.*
import java.text.SimpleDateFormat
import java.util.*


class StudentListActivity : AppCompatActivity(), AdapterStudentList.OnItemClickListener {
    var datesTime = ""

    */
/*isActive variable is used to check which fragment is open by there values TRUE or False*//*

    private var isActive = false

    */
/*showAlertDialog is used to show AlertDialog Box when any checkbox is tried to check or uncheck*//*

    private var showAlertDialog = false
    var mAdapter: AdapterStudentList? = null
    var mDatabase: Database? = null
    private var mListStd = mutableListOf<StudentListClass>()
    private var profName: String? = null
    var sheetNo: Int? = 0
    var subName: String? = null

    companion object {
        const val REQUEST_CODE = 1
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_list)


        sheetNo = intent?.getIntExtra("sheetNo", 0)
        subName = intent?.getStringExtra("subName")
        profName = intent?.getStringExtra("profName")
        isActive = intent?.getBooleanExtra("isActive", false)!!

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            llStudentList.transitionName = "activityBackGroundImg"
        }
        */
/*If StudentListActivity is opened from Attendance sheet fragment*//*

        if (isActive) {
            */
/*Making views Visible which are required in StudentListActivity when opened from Attendance sheet fragment*//*

            imgSaveBtn.visibility = View.VISIBLE
            btnAddStudent.visibility = View.VISIBLE
            txtSelect.visibility = View.VISIBLE
            checkAll.visibility = View.VISIBLE

            txtSubNameStdList.text = subName

            txtProfNameStdList.text = profName
        } else {
            */
/* if StudentListActivity is opened from StudentInformation fragment*//*


            */
/*Hiding views which are not required in StudentListActivity when opened from StudentInformation fragment*//*

            txtSubNameStdList.visibility = View.GONE
            txtProfNameStdList.visibility = View.GONE

            txtSubNameStd2ndList.visibility = View.VISIBLE
            txtProfNameStd2ndList.visibility = View.VISIBLE
            txtSubNameStd2ndList.text = subName
            txtProfNameStd2ndList.text = profName
        }

        setDB()
        btnAddStudent.setOnClickListener {
            startActivityForResult(
                Intent(
                    this,
                    AddStudentActivity::class.java
                ), REQUEST_CODE
            )
        }
        gettingDataFromDatabase()


        stdRecyclerView.layoutManager = LinearLayoutManager(this)

        stdRecyclerView.itemAnimator = DefaultItemAnimator()

        stdRecyclerView.isNestedScrollingEnabled = false
        buildRecyclerView()

        checkAll.setOnClickListener {
            onCheckedChanged(checkAll.isChecked)
        }

        imgSaveBtn.setOnClickListener {
            saveAttendance()
        }

        imgBackBtnStdList.setOnClickListener {
            backPressed()
        }

        */
/*todo: Will implement  later*//*

        swipeToDelete()
    }

    private fun gettingDataFromDatabase() {
        for (list in mDatabase?.mDao()?.getAllStudents(sheetNo!!)!!) {
            mListStd = list.studentListClass
        }
        if (mListStd.isNotEmpty()) {
            mHintRelativeLayout.visibility = View.INVISIBLE
        }
    }

    private fun saveAttendance() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Save Attendance" as CharSequence)
        builder.setMessage("Do you want to save changes to attendance?" as CharSequence)
        builder.setPositiveButton("Save") { _, _ ->

            val date = Date()
            val simpleDateFormat = SimpleDateFormat("EEE, d MMM yyyy hh:mm:ss aaa", Locale.US)
            datesTime = simpleDateFormat.format(date)

            val id = System.currentTimeMillis().toInt()
            val attendanceDateTimes = AttendanceDateTimes(id, datesTime, sheetNo!!)
            mDatabase?.mDao()?.insertAttendanceDatesTime(attendanceDateTimes)

            for (list in mDatabase?.mDao()?.getAllStudents(sheetNo!!)!!) {
                for (i in list.studentListClass) {

                    val stdList = StudentPastAttendance(
                        System.currentTimeMillis().toInt(),
                        id,
                        i.isChecked,
                        i.stdName,
                        i.stdUsn,
                        i.stdId,   */
/*Passing the same stdId which is passed in stdDetails and stdListClass*//*

                    )
                    mDatabase?.mDao()?.insertStudentHistory(stdList)

                    var incrementedPresent = 0
                    if (i.isChecked) {
                        Log.d("isChecked", "saveAttendance:${i.isChecked} ")
                        for (j in mDatabase?.mDao()?.getStudentDetails(i.stdUsn)!!) {
                            incrementedPresent = j.present + 1
                        }
                        mDatabase?.mDao()?.updateStudentPresentStatus(incrementedPresent, i.stdUsn)
                    } else {
                        var decrementAbsent = 0
                        for (j2 in mDatabase?.mDao()?.getStudentDetails(i.stdUsn)!!) {
                            decrementAbsent = j2.absent + 1
                        }
                        mDatabase?.mDao()
                            ?.updateStudentAbsentStatus(decrementAbsent, i.stdUsn)
                    }
                }
            }
            */
/*After saving Attendance making checkbox unchecked*//*

            for (k in mListStd) {
                mDatabase?.mDao()?.updateStudentList(false, k.stdUsn)
            }
            finish()
        }

        builder.setNegativeButton("Don't save") { dialog, _ ->
            dialog.dismiss();
        }
        builder.setCancelable(false)
        builder.show()
    }

    private fun buildRecyclerView() {
        mAdapter = AdapterStudentList(this, isActive, this, mListStd)
        mAdapter?.notifyDataSetChanged()
        stdRecyclerView.adapter = mAdapter
    }

    private fun setDB() {
        mDatabase = DatabaseInstance().newInstance(this)
    }

    override fun onCheckBoxClicked(position: Int, viewHolder: AdapterStudentList.ViewHolder?) {
        val studentListClass = mListStd[position]
        showAlertDialog = true
        if (viewHolder?.checkbox?.isChecked!!) {
            mDatabase?.mDao()?.updateStudentList(true, studentListClass.stdUsn)
        } else {
            mDatabase?.mDao()?.updateStudentList(false, studentListClass.stdUsn)
        }
    }

    private fun onCheckedChanged(isChecked: Boolean) {
        mAdapter?.toggleSelection(isChecked)
        txtSelect.text = if (isChecked) {
            for (i in mListStd) {
                mDatabase?.mDao()?.updateStudentList(true, i.stdUsn)
            }
            "Deselect all"
        } else {
            for (i in mListStd) {
                mDatabase?.mDao()?.updateStudentList(false, i.stdUsn)
            }
            "Select all"
        }
    }

    private fun backPressed() {
        if (mListStd.isEmpty()) {
            super.onBackPressed()
            return
        }
        if (isActive && showAlertDialog) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Alert!!")
            builder.setMessage("Attendance isn't saved. Do you want to go back?")
            builder.setPositiveButton("Yes") { _, _ ->
                for (i in mListStd) {
                    mDatabase?.mDao()?.updateStudentList(false, i.stdUsn)
                }
                finish()
            }

            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

            builder.show()
        } else {
            super.onBackPressed()
        }
    }

    override fun onBackPressed() {
        backPressed()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

            val stdName = data!!.getStringExtra("stdName")
            val stdUsn = data.getStringExtra("stdUsn")
            val stdId = data.getIntExtra("stdId", 0)   */
/*SystemCurrentTime ms*//*

            val stdBranch = data.getStringExtra("stdBranch")
            val stdContact = data.getStringExtra("stdContact")
            val stdAddress = data.getStringExtra("stdAddress")
            val totalStud = mListStd.size + 1

            mDatabase?.mDao()?.updateTotalStudInAttendanceSheet(subName!!, totalStud)

            mDatabase?.mDao()?.updateTotalStudInAttendanceHistory(subName!!, totalStud)

            val currentStudentDetails = StudentDetails(
                stdId, sheetNo!!, stdUsn!!, stdName!!, stdAddress!!, stdContact!!, stdBranch!!,
                0, 0.0f, 0
            )

            mDatabase?.mDao()?.insertStudentDetails(currentStudentDetails)

            val currentStud = StudentListClass(
                stdId, false,
                stdName, sheetNo!!, stdUsn
            )
            mListStd.add(currentStud)

            mAdapter?.notifyDataSetChanged()
            if (mListStd.isNotEmpty()) {
                mHintRelativeLayout.visibility = View.INVISIBLE
            }
            mDatabase?.mDao()?.insertStudentList(currentStud)
        }
    }

    override fun onResume() {
        gettingDataFromDatabase()
        buildRecyclerView()
        super.onResume()
    }

    private fun swipeToDelete() {
        */
/*   ItemTouchHelper(`StudentListActivity$swipeToDelete$1`(this, 0, 12)).attachToRecyclerView(
               `_$_findCachedViewById`(C0754R.C0757id.stdRecyclerView) as RecyclerView
           )*//*

    }
}*/
