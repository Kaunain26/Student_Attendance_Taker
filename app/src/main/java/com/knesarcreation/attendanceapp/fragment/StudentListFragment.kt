package com.knesarcreation.attendanceapp.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.knesarcreation.attendanceapp.R
import com.knesarcreation.attendanceapp.activity.AddStudentActivity
import com.knesarcreation.attendanceapp.adapter.AdapterStudentList
import com.knesarcreation.attendanceapp.database.*
import kotlinx.android.synthetic.main.activity_main_screen.*
import kotlinx.android.synthetic.main.activity_student_list.*
import kotlinx.android.synthetic.main.activity_student_list.view.*
import java.text.SimpleDateFormat
import java.util.*

class StudentListFragment : Fragment(), AdapterStudentList.OnItemClickListener {

    private var datesTime = ""

    /*isActive variable is used to check which fragment is open by there values TRUE or False*/
    private var isActive = false

    /*showAlertDialog is used to show AlertDialog Box when any checkbox is tried to check or uncheck*/
    private var showAlertDialog = false
    private var mAdapter: AdapterStudentList? = null
    private var mDatabase: Database? = null
    private var mListStd = mutableListOf<StudentListClass>()
    private var profName: String? = null
    private var sheetNo: Int? = 0
    private var subName: String? = null

    companion object {
        const val REQUEST_CODE = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_student_list, container, false)

        subName = arguments?.getString("subName")
        sheetNo = arguments?.getInt("sheetNo")
        profName = arguments?.getString("profName")
        isActive = arguments?.getBoolean("isActive")!!

        (activity as AppCompatActivity).toolbar.setBackgroundResource(R.drawable.student_list_backgound)

        view.txtSubNameStdList.text = subName

        view.txtProfNameStdList.text = profName

        if (isActive) {
            /*Making views Visible which are required in StudentListActivity when opened from Attendance sheet fragment*/
            view.txtSelect.visibility = View.VISIBLE
            view.checkAll.visibility = View.VISIBLE
            setHasOptionsMenu(true)
        }

        setDB()

        /*  view.btnAddStudent.setOnClickListener {
              startActivityForResult(
                  Intent(activity as Context, AddStudentActivity::class.java), REQUEST_CODE
              )
          }*/

        gettingDataFromDatabase(view)

        view.stdRecyclerView.layoutManager = LinearLayoutManager(activity as Context)

        view.stdRecyclerView.itemAnimator = DefaultItemAnimator()

        view.stdRecyclerView.isNestedScrollingEnabled = false

        buildRecyclerView(view)

        view.checkAll.setOnClickListener {
            onCheckedChanged(checkAll.isChecked, view)
        }

        /* view.imgSaveBtn.setOnClickListener {
         }*/

        /* view.imgBackBtnStdList.setOnClickListener {
             backPressed()
         }*/

        /*todo: Will implement  later*/
        swipeToDelete()

        return view
    }

    private fun setDB() {
        mDatabase = DatabaseInstance().newInstance(activity as Context)
    }

    private fun gettingDataFromDatabase(view: View) {
        for (list in mDatabase?.mDao()?.getAllStudents(sheetNo!!)!!) {
            mListStd = list.studentListClass
        }
        if (mListStd.isNotEmpty()) {
            view.mHintRelativeLayout.visibility = View.INVISIBLE
        }
    }

    private fun buildRecyclerView(view: View) {
        mAdapter = AdapterStudentList(activity as Context, isActive, this, mListStd)
        mAdapter?.notifyDataSetChanged()
        view.stdRecyclerView?.adapter = mAdapter
    }


    private fun saveAttendance() {
        val builder = AlertDialog.Builder(activity as Context)
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
                        i.stdId,   /*Passing the same stdId which is passed in stdDetails and stdListClass*/
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
            /*After saving Attendance making checkbox unchecked*/
            for (k in mListStd) {
                mDatabase?.mDao()?.updateStudentList(false, k.stdUsn)
            }

            /*After saving attendance open AttendanceSheet Fragment*/
            openFragment(AttendanceSheetFragment())
        }

        builder.setNegativeButton("Don't save") { dialog, _ ->
            dialog.dismiss();
        }
        builder.setCancelable(false)
        builder.show()
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


    private fun onCheckedChanged(isChecked: Boolean, view: View) {
        mAdapter?.toggleSelection(isChecked)
        view.txtSelect.text = if (isChecked) {
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
            activity?.finish()
            return
        }
        if (isActive) {
            if (showAlertDialog) {
                /*If AttendanceSheet Fragment opens this Fragment*/
                val builder = AlertDialog.Builder(activity as Context)
                builder.setTitle("Alert!!")
                builder.setMessage("Attendance isn't saved. Do you want to go back?")
                builder.setPositiveButton("Yes") { _, _ ->
                    for (i in mListStd) {
                        mDatabase?.mDao()?.updateStudentList(false, i.stdUsn)
                    }
                    openFragment(AttendanceSheetFragment())
                }

                builder.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }

                builder.show()
            } else {
                openFragment(AttendanceSheetFragment())
            }
        } else {
            /*if StudentInformationFragment opens this Fragment*/
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, StudentInformationFragment())?.commit()
        }
    }

    private fun openFragment(fragment: Fragment) {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, fragment)?.commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {

            val stdName = data!!.getStringExtra("stdName")
            val stdUsn = data.getStringExtra("stdUsn")
            val stdId = data.getIntExtra("stdId", 0)   /*SystemCurrentTime ms*/
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.student_list_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.ic_save -> {
                saveAttendance()
            }
            R.id.ic_add -> {
                startActivityForResult(
                    Intent(activity as Context, AddStudentActivity::class.java), REQUEST_CODE
                )
            }
        }
        return true
    }

    override fun onResume() {
        view?.let { it -> gettingDataFromDatabase(it) }
        view?.let { it -> buildRecyclerView(it) }
        super.onResume()
    }

    private fun swipeToDelete() {
        /*   ItemTouchHelper(`StudentListActivity$swipeToDelete$1`(this, 0, 12)).attachToRecyclerView(
               `_$_findCachedViewById`(C0754R.C0757id.stdRecyclerView) as RecyclerView
           )*/
    }

}