package com.knesarcreation.attendanceapp.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.knesarcreation.attendanceapp.R
import com.knesarcreation.attendanceapp.adapter.AdapterAttendanceSheet
import com.knesarcreation.attendanceapp.database.AttendanceSheet
import com.knesarcreation.attendanceapp.database.Database
import com.knesarcreation.attendanceapp.database.DatabaseInstance
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
        view.txtTitleNameAttendSheet.text = "Student\nInformations"

        view.imgAttendanceSheetBackground.setBackgroundResource(
            R.drawable.attendance_sheet_background
        )

        view.hintMessageMainScreen.text = "Oops! No Info Found"
        view.hintSmallMessageMainScreen.text = "Make Sure You Have Created Attendance Sheet"

        setDB()

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

    override fun onClick(position: Int, viewHolder: AdapterAttendanceSheet.MyViewHolder) {
        /*if (clickedOn) {*/
        val studentListFragment = StudentListFragment()
        val bundle = Bundle()
        bundle.putInt("sheetNo", mAttendanceList[position].sheetNo)
        bundle.putString("subName", mAttendanceList[position].subName)
        bundle.putString("profName", mAttendanceList[position].profName)
        bundle.putBoolean("isActive", isActive)
        studentListFragment.arguments = bundle

        fragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, studentListFragment)?.commit()
//        }
    }
}