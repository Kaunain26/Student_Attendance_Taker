/*
package com.knesarcreation.attendanceapp.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.knesarcreation.attendanceapp.adapter.AdapterHistoryDatesTime
import com.knesarcreation.attendanceapp.R
import com.knesarcreation.attendanceapp.database.AttendanceDateTimes
import com.knesarcreation.attendanceapp.database.Database
import com.knesarcreation.attendanceapp.database.DatabaseInstance
import kotlinx.android.synthetic.main.activity_attendance_history_dates_time.*

class AttendanceDatesTimeActivity : AppCompatActivity() {
    private var hisID: Int? = 0
    private var mAdapter: AdapterHistoryDatesTime? = null
    private var mDatabase: Database? = null
    private var mStdHistoryList = mutableListOf<AttendanceDateTimes>()
    private var profName: String? = ""
    private var subName: String? = ""

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance_history_dates_time)

        setDB()
        hisID = intent?.getIntExtra("hisId", 0)
        subName = intent?.getStringExtra("subName")
        profName = intent?.getStringExtra("profName")


        txtTitleNameAttendDatesTimes.text = subName
        txtProfNameAttendDatesTimes.text = profName

        for (i in mDatabase?.mDao()?.getHistoryAndDatesTimes(hisID!!)!!) {
            mStdHistoryList = i.attendanceDateTimes
        }
        if (mStdHistoryList.isNotEmpty()) {

            imgNoRecordsHistDatesTime.visibility = View.INVISIBLE
            hintMessageHistDatesTime.visibility = View.INVISIBLE
            hintSmallMessageHistDatesTime.visibility = View.INVISIBLE
        }
        buildRecyclerView()
        btnBackHistoryDatesTIme.setOnClickListener {
            super.onBackPressed()
        }
    }

    private fun buildRecyclerView() {
        mRecyclerViewAttendHistoryDatesTimes.setHasFixedSize(true)
        mRecyclerViewAttendHistoryDatesTimes.layoutManager = LinearLayoutManager(this)
        mAdapter = AdapterHistoryDatesTime(
            this,
            mStdHistoryList,
            object : AdapterHistoryDatesTime.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    val intent =
                        Intent(this@AttendanceDatesTimeActivity, StudentsPastAttendance::class.java)
                    intent.putExtra("id", mStdHistoryList[position].id)
                    intent.putExtra("dateTime", mStdHistoryList[position].dateTime)
                    intent.putExtra("subName", subName)
                    startActivity(intent)
                }

            })
        mRecyclerViewAttendHistoryDatesTimes.adapter = mAdapter
    }

    private fun setDB() {
        mDatabase = DatabaseInstance().newInstance(this)
    }
}
*/
