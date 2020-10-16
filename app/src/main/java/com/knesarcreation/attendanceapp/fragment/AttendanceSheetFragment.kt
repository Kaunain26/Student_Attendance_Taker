package com.knesarcreation.attendanceapp.fragment

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.knesarcreation.attendanceapp.R
import com.knesarcreation.attendanceapp.activity.CreateAttendanceSheet
import com.knesarcreation.attendanceapp.adapter.AdapterAttendanceSheet
import com.knesarcreation.attendanceapp.database.AttendanceHistory
import com.knesarcreation.attendanceapp.database.AttendanceSheet
import com.knesarcreation.attendanceapp.database.Database
import com.knesarcreation.attendanceapp.database.DatabaseInstance
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.activity_main_screen.*
import kotlinx.android.synthetic.main.fragment_attendance_sheet.view.*


class AttendanceSheetFragment : Fragment() {

    var sheetNo = 0
    var isActive = true
    private val clickedOn = true
    private lateinit var mAdapter: AdapterAttendanceSheet
    var mAttendanceList = mutableListOf<AttendanceSheet>()
    var mDatabase: Database? = null

    companion object {
        const val EDIT_REQUEST_CODE = 2
        const val REQUEST_CODE = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_attendance_sheet, container, false)

        view.txtTitleNameAttendSheet.text = "Attendance Sheets"
        isActive = true

        setHasOptionsMenu(true)

        (activity as AppCompatActivity).toolbar.setBackgroundResource(R.drawable.attendance_sheet_background)

        /*Setting up database*/
        setDB()


        view.mRecyclerView.setHasFixedSize(true)
        view.mRecyclerView.layoutManager = LinearLayoutManager(activity as Context)

        gettingDataFromDatabase(view)

        buildRecyclerView(view)

        var deletedSheet: AttendanceSheet?
        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                deletedSheet = mAttendanceList[position]
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        val builder = AlertDialog.Builder(activity as Context)
                        builder.setTitle("Alert!!" as CharSequence)
                        builder.setMessage("Your Current Attendance Sheet with Past Records Will be Deleted. Do You Want To Delete" as CharSequence)
                        builder.setPositiveButton(
                            "YES"
                        ) { dialog, _ ->
                            mDatabase?.mDao()
                                ?.deleteAttendanceSheet(mAttendanceList[position].sheetNo)
                            mDatabase?.mDao()
                                ?.deleteAttendanceHistory(mAttendanceList[position].sheetNo)
                            mDatabase?.mDao()?.deleteStdDetails(mAttendanceList[position].sheetNo)
                            mDatabase?.mDao()
                                ?.deleteAttendanceHistoryDateTimes(mAttendanceList[position].sheetNo)

                            mAttendanceList.remove(deletedSheet)
                            if (mAttendanceList.isEmpty()) {
                                view.llNoAttendanceSheets.visibility = View.VISIBLE
                            }
                            mAdapter.notifyItemRemoved(position)
                            dialog.dismiss()
                        }
                        builder.setNegativeButton(
                            "NO"
                        ) { dialog, _ ->
                            mAdapter.notifyDataSetChanged()
                            dialog.dismiss()
                        }

