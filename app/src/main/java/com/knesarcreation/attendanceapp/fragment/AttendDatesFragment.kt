package com.knesarcreation.attendanceapp.fragment

import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.knesarcreation.attendanceapp.R
import com.knesarcreation.attendanceapp.adapter.AdapterHistoryDatesTime
import com.knesarcreation.attendanceapp.database.AttendanceDateTimes
import com.knesarcreation.attendanceapp.database.Database
import com.knesarcreation.attendanceapp.database.DatabaseInstance
import com.knesarcreation.attendanceapp.util.SharedTransition
import kotlinx.android.synthetic.main.fragment_attend_dates.*
import kotlinx.android.synthetic.main.fragment_attend_dates.view.*

class AttendDatesFragment : Fragment() {
    private var hisID: Int? = 0
    private var mAdapter: AdapterHistoryDatesTime? = null
    private var mDatabase: Database? = null
    private var mStdHistoryList = mutableListOf<AttendanceDateTimes>()
    private var profName: String? = ""
    private var subName: String? = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val itemImgBackGround = view.findViewById<ImageView>(R.id.imgHistDatesBackground)
        val imgBack = view.findViewById<ImageView>(R.id.imgArrowBackHistoryDates)
        ViewCompat.setTransitionName(itemImgBackGround, "hist_background")
        ViewCompat.setTransitionName(imgBack, "imgBack")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_attend_dates, container, false)

        setDB()
        if (arguments != null) {
            hisID = arguments?.getInt("hisId", 0)
            subName = arguments?.getString("subName")
            profName = arguments?.getString("profName")
        }

        view.txtTitleNameAttendDatesTimes.text = subName
        view.txtProfNameAttendDatesTimes.text = profName


        sharedElementEnterTransition = TransitionInflater.from(activity as Context)
            .inflateTransition(R.transition.change_background_trans)

        val slideFromRight = AnimationUtils.loadAnimation(
            activity as Context,
            R.anim.slide_from_right
        )
        val layoutRightSlide =
            AnimationUtils.loadAnimation(activity as Context, R.anim.layout_right_slide)

        view.txtTitleNameAttendDatesTimes.startAnimation(slideFromRight)
        view.txtProfNameAttendDatesTimes.startAnimation(slideFromRight)
        view.mRecyclerViewAttendHistoryDatesTimes.startAnimation(layoutRightSlide)

        try {
            for (i in mDatabase?.mDao()?.getHistoryAndDatesTimes(hisID!!)!!) {
                mStdHistoryList = i.attendanceDateTimes
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }

        if (mStdHistoryList.isNotEmpty()) {
            view.rlNoHistoryAttend.visibility = View.INVISIBLE
        }
        buildRecyclerView(view)

        view.imgArrowBackHistoryDates.setOnClickListener {
            fragmentManager?.beginTransaction()
                ?.addSharedElement(imgHistDatesBackground, "hist_background")
                ?.replace(R.id.fragment_container, AttendanceHistoryFragment())?.commit()
        }
        return view
    }

    private fun buildRecyclerView(view: View) {
        view.mRecyclerViewAttendHistoryDatesTimes.setHasFixedSize(true)
        view.mRecyclerViewAttendHistoryDatesTimes.layoutManager =
            LinearLayoutManager(activity as Context)
        mAdapter = AdapterHistoryDatesTime(
            activity as Context,
            mStdHistoryList,
            object : AdapterHistoryDatesTime.OnItemClickListener {
                override fun onItemClick(position: Int) {

                    val fragment = SharedTransition(activity as Context).sharedEnterAndExitTrans(
                        StudentPastAttendFragment()
                    )
                    val bundle = Bundle()
                    bundle.putInt("id", mStdHistoryList[position].id)
                    bundle.putString("dateTime", mStdHistoryList[position].dateTime)
                    bundle.putString("subName", subName)
                    bundle.putInt("hisId", hisID!!)
                    bundle.putString("profName", profName)

                    fragment.arguments = bundle
                    fragmentManager?.beginTransaction()?.addSharedElement(
                        imgHistDatesBackground, "hist_background"
                    )?.addSharedElement(imgArrowBackHistoryDates, "imgBack")
                        ?.replace(R.id.fragment_container, fragment)?.commit()
                }

            })
        view.mRecyclerViewAttendHistoryDatesTimes.adapter = mAdapter
    }

    private fun setDB() {
        mDatabase = DatabaseInstance().newInstance(activity as Context)
    }

}