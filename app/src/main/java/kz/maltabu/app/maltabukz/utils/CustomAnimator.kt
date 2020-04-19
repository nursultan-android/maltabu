package kz.maltabu.app.maltabukz.utils

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.BounceInterpolator
import android.view.animation.LinearInterpolator


class CustomAnimator {
    companion object {
        fun animateDown(icon: View?) {
            val objectAnimator = ObjectAnimator.ofFloat(icon, "rotation", 180f)
            objectAnimator.duration = 300
            objectAnimator.start()
        }

        fun animateUp(icon: View?) {
            val objectAnimator = ObjectAnimator.ofFloat(icon, "rotation", 0f)
            objectAnimator.duration = 300
            objectAnimator.start()
        }

        fun animateViewBound(refresh: View?) {
            val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.8f, 1f)
            val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.8f, 1f)
            val objectAnimator = ObjectAnimator.ofPropertyValuesHolder(refresh, scaleX, scaleY)
            objectAnimator.interpolator = BounceInterpolator()
            objectAnimator.repeatMode = ValueAnimator.REVERSE
            objectAnimator.start()
        }

        fun animateTab(refresh: View?) {
            val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.9f, 1f)
            val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.9f, 1f)
            val objectAnimator = ObjectAnimator.ofPropertyValuesHolder(refresh, scaleX, scaleY)
            objectAnimator.interpolator = BounceInterpolator()
            objectAnimator.startDelay=200
            objectAnimator.start()
        }

        fun animateHotViewLinear(refresh: View?) {
            val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.95f, 1f)
            val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.95f, 1f)
            val objectAnimator = ObjectAnimator.ofPropertyValuesHolder(refresh, scaleX, scaleY)
            objectAnimator.interpolator = LinearInterpolator()
            objectAnimator.repeatMode = ValueAnimator.REVERSE
            objectAnimator.start()
        }
    }
}