                        builder.setCancelable(false)
                        builder.show()
                    }

                    ItemTouchHelper.RIGHT -> {
                        sheetNo = mAttendanceList[position].sheetNo
                        val intent =
                            Intent(activity as Context, CreateAttendanceSheet::class.java)
                        intent.putExtra(
                            CreateAttendanceSheet.EXTRA_SUB_NAME,
                            mAttendanceList[position].subName
                        )
                        intent.putExtra(
                            CreateAttendanceSheet.EXTRA_PROF_NAME,
                            mAttendanceList[position].profName
                        )
                        intent.putExtra(
                            CreateAttendanceSheet.EXTRA_CLASS_YEAR,
                            mAttendanceList[position].classYear
                        )
                        intent.putExtra(
                            CreateAttendanceSheet.EXTRA_SUB_CODE,
                            mAttendanceList[position].subCode
                        )
                        /*  val attendanceSheetFragment: AttendanceSheetFragment = this.`this$0`
                          attendanceSheetFragment.f101id =
                              Integer.valueOf((attendanceSheetFragment.mAttendanceList[position] as StudentsAttendanceSheet).getId())*/
                        startActivityForResult(intent, EDIT_REQUEST_CODE)
                    }
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val builder: RecyclerViewSwipeDecorator.Builder =
                    RecyclerViewSwipeDecorator.Builder(
                        context,
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                builder.addSwipeLeftBackgroundColor(
                    ContextCompat.getColor(
                        activity as Context,
                        R.color.lightRed
                    )
                )
                builder.addSwipeRightBackgroundColor(
                    ContextCompat.getColor(
                        activity as Context,
                        R.color.lightYellow
                    )
                ).addSwipeRightLabel("Edit").setSwipeLeftLabelTextSize(1, 16.0f)
                    .addSwipeLeftLabel("Delete").setSwipeLeftLabelTextSize(1, 16.0f).create()
                    .decorate()
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }

        }).attachToRecyclerView(view.mRecyclerView)
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

            val profName = data!!.getStringExtra(CreateAttendanceSheet.EXTRA_PROF_NAME)
            val subName = data.getStringExtra(CreateAttendanceSheet.EXTRA_SUB_NAME)
            val subCode = data.getStringExtra(CreateAttendanceSheet.EXTRA_SUB_CODE)
            val chooseYear = data.getStringExtra(CreateAttendanceSheet.EXTRA_CLASS_YEAR)
            val id = System.currentTimeMillis().toInt()

            val currentStdAttendanceSheet = AttendanceSheet(
                id, subName!!, chooseYear!!,
                profName!!, 0
            )

            if (!subCode!!.isBlank()) {
                currentStdAttendanceSheet.subCode = ("($subCode)")
            }
            mAttendanceList.add(currentStdAttendanceSheet)
            mDatabase!!.mDao().insertAttendanceSheet(currentStdAttendanceSheet)

            val currentAttendanceHistory = AttendanceHistory(
                id,
                subName,
                chooseYear,
                profName,
                0,
            )
            if (subCode.isNotBlank()) {
                currentAttendanceHistory.subCode = "($subCode)"
            }
            mDatabase!!.mDao().insertAttendanceHistory(currentAttendanceHistory)

            view?.let { gettingDataFromDatabase(it) }

            view?.let { buildRecyclerView(it) }

            val adapterAttendanceSheet: AdapterAttendanceSheet? = mAdapter

            adapterAttendanceSheet?.notifyDataSetChanged()

        } else if (requestCode == EDIT_REQUEST_CODE && resultCode == RESULT_OK) {

            var subjectCode = data?.getStringExtra(CreateAttendanceSheet.EXTRA_SUB_CODE)
            if (subjectCode!!.isNotEmpty()) {
                subjectCode = "($subjectCode)"
            }
            mDatabase?.mDao()?.editAttendanceSheet(
                data!!.getStringExtra(CreateAttendanceSheet.EXTRA_SUB_NAME),
                subjectCode,
                data.getStringExtra(CreateAttendanceSheet.EXTRA_PROF_NAME),
                data.getStringExtra(CreateAttendanceSheet.EXTRA_CLASS_YEAR),
                sheetNo
            )
            mDatabase?.mDao()
                ?.editAttendanceHistory(
                    data!!.getStringExtra(CreateAttendanceSheet.EXTRA_SUB_NAME),
                    subjectCode,
                    data.getStringExtra(CreateAttendanceSheet.EXTRA_PROF_NAME),
                    data.getStringExtra(CreateAttendanceSheet.EXTRA_CLASS_YEAR),
                    sheetNo
                )

            view?.let { gettingDataFromDatabase(it) }

            view?.let { buildRecyclerView(it) }

            mAdapter.notifyDataSetChanged()
        }
    }

    private fun buildRecyclerView(view: View) {
        mAdapter = AdapterAttendanceSheet(
            activity as Context,
            isActive,
            clickedOn,
            mAttendanceList,
            fragmentManager
        )
        mAdapter.notifyDataSetChanged()
        view.mRecyclerView.adapter = mAdapter
    }

    private fun gettingDataFromDatabase(view: View) {
        mAttendanceList = mDatabase?.mDao()?.getAllAttendanceSheets()!!
        if (mAttendanceList.isNotEmpty()) {
            view.llNoAttendanceSheets.visibility = View.INVISIBLE
        }
    }

    /* private fun callCreateAttendanceSheet(view: View) {
         view.btnCreateSheet.setOnClickListener {
             startActivityForResult(
                 Intent(activity as Context, CreateAttendanceSheet::class.java),
                 REQUEST_CODE
             )
         }
     }*/

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.attendance_sheet_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.ic_help -> {

                val builder = AlertDialog.Builder(activity as Context)
                val dialogLayout: View = layoutInflater
                    .inflate(R.layout.help_dialog, null)
                val imgView: ImageView =
                    dialogLayout.findViewById<View>(R.id.imgSwipe) as ImageView
                val txtView = dialogLayout.findViewById<View>(R.id.txtRightSwipe) as TextView
                val btnNext: Button = dialogLayout.findViewById<View>(R.id.btnNext) as Button
                val btnGotIt: Button =
                    dialogLayout.findViewById<View>(R.id.btnGotIt) as Button

                btnNext.visibility = View.VISIBLE
                builder.setView(dialogLayout)
                val dialog: AlertDialog = builder.create()

                btnNext.setOnClickListener {

                    btnNext.visibility = View.INVISIBLE
                    btnGotIt.visibility = View.VISIBLE
                    imgView.setImageResource(R.drawable.swipe_left)
                    txtView.text = "Do Right Swipe To ' Edit ' Any Item"
                }
                btnGotIt.setOnClickListener { dialog.dismiss() }
                dialog.setCancelable(false)
                dialog.show()

            }

            R.id.ic_add -> {
                startActivityForResult(
                    Intent(activity as Context, CreateAttendanceSheet::class.java),
                    REQUEST_CODE
                )
            }

            android.R.id.home -> {
                (activity as AppCompatActivity).mDrawerLayout.openDrawer(GravityCompat.START)
            }
        }
        return true
    }

    override fun onResume() {
        super.onResume()

        view?.let { gettingDataFromDatabase(it) }

        view?.let { buildRecyclerView(it) }

        val adapterAttendanceSheet: AdapterAttendanceSheet? = mAdapter

        adapterAttendanceSheet?.notifyDataSetChanged()
        isActive = true
    }

    private fun setDB() {
        mDatabase = DatabaseInstance().newInstance(activity as Context)
    }
}
