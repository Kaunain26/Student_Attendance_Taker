package com.knesarcreation.attendanceapp.fragment

import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.knesarcreation.attendanceapp.R
import com.knesarcreation.attendanceapp.adapter.AdapterAttendanceSheet
import com.knesarcreation.attendanceapp.database.AttendanceSheet
import com.knesarcreation.attendanceapp.database.Database
import com.knesarcreation.attendanceapp.database.DatabaseInstance
import com.knesarcreation.attendanceapp.util.SharedTransition
import kotlinx.android.synthetic.main.activity_main_screen.*
import kotlinx.android.synthetic.main.fragment_attendance_sheet.*
import kotlinx.android.synthetic.main.fragment_attendance_sheet.view.*

class StudentInformationFragment : Fragment(), AdapterAttendanceSheet.OnItemClickListener {
    private var mAdapter: AdapterAttendanceSheet? = null
    private var mAttendanceList = mutableListOf<AttendanceSheet>()
    private var mDatabase: Database? = null
    private var isActive = false
    /* private var clickedOn = true*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_attendance_sheet, container, false)
        (activity as AppCompatActivity).mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

        view.imgCreateSheet.visibility = View.INVISIBLE
        view.imgHelp.visibility = View.INVISIBLE

        view.txtTitleNameAttendSheet.text = "Student\nInformations"

        /*setting background*/
        view.imgAttendanceSheetBackground.setBackgroundResource(
            R.drawable.stud_details_background
        )
        view.hintMessageMainScreen.text = "Oops! No Info Found"
        view.hintSmallMessageMainScreen.text = "Make Sure You Have Created Attendance Sheet"

        /*SharedElement Entering transition*/
        sharedElementEnterTransition = TransitionInflater.from(activity as Context)
            .inflateTransition(R.transition.change_background_trans)

        /*setting database*/
        setDB()

        /*open drawer*/
        view.imgNavigateBtn.setOnClickListener {
            (activity as AppCompatActivity).mDrawerLayout.openDrawer(GravityCompat.START)
        }

        /*setting animation to views*/
        val slideFromRight = AnimationUtils.loadAnimation(
            activity as Context,
            R.anim.slide_from_right
        )
        val layoutRightSlide =
            AnimationUtils.loadAnimation(activity as Context, R.anim.layout_right_slide)

        view.txtTitleNameAttendSheet.startAnimation(
            slideFromRight
        )
        view.mRecyclerView.startAnimation(layoutRightSlide)

        view.mRecyclerView.setHasFixedSize(true)
        view.mRecyclerView.layoutManager = LinearLayoutManager(activity as Context)

        /*getting data from database*/
        gettingDataFromDatabase()
        buildRecyclerView(view)

        if (mAttendanceList.isNotEmpty()) {
            view.llNoAttendanceSheets.visibility = View.INVISIBLE
        }
        return view
    }

    private fun buildRecyclerView(view: View) {
        mAdapter = AdapterAttendanceSheet(
            activity as Context,
            this, mAttendanceList, fragmentManager
        )
        mAdapter?.notifyDataSetChanged()
        view.mRecyclerView.adapter = mAdapter
    }

    private fun gettingDataFromDatabase() {
        mAttendanceList = mDatabase?.mDao()?.getAllAttendanceSheets()!!
    }

    override fun onResume() {
        super.onResume()
        mAdapter?.notifyDataSetChanged()
    }

    private fun setDB() {
        mDatabase = DatabaseInstance().newInstance(activity as Context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val itemImgBackGround = view.findViewById<ImageView>(R.id.imgAttendanceSheetBackground)
        val imgCreateSheet = view.findViewById<ImageView>(R.id.imgCreateSheet)
        val imgHelp = view.findViewById<ImageView>(R.id.imgHelp)
        val imgNavigateBtn = view.findViewById<ImageView>(R.id.imgNavigateBtn)
        ViewCompat.setTransitionName(itemImgBackGround, "background")
        ViewCompat.setTransitionName(imgCreateSheet, "imgAddIcon")
        ViewCompat.setTransitionName(imgHelp, "imgSaveAndHelp")
        ViewCompat.setTransitionName(imgNavigateBtn, "imgArrowBack")
    }

    override fun onClick(position: Int, viewHolder: AdapterAttendanceSheet.MyViewHolder) {
        val studentListFragment =
            SharedTransition(activity as Context).sharedEnterAndExitTrans(StudentListFragment())
        val bundle = Bundle()
        bundle.putInt("sheetNo", mAttendanceList[position].sheetNo)
        bundle.putString("subName", mAttendanceList[position].subName)
        bundle.putString("profName", mAttendanceList[position].profName)
        bundle.putBoolean("isActive", isActive)
        studentListFragment.arguments = bundle

        fragmentManager?.beginTransaction()
            ?.addSharedElement(imgAttendanceSheetBackground, "background")
            ?.addSharedElement(imgCreateSheet, "imgAddIcon")
            ?.addSharedElement(imgHelp, "imgSaveAndHelp")
            ?.addSharedElement(imgNavigateBtn, "imgArrowBack")
            ?.replace(R.id.fragment_container, studentListFragment)
            ?.replace(R.id.fragment_container, studentListFragment)?.commit()
    }
}