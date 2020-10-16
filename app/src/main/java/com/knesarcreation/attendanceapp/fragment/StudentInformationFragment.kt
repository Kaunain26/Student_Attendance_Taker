package com.knesarcreation.attendanceapp.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.knesarcreation.attendanceapp.R
import com.knesarcreation.attendanceapp.adapter.AdapterAttendanceSheet
import com.knesarcreation.attendanceapp.database.AttendanceSheet
import com.knesarcreation.attendanceapp.database.Database
import com.knesarcreation.attendanceapp.database.DatabaseInstance
import kotlinx.android.synthetic.main.fragment_attendance_sheet.view.*

class StudentInformationFragment : Fragment() {
    var mAdapter: AdapterAttendanceSheet? = null
    private var mAttendanceList = mutableListOf<AttendanceSheet>()
    private var mDatabase: Database? = null
    private val clickedOn = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_attendance_sheet, container, false)
        view.txtTitleNameAttendSheet.text = "Student\nInformations"
        view.contentView.setBackgroundResource(
            R.drawable.attendance_sheet_background
        )

        view.hintMessageMainScreen.text = "Oops! No Info Found"
        view.hintSmallMessageMainScreen.text = "Make Sure You Have Created Attendance Sheet"

        setDB()
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
            false, clickedOn, mAttendanceList, fragmentManager
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
}