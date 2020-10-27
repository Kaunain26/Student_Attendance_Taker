package com.knesarcreation.attendanceapp.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout.SimpleDrawerListener
import androidx.fragment.app.Fragment
import com.knesarcreation.attendanceapp.R
import com.knesarcreation.attendanceapp.database.Database
import com.knesarcreation.attendanceapp.database.DatabaseInstance
import com.knesarcreation.attendanceapp.fragment.*
import com.knesarcreation.attendanceapp.util.SharedTransition
import kotlinx.android.synthetic.main.activity_main_screen.*
import kotlinx.android.synthetic.main.fragment_attend_dates.*
import kotlinx.android.synthetic.main.fragment_student_list.*
import kotlinx.android.synthetic.main.fragment_student_list.imgArrowBack
import kotlinx.android.synthetic.main.fragment_student_past_attend.*


class MainScreenActivity : AppCompatActivity() {
    private var mDatabase: Database? = null

    companion object {
        const val END_SCALE = 0.7f
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)

        /*Open default fragment i.e., AttendanceSheetFragment*/
        fragmentTransaction(AttendanceSheetFragment())

        /* Setting click listener for menu items inside the navigation drawer*/
        navigationItemClickListener()

        animateNavigationDrawer()
        getSharedPreferences("SHARED_PREFS", 0).edit().putBoolean("Active", true).apply()
    }

    private fun navigationItemClickListener() {
        navMenuAttendance.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.attendanceHistory -> fragmentTransaction(AttendanceHistoryFragment())

                R.id.attendanceSheet -> fragmentTransaction(AttendanceSheetFragment())

                R.id.studentInformation -> fragmentTransaction(StudentInformationFragment())

            }
            mDrawerLayout.closeDrawers()
            return@setNavigationItemSelectedListener true
        }
    }

    private fun fragmentTransaction(fragment: Fragment) {

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment).commit()
        navMenuAttendance.setCheckedItem(R.id.attendanceSheet)
    }

    private fun animateNavigationDrawer() {
        //Add any color or remove it to use the default one!
        //To make it transparent use Color.Transparent in side setScrimColor();
        mDrawerLayout.setScrimColor(resources.getColor(R.color.lightPink))

        mDrawerLayout.addDrawerListener(object : SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

                // Scale the View based on current slide offset
                val diffScaledOffset: Float = slideOffset * (1 - END_SCALE)
                val offsetScale = 1 - diffScaledOffset
                contentView.scaleX = offsetScale
                contentView.scaleY = offsetScale

                // Translate the View, accounting for the scaled width
                val xOffset: Float = drawerView.width * slideOffset
                val xOffsetDiff = contentView.width * diffScaledOffset / 2
                val xTranslation = xOffset - xOffsetDiff
                contentView.translationX = xTranslation
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBackPressed() {
        val findFragmentById = supportFragmentManager.findFragmentById(R.id.fragment_container)
        when {
            mDrawerLayout.isDrawerVisible(GravityCompat.START) -> {
                mDrawerLayout.closeDrawer(GravityCompat.START)
            }

            findFragmentById is StudentListFragment -> {
                mDatabase = DatabaseInstance().newInstance(this)

                /*If AttendanceSheetFragment opens StudentListFragment*/
                if (StudentListFragment.isActive) {

                    if (StudentListFragment.showAlertDialog) {
                        /*If AttendanceSheet Fragment opens StudentListFragment*/
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Alert!!")
                        builder.setMessage("Attendance isn't saved. Do you want to go back?")
                        builder.setPositiveButton("Yes") { _, _ ->
                            for (i in StudentListFragment.mListStd) {
                                mDatabase?.mDao()?.updateStudentList(false, i.stdUsn)
                            }
                            StudentListFragment.showAlertDialog = false

                            val fragment =
                                SharedTransition(this).sharedEnterAndExitTrans(
                                    AttendanceSheetFragment()
                                )
                            /*open AttendanceHistoryFragment with transition*/
                            openFragmentWithTransition(fragment)
                        }

                        builder.setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                        builder.show()
                    } else {

                        val fragment =
                            SharedTransition(this).sharedEnterAndExitTrans(
                                AttendanceSheetFragment()
                            )
                        /*open AttendanceHistoryFragment with transition*/
                        openFragmentWithTransition(fragment)
                    }
                } else {
                    /*if StudentInformationFragment opens StudentListFragment*/

                    val fragment =
                        SharedTransition(this).sharedEnterAndExitTrans(
                            StudentInformationFragment()
                        )
                    /*open StudentInformation fragment with transition*/
                    openFragmentWithTransition(fragment)
                }
            }

            findFragmentById is AttendDatesFragment -> {

                /*open AttendanceHistoryFragment with transition*/
                val replaceFragment =
                    SharedTransition(this).sharedEnterAndExitTrans(AttendanceHistoryFragment())

                supportFragmentManager.beginTransaction()
                    .addSharedElement(imgHistDatesBackground, "hist_background")
                    .replace(R.id.fragment_container, replaceFragment).commit()

            }

            findFragmentById is StudentPastAttendFragment -> {
                /*open AttendanceDatesFragment with transition*/
                openAttendanceDatesFragment()
            }

            findFragmentById !is AttendanceSheetFragment -> {
                fragmentTransaction(AttendanceSheetFragment())
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    private fun openFragmentWithTransition(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .addSharedElement(imgStudentListBackground, "background")
            .addSharedElement(imgAddStudents, "imgAddIcon")
            .addSharedElement(imgSaveAttendance, "imgSaveAndHelp")
            .addSharedElement(imgArrowBack, "imgArrowBack")
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun openAttendanceDatesFragment() {
        val replaceFragment =
            SharedTransition(this).sharedEnterAndExitTrans(AttendDatesFragment())
        val bundle = Bundle()
        bundle.putString(
            "profName",
            StudentPastAttendFragment().arguments?.getString("profName")
        )
        bundle.putString("profName", StudentPastAttendFragment.profName)
        bundle.putInt("hisId", StudentPastAttendFragment.hisId)
        bundle.putString("subName", StudentPastAttendFragment.subName)
        replaceFragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .addSharedElement(imgStdPastAttendBackground, "hist_background")
            .addSharedElement(imgArrowBack, "imgBack")
            .replace(R.id.fragment_container, replaceFragment)
            .commit()
    }
}