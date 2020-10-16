package com.knesarcreation.attendanceapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.knesarcreation.attendanceapp.R
import com.knesarcreation.attendanceapp.database.StudentPastAttendance
import kotlinx.android.synthetic.main.recyler_student_single_row.view.*

class AdapterStdPastAttendance(
    val context: Context,
    private val listener: OnItemClickListener,
    private val mPastAttendanceHist: MutableList<StudentPastAttendance>
) :
    RecyclerView.Adapter<AdapterStdPastAttendance.MyViewHolder>() {


    interface OnItemClickListener {
        fun onItemClick(Position: Int, myViewHolder: MyViewHolder?)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var checkbox: CheckBox = itemView.mCheckBox
        var stdName: TextView = itemView.txtStudentName
        var stdUsn: TextView = itemView.txtStdUsn

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recyler_student_single_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.checkbox.visibility = View.VISIBLE

        holder.checkbox
            .setOnCheckedChangeListener { _, isChecked: Boolean ->
                mPastAttendanceHist[position].isChecked = isChecked
            }
        holder.stdName.text = mPastAttendanceHist[position].stdName
        holder.stdUsn.text = mPastAttendanceHist[position].stdUsn
        holder.checkbox.isChecked = mPastAttendanceHist[position].isChecked
        holder.checkbox.tag = mPastAttendanceHist[position]

        holder.checkbox.setOnClickListener {
            listener.onItemClick(position, holder)
        }
    }

    override fun getItemCount(): Int {
        return mPastAttendanceHist.size
    }
}