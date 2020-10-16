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
import com.knesarcreation.attendanceapp.database.AttendanceHistory
import com.knesarcreation.attendanceapp.database.AttendanceSheet
import com.knesarcreation.attendanceapp.database.Database
import com.knesarcreation.attendanceapp.database.DatabaseInstance
import kotlinx.android.synthetic.main.fragment_attnd_history.view.*

class AttendanceHistoryFragment : Fragment() {
    private var mAdapter: AdapterAttendanceSheet? = null
    private var mDatabase: Database? = null
    private var mStdHistoryList = mutableListOf<AttendanceHistory>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_attnd_history, container, false)

        setDB()

        mStdHistoryList = mDatabase?.mDao()?.getAllAttendanceHistory()!!

        if (mStdHistoryList.isNotEmpty()) {
            view.imgNoRecordsAttendHistory.visibility = View.INVISIBLE
            view.hintMessageAttendHistory.visibility = View.INVISIBLE
        }
        view.mRecyclerViewFragmentHistory.setHasFixedSize(true)
        buildRecyclerView(view)
        return view
    }

    private fun buildRecyclerView(view: View) {

        view.mRecyclerViewFragmentHistory.layoutManager = LinearLayoutManager(context)
        val attendanceHistoryList = mutableListOf<AttendanceSheet>()
        for (list in mStdHistoryList) {
            val attendanceSheet = AttendanceSheet(
                list.hisID,
                list.subName,
                list.classYear,
                list.profName,
                list.totalStud,
            )
            attendanceHistoryList.add(attendanceSheet)
            if (list.subCode.isNotEmpty()) {
                attendanceSheet.subCode = list.subCode
            }
        }
        mAdapter = AdapterAttendanceSheet(
            activity as Context,
            isActive = false,
            clickedOn = false,
            mAttendanceList = attendanceHistoryList,
            fragmentManager
        )

        view.mRecyclerViewFragmentHistory.adapter = mAdapter
    }

    private fun setDB() {
        mDatabase = DatabaseInstance().newInstance(activity as Context)
    }
}
