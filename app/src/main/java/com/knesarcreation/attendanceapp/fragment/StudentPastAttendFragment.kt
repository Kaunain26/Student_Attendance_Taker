package com.knesarcreation.attendanceapp.fragment

import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.knesarcreation.attendanceapp.R
import com.knesarcreation.attendanceapp.adapter.AdapterStdPastAttendance
import com.knesarcreation.attendanceapp.database.Database
import com.knesarcreation.attendanceapp.database.DatabaseInstance
import com.knesarcreation.attendanceapp.database.StudentPastAttendance
import com.knesarcreation.attendanceapp.util.SharedTransition
import kotlinx.android.synthetic.main.fragment_student_past_attend.*
import kotlinx.android.synthetic.main.fragment_student_past_attend.view.*

class StudentPastAttendFragment : Fragment(), AdapterStdPastAttendance.OnItemClickListener {
    private var id: Int? = 0
    private var subName: String? = ""
    private var showAlertDialog: Boolean = false
    private var mAdapter: AdapterStdPastAttendance? = null
    private var mDatabase: Database? = null
    private var mStdPastList = mutableListOf<StudentPastAttendance>()

    /*For saving position of checkbox which is checked or not*/
    private var positionList = mutableListOf<Int>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imgBack = view.imgArrowBack
        val imgBackground = view.imgStdPastAttendBackground
        ViewCompat.setTransitionName(imgBackground, "hist_background")
        ViewCompat.setTransitionName(imgBack, "imgBack")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_student_past_attend, container, false)

        setDB()

        id = arguments?.getInt("id", 0)
        subName = arguments?.getString("subName")
        val dateTime = arguments?.getString("dateTime")
        view.txtDateTimeStdPastAttend.text = dateTime
        view.txtSubNameFragmentPast.text = subName

        sharedElementEnterTransition = TransitionInflater.from(activity as Context)
            .inflateTransition(R.transition.change_background_trans)

        for (i in mDatabase?.mDao()?.getDatesTimesAndStudentHist(id!!.toInt())!!) {
            mStdPastList = i.studentPastAttendance
        }

        if (mStdPastList.isNotEmpty()) {
            view.rlNoPastAttendance.visibility = View.INVISIBLE
        }

        val slideFromRight = AnimationUtils.loadAnimation(
            activity as Context,
            R.anim.slide_from_right
        )
        val layoutRightSlide =
            AnimationUtils.loadAnimation(activity as Context, R.anim.layout_right_slide)

        view.txtDateTimeStdPastAttend.startAnimation(slideFromRight)
        view.txtSubNameFragmentPast.startAnimation(slideFromRight)
        view.mRecyclerViewPastAttd.startAnimation(layoutRightSlide)

        buildRecyclerView(view)

        saveAttendanceBtn(view)

        view.imgArrowBack.setOnClickListener {
            val fragment =
                SharedTransition(activity as Context).sharedEnterAndExitTrans(AttendDatesFragment())

            val bundle = Bundle()
            bundle.putString("profName", arguments?.getString("profName"))
            bundle.putInt("hisId", arguments?.getInt("hisId")!!)
            bundle.putString("subName", subName)
            fragment.arguments = bundle
            fragmentManager?.beginTransaction()
                ?.addSharedElement(imgStdPastAttendBackground, "hist_background")
                ?.addSharedElement(imgArrowBack, "imgBack")
                ?.replace(R.id.fragment_container, fragment)
                ?.commit()

            // TODO: 18-10-2020 OPEN REQUIRED FRAGMENT
        }

        return view
    }

    private fun saveAttendanceBtn(view: View) {
        view.imgSaveBtnPastAttend?.setOnClickListener {
            savePastAttendance()
            Toast.makeText(activity as Context, "Saved", Toast.LENGTH_SHORT).show()

            // TODO: 18-10-2020 OPEN REQUIRED FRAGMENT
        }
    }

    private fun buildRecyclerView(view: View) {
        view.mRecyclerViewPastAttd.setHasFixedSize(true)
        view.mRecyclerViewPastAttd.layoutManager = LinearLayoutManager(activity as Context)
        mAdapter = AdapterStdPastAttendance(activity as Context, this, mStdPastList)
        view.mRecyclerViewPastAttd.adapter = mAdapter
    }

    private fun savePastAttendance() {
        for (k in positionList) {

            /*Saving: The changes made in past attendance with respect to position which was added to positionList*/
            if (mStdPastList[k].isChecked) {
                mDatabase?.mDao()
                    ?.updateStudentPastAttendance(true, mStdPastList[k].id)

            } else {
                mDatabase?.mDao()
                    ?.updateStudentPastAttendance(false, mStdPastList[k].id)
            }
        }
    }

    private fun setDB() {
        mDatabase = DatabaseInstance().newInstance(activity as Context)
    }

    override fun onItemClick(
        Position: Int,
        myViewHolder: AdapterStdPastAttendance.MyViewHolder?
    ) {
        showAlertDialog = true
        /*Adding position to positionList when checkbox is checked*/
        positionList.add(Position)
        if (myViewHolder?.checkbox?.isChecked!!) {
            for (i in mDatabase?.mDao()?.getStudentDetails(mStdPastList[Position].stdUsn!!)!!) {
                mDatabase?.mDao()
                    ?.updateStudentPresentStatus(i.present + 1, mStdPastList[Position].stdUsn!!)

                mDatabase?.mDao()
                    ?.updateStudentAbsentStatus(i.absent - 1, mStdPastList[Position].stdUsn!!)
            }
        } else {

            for (i in mDatabase?.mDao()?.getStudentDetails(mStdPastList[Position].stdUsn!!)!!) {
                mDatabase?.mDao()
                    ?.updateStudentAbsentStatus(i.absent + 1, mStdPastList[Position].stdUsn!!)

                mDatabase?.mDao()
                    ?.updateStudentPresentStatus(i.present - 1, mStdPastList[Position].stdUsn!!)
            }
        }
    }
}

