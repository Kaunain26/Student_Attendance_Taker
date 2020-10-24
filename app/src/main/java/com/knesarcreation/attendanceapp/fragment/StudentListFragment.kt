package com.knesarcreation.attendanceapp.fragment

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.knesarcreation.attendanceapp.R
import com.knesarcreation.attendanceapp.activity.AddStudentActivity
import com.knesarcreation.attendanceapp.adapter.AdapterStudentList
import com.knesarcreation.attendanceapp.database.*
import com.knesarcreation.attendanceapp.util.SharedTransition
import kotlinx.android.synthetic.main.activity_main_screen.*
import kotlinx.android.synthetic.main.fragment_student_list.*
import kotlinx.android.synthetic.main.fragment_student_list.view.*
import java.text.SimpleDateFormat
import java.util.*

class StudentListFragment : Fragment(), AdapterStudentList.OnItemClickListener {

    private var datesTime = ""


    private var mAdapter: AdapterStudentList? = null
    private var mDatabase: Database? = null
    private var profName: String? = null
    private var sheetNo: Int? = 0
    private var subName: String? = null
    private var sharedTransition: SharedTransition? = null

    companion object {
        const val REQUEST_CODE = 1

        /*isActive variable is used to check which fragment is open by there values TRUE or False*/
        var isActive = false

        /*showAlertDialog is used to show AlertDialog Box when any checkbox is tried to check or uncheck*/
        var showAlertDialog = false

        var mListStd = mutableListOf<StudentListClass>()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imgBackground = view.findViewById<ImageView>(R.id.imgStudentListBackground)
        val imgAddStudents = view.findViewById<ImageView>(R.id.imgAddStudents)
        val imgSaveAttendance = view.findViewById<ImageView>(R.id.imgSaveAttendance)
        val imgArrowBack = view.findViewById<ImageView>(R.id.imgArrowBack)
        ViewCompat.setTransitionName(imgBackground, "background")
        ViewCompat.setTransitionName(imgAddStudents, "imgAddIcon")
        ViewCompat.setTransitionName(imgSaveAttendance, "imgSaveAndHelp")
        ViewCompat.setTransitionName(imgArrowBack, "imgArrowBack")
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_student_list, container, false)

        sharedTransition = SharedTransition(activity as Context)

        subName = arguments?.getString("subName")
        sheetNo = arguments?.getInt("sheetNo")
        profName = arguments?.getString("profName")
        isActive = arguments?.getBoolean("isActive")!!

        /*setting entering transition for fragment*/
        sharedElementEnterTransition = TransitionInflater.from(activity as Context)
            .inflateTransition(R.transition.change_background_trans)

        view.txtSubNameStdList.text = subName
        view.txtProfNameStdList.text = "by $profName"

        (activity as AppCompatActivity).mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        setDB()
        gettingDataFromDatabase(view)

        if (isActive) {
            /*Making views Visible which are required in StudentListActivity when opened from Attendance sheet fragment*/
            if (mListStd.isNotEmpty()) {
                view.txtSelect.visibility = View.VISIBLE
                view.checkAll.visibility = View.VISIBLE
            }
            view.imgStudentListBackground.setBackgroundResource(R.drawable.dark_blue_backgound)
            view.imgAddStudents.visibility = View.VISIBLE
            view.imgSaveAttendance.visibility = View.VISIBLE
            setHasOptionsMenu(true)
        }

        view.imgAddStudents.setOnClickListener {
            startActivityForResult(
                Intent(activity as Context, AddStudentActivity::class.java), REQUEST_CODE
            )
        }

        /*setting animation for views*/
        val slideFromRight = AnimationUtils.loadAnimation(
            activity as Context,
            R.anim.slide_from_right
        )
        val normalRightSlide =
            AnimationUtils.loadAnimation(activity as Context, R.anim.layout_right_slide)
        view.stdRecyclerView.startAnimation(slideFromRight)
        view.txtSubNameStdList.startAnimation(slideFromRight)
        view.txtProfNameStdList.startAnimation(normalRightSlide)

        view.stdRecyclerView.layoutManager = LinearLayoutManager(activity as Context)

        view.stdRecyclerView.itemAnimator = DefaultItemAnimator()

        view.stdRecyclerView.isNestedScrollingEnabled = false

        buildRecyclerView(view)

        view.checkAll.setOnClickListener {
            onCheckedChanged(checkAll.isChecked, view)
        }

        view.imgSaveAttendance.setOnClickListener {
            saveAttendance()
        }

        view.imgArrowBack.setOnClickListener {
            arrowBackButton()
        }

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
            openFragmentWithTransition(AttendanceSheetFragment())
        }

        builder.setNegativeButton("Don't save") { dialog, _ ->
            dialog.dismiss()
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
        showAlertDialog = true
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

    private fun arrowBackButton() {
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
                    showAlertDialog = false
                    openFragmentWithTransition(AttendanceSheetFragment())
                }

                builder.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }

                builder.show()
            } else {
                openFragmentWithTransition(AttendanceSheetFragment())
            }
        } else {
            /*if StudentInformationFragment opens this StudentList Fragment
            *  Go back with shared transition animation*/
            openFragmentWithTransition(StudentInformationFragment())
        }
    }

    private fun openFragmentWithTransition(fragment: Fragment) {
        val replaceFragment = sharedTransition?.sharedEnterAndExitTrans(fragment)
        replaceFragment?.let {
            activity?.supportFragmentManager?.beginTransaction()
                ?.addSharedElement(imgStudentListBackground, "background")
                ?.addSharedElement(imgAddStudents, "imgAddIcon")
                ?.addSharedElement(imgSaveAttendance, "imgSaveAndHelp")
                ?.addSharedElement(imgArrowBack, "imgArrowBack")
                ?.replace(R.id.fragment_container, it)?.commit()
        }
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