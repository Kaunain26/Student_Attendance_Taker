package com.knesarcreation.attendanceapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.knesarcreation.attendanceapp.R
import com.knesarcreation.attendanceapp.activity.AttendanceDatesTimeActivity
import com.knesarcreation.attendanceapp.database.AttendanceSheet
import com.knesarcreation.attendanceapp.fragment.StudentListFragment


class AdapterAttendanceSheet(
    private val context: Context,
    private val isActive: Boolean,
    private val clickedOn: Boolean,
    var mAttendanceList: MutableList<AttendanceSheet>,
    var fragmentManager: FragmentManager?
) :
    RecyclerView.Adapter<AdapterAttendanceSheet.MyViewHolder>() {

    /* compiled from: AdapterAttendanceSheet.kt */
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtLecturerName: TextView = itemView.findViewById(R.id.txtLecturerName)
        val txtSubjectName = itemView.findViewById<View>(R.id.txtSubjectName) as TextView
        val txtClassYear = itemView.findViewById<View>(R.id.txtClassYear) as TextView
        val txtSubCode = itemView.findViewById<View>(R.id.txtSubCode) as TextView
        val contentLayout =
            itemView.findViewById<View>(R.id.contentLayout) as RelativeLayout
        val totalStd = itemView.findViewById<View>(R.id.txtTotalStud) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(context)
            .inflate(R.layout.recycler_attendance_sheet_single_row, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val attendanceSheetModel: AttendanceSheet = mAttendanceList[position]
        holder.txtLecturerName.text = "by ${attendanceSheetModel.profName}"
        holder.txtClassYear.text = attendanceSheetModel.classYear
        holder.txtSubCode.text = attendanceSheetModel.subCode
        holder.txtSubjectName.text = attendanceSheetModel.subName
        holder.totalStd.text = attendanceSheetModel.totalStud.toString()

        holder.contentLayout.setOnClickListener {
            if (clickedOn) {
                /*   val studentListActivity =
                       Intent(context, StudentListActivity::class.java)
                   studentListActivity.putExtra("sheetNo", attendanceSheetModel.sheetNo)
                   studentListActivity.putExtra("subName", attendanceSheetModel.subName)
                   studentListActivity.putExtra("profName", attendanceSheetModel.profName)
                   studentListActivity.putExtra("isActive", isActive)

                   val options: ActivityOptions = ActivityOptions.makeSceneTransitionAnimation(
                       context as Activity?,
                       view.findViewById(R.id.contentView),
                       "activityBackGroundImg"
                   )
                   context.startActivity(studentListActivity, options.toBundle())*/
                val studentListFragment = StudentListFragment()
                val bundle = Bundle()
                bundle.putInt("sheetNo", attendanceSheetModel.sheetNo)
                bundle.putString("subName", attendanceSheetModel.subName)
                bundle.putString("profName", attendanceSheetModel.profName)
                bundle.putBoolean("isActive", isActive)
                studentListFragment.arguments = bundle

                fragmentManager?.beginTransaction()
                    ?.replace(R.id.fragment_container, studentListFragment)?.commit()


            } else {
                val startAttendanceHistActivity =
                    Intent(context, AttendanceDatesTimeActivity::class.java)
                startAttendanceHistActivity.putExtra("hisId", attendanceSheetModel.sheetNo)
                startAttendanceHistActivity.putExtra("subName", attendanceSheetModel.subName)
                startAttendanceHistActivity.putExtra("profName", attendanceSheetModel.profName)
                context.startActivity(startAttendanceHistActivity)

            }
        }
    }

    override fun getItemCount(): Int {
        return mAttendanceList.size
    }
}