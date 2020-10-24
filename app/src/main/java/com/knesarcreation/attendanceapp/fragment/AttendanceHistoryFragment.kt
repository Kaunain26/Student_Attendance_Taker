package com.knesarcreation.attendanceapp.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.knesarcreation.attendanceapp.R
import com.knesarcreation.attendanceapp.adapter.AdapterAttendanceSheet
import com.knesarcreation.attendanceapp.database.AttendanceHistory
import com.knesarcreation.attendanceapp.database.AttendanceSheet
import com.knesarcreation.attendanceapp.database.Database
import com.knesarcreation.attendanceapp.database.DatabaseInstance
import com.knesarcreation.attendanceapp.util.SharedTransition
import kotlinx.android.synthetic.main.activity_main_screen.*
import kotlinx.android.synthetic.main.fragment_attnd_history.*
import kotlinx.android.synthetic.main.fragment_attnd_history.view.*

class AttendanceHistoryFragment : Fragment(), AdapterAttendanceSheet.OnItemClickListener {
    private var mAdapter: AdapterAttendanceSheet? = null
    private var mDatabase: Database? = null
    private var mStdHistoryList = mutableListOf<AttendanceHistory>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val itemImgBackGround = view.findViewById<ImageView>(R.id.imgHistoryBackground)
        ViewCompat.setTransitionName(itemImgBackGround, "hist_background")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_attnd_history, container, false)

        setDB()
        view.imgNavigateBtn.setOnClickListener {
            (activity as AppCompatActivity).mDrawerLayout.openDrawer(GravityCompat.START)
        }

        mStdHistoryList = mDatabase?.mDao()?.getAllAttendanceHistory()!!

        if (mStdHistoryList.isNotEmpty()) {
            view.imgNoRecordsAttendHistory.visibility = View.INVISIBLE
            view.hintMessageAttendHistory.visibility = View.INVISIBLE
        }

        val slideFromRight = AnimationUtils.loadAnimation(
            activity as Context,
            R.anim.slide_from_right
        )
        val layoutRightSlide =
            AnimationUtils.loadAnimation(activity as Context, R.anim.layout_right_slide)

        view.txtTitleNameFragmentHistory.startAnimation(slideFromRight)
        view.mRecyclerViewFragmentHistory.startAnimation(layoutRightSlide)

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
            this,
            attendanceHistoryList,
            fragmentManager
        )

        view.mRecyclerViewFragmentHistory.adapter = mAdapter
    }

    private fun setDB() {
        mDatabase = DatabaseInstance().newInstance(activity as Context)
    }

    override fun onClick(position: Int, viewHolder: AdapterAttendanceSheet.MyViewHolder) {
        val attendDatesFragment = AttendDatesFragment()

        val fragment =
            SharedTransition(activity as Context).sharedEnterAndExitTrans(attendDatesFragment)
        val bundle = Bundle()

        bundle.putInt("hisId", mStdHistoryList[position].hisID)
        bundle.putString("subName", mStdHistoryList[position].subName)
        bundle.putString("profName", mStdHistoryList[position].profName)
        attendDatesFragment.arguments = bundle

        fragmentManager?.beginTransaction()
            ?.addSharedElement(imgHistoryBackground, "hist_background")
            ?.replace(R.id.fragment_container, fragment)
            ?.commit()
    }
}
