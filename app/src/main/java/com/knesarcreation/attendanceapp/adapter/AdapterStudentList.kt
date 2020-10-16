package com.knesarcreation.attendanceapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.knesarcreation.attendanceapp.R
import com.knesarcreation.attendanceapp.activity.StudentDetailsActivity
import com.knesarcreation.attendanceapp.database.StudentListClass


class AdapterStudentList(
    private val context: Context,
    private val isActive: Boolean,
    private val listener: OnItemClickListener,
    private val lstStudent: List<StudentListClass>
) :
    RecyclerView.Adapter<AdapterStudentList.ViewHolder>() {

    interface OnItemClickListener {
        //        fun onContentLayoutClick(i: Int)
        fun onCheckBoxClicked(position: Int, viewHolder: ViewHolder?)
    }

    fun toggleSelection(isChecked: Boolean) {
        if (lstStudent.isNotEmpty()) {
            for (i in lstStudent) {
                i.isChecked = isChecked
            }
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var checkbox: CheckBox = view.findViewById(R.id.mCheckBox)
        var contentLayout: RelativeLayout = view.findViewById(R.id.rlStdPastAttendance)
        var imgArrowNext: ImageView = view.findViewById(R.id.imgArrowNext)
        var stdName: TextView = view.findViewById(R.id.txtStudentName)
        var stdUsn: TextView = view.findViewById(R.id.txtStdUsn)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View =
            LayoutInflater.from(context).inflate(R.layout.recyler_student_single_row, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (isActive) {
            holder.checkbox.visibility = View.VISIBLE
            holder.checkbox
                .setOnCheckedChangeListener { _, isChecked: Boolean ->
                    lstStudent[position].isChecked = isChecked
                }

            holder.stdName.text = lstStudent[position].stdName
            holder.stdUsn.text = lstStudent[position].stdUsn
            holder.checkbox.isChecked = lstStudent[position].isChecked
            holder.checkbox.tag = lstStudent[position]
            holder.checkbox
                .setOnClickListener {
                    listener.onCheckBoxClicked(position, holder)
                }

        } else {
            holder.checkbox.visibility = View.INVISIBLE
            holder.imgArrowNext.visibility = View.VISIBLE
            holder.stdName.text = lstStudent[position].stdName
            holder.stdUsn.text = lstStudent[position].stdUsn
            holder.contentLayout.setOnClickListener {
                val studentDetailsActivity = Intent(context, StudentDetailsActivity::class.java)
                studentDetailsActivity.putExtra("stdUsn", lstStudent[position].stdUsn)
                context.startActivity(studentDetailsActivity)
            }
        }
    }

    override fun getItemCount(): Int {
        return lstStudent.size
    }
}