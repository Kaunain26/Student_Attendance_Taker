package com.knesarcreation.attendanceapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.knesarcreation.attendanceapp.R
import com.knesarcreation.attendanceapp.database.AttendanceDateTimes
import kotlinx.android.synthetic.main.recycler_history_date_time_single_row.view.*


class AdapterHistoryDatesTime(
    private val context: Context,
    private val mAttendanceHist: List<AttendanceDateTimes>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<AdapterHistoryDatesTime.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contentLayout: RelativeLayout = itemView.mRelativeLayoutAttendHistory
        val dateAndTime: TextView = itemView.txtDateTimeList
    }

    interface OnItemClickListener{
       fun  onItemClick(position: Int)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view: View = LayoutInflater.from(context)
            .inflate(R.layout.recycler_history_date_time_single_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.dateAndTime.text = mAttendanceHist[position].dateTime
        holder.contentLayout.setOnClickListener {
            listener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return mAttendanceHist.size
    }
}