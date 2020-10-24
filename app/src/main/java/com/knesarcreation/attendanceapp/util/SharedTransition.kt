package com.knesarcreation.attendanceapp.util

import android.content.Context
import android.os.Build
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import com.knesarcreation.attendanceapp.R

class SharedTransition(val context: Context) {

    fun sharedEnterAndExitTrans(fragment: Fragment): Fragment {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            /*setting a return transition and exit transition*/
            fragment.sharedElementReturnTransition = TransitionInflater.from(context)
                .inflateTransition(R.transition.change_background_trans)
            fragment.exitTransition = TransitionInflater.from(context)
                .inflateTransition(android.R.transition.fade)

            /* setting a entering transition*/
            fragment.sharedElementEnterTransition =
                TransitionInflater.from(context)
                    .inflateTransition(R.transition.change_background_trans)
            fragment.enterTransition = TransitionInflater.from(context)
                .inflateTransition(android.R.transition.fade)
        }
        return fragment
    }
}