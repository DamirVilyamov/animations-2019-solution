package com.example.animations

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.View.*
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.animations.animObjects.AnimObjectCircle
import com.example.animations.animObjects.AnimObjectRectangle
import com.example.animations.anims.Anims
import com.example.animations.anims.MoveAnimation
import com.example.animations.anims.RotateAnimation
import com.example.animations.anims.ScaleAnimation
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val RectangleObjectsList = ArrayList<AnimObjectRectangle>()
    private val CircleObjectsList = ArrayList<AnimObjectCircle>()
    private val objectAnimators = ArrayList<ObjectAnimator>()
    private val animatorSets = ArrayList<AnimatorSet>()
    private val raws = ArrayList<Int>()
    private val TAG = "!@#"
    private var isPaused = false
    var currentAnimNum = 0
    var canvasWidth = 0
    var canvasHeight = 0
    var numOfObjectsToAnimate = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initRaws()
        getAnimInfo(raws[currentAnimNum])
        createAnims()
        button_pause.setOnClickListener { pause() }
        button_previous.setOnClickListener { changeAnimView(-1) }
        button_start.setOnClickListener { startAnims() }
        button_next.setOnClickListener { changeAnimView(1) }
    }

    private fun pause() {
        if (animatorSets.any { it.isStarted }) {
            if (isPaused) {
                animatorSets.forEach { it.resume() }
                isPaused = false
                button_pause.text = "Pause"
            } else {
                animatorSets.forEach { it.pause() }
                isPaused = true
                button_pause.text = "Resume"
            }
        }
    }

    private fun initRaws() {
        with(raws) {
            add(R.raw.input_0)
            add(R.raw.input_1)
            add(R.raw.input_2)
            add(R.raw.input_3)
            add(R.raw.input_4)
            add(R.raw.input_5)
            add(R.raw.input_6)
            add(R.raw.input_7)
            add(R.raw.input_8)
            add(R.raw.input_9)
            add(R.raw.input_10)
        }
    }

    private fun startAnims() {
        animatorSets.forEach { it.start() }
    }

    private fun changeAnimView(shift: Int) {
        if (currentAnimNum + shift < 0 || currentAnimNum + shift > 9) {
            Toast.makeText(this, "Сорри дальше не получится \n\t¯_(ツ)_/¯", Toast.LENGTH_SHORT)
                .show()
        } else {
            currentAnimNum += shift
            canvasView.removeAllViewsInLayout()
            canvasView.refreshDrawableState()
            getAnimInfo(raws[currentAnimNum])
            createAnims()
        }
    }

    private fun parseInput(input: String): List<String> {
        return input.split(" ", "\n")
    }

    private fun getColor(color: String): Int {
        when (color) {
            "black" -> {
                return Color.BLACK
            }
            "red" -> {
                return Color.RED
            }
            "white" -> {
                return Color.WHITE
            }
            "yellow" -> {
                return Color.YELLOW
            }
        }
        return -1
    }

    private fun getAnimInfo(resId: Int) {
        RectangleObjectsList.clear()
        CircleObjectsList.clear()
        animatorSets.clear()

        val inputList = parseInput(TextFileReader.readRawTextFile(this, resId))
        Log.d(TAG, "initAnim: $inputList")

        canvasWidth = inputList[0].toInt()
        canvasHeight = inputList[1].toInt()
        numOfObjectsToAnimate = inputList[2].toInt()

        var skip = 0
        for (i in 3 until inputList.size) {

            if (skip != 0) {
                skip--
                continue
            }
            Log.d(TAG, "initAnim: position after skip: ${inputList[i]}")

            if (inputList[i] == "rectangle") {
                val centerX = inputList[i + 1].toFloat().toInt()
                val centerY = inputList[i + 2].toFloat().toInt()
                val width = inputList[i + 3].toFloat().toInt()
                val height = inputList[i + 4].toFloat().toInt()
                val angle = inputList[i + 5].toFloat()
                val color = getColor(inputList[i + 6])
                val moveAnimations = ArrayList<MoveAnimation>()
                val rotateAnimations = ArrayList<RotateAnimation>()
                val scaleAnimations = ArrayList<ScaleAnimation>()
                val animations = ArrayList<Anims>()
                val animationsNum = inputList[i + 7].toInt()
                var animCount = 0
                for (j in 0 until animationsNum) {
                    Log.d(TAG, "initAnim: anim count = $animCount")
                    when (inputList[i + 8 + animCount]) {
                        "move" -> {
                            val animDestX = inputList[i + 9 + animCount].toFloat()
                            val animDestY = inputList[i + 10 + animCount].toFloat()
                            val time = inputList[i + 11 + animCount].toLong()
                            var cycle = false
                            if (inputList[i + 12 + animCount] == "cycle") {
                                cycle = true
                            }
                            val moveAnim = MoveAnimation(animDestX, animDestY, time, cycle)
                            moveAnimations.add(moveAnim)

                            animations.add(moveAnim)

                            animCount += 4
                            Log.d(TAG, "initAnim: move anim added, count = $animCount")
                        }
                        "scale" -> {
                            val destScale = inputList[i + 9 + animCount].toFloat()
                            val time = inputList[i + 10 + animCount].toLong()
                            val cycle = inputList[i + 11 + animCount].toBoolean()
                            val scaleAnim = ScaleAnimation(destScale, time, cycle)
                            scaleAnimations.add(scaleAnim)

                            animations.add(scaleAnim)

                            animCount += 3
                            Log.d(TAG, "initAnim: scale anim added, count = $animCount")
                        }
                        "rotate" -> {
                            val animAngle = inputList[i + 9 + animCount].toFloat()
                            val time = inputList[i + 10 + animCount].toLong()
                            val cycle = inputList[i + 11 + animCount].toBoolean()
                            val rotateAnim = RotateAnimation(animAngle, time, cycle)
                            rotateAnimations.add(rotateAnim)

                            animations.add(rotateAnim)

                            animCount += 3
                            Log.d(TAG, "initAnim: rotate anim added, count = $animCount")
                        }
                    }
                }
                val rect = AnimObjectRectangle(
                    centerX,
                    centerY,
                    width,
                    height,
                    angle,
                    color,
                    moveAnimations,
                    rotateAnimations,
                    scaleAnimations,
                    animations
                )
                RectangleObjectsList.add(rect)
                skip = 7 + animCount
                Log.d(TAG, "initAnim: added rectangle:$rect")
            } else if (inputList[i] == "circle") {
                val centerX = inputList[i + 1].toFloat().toInt()
                val centerY = inputList[i + 2].toFloat().toInt()
                val radius = inputList[i + 3].toFloat()
                val color = getColor(inputList[i + 4])
                val moveAnimations = ArrayList<MoveAnimation>()
                val rotateAnimations = ArrayList<RotateAnimation>()
                val scaleAnimations = ArrayList<ScaleAnimation>()
                val animations = ArrayList<Anims>()
                val animationsNum = inputList[i + 5].toInt()
                var animCount = 0
                for (j in 0 until animationsNum) {
                    Log.d(TAG, "initAnim: anim count = $animCount")
                    when (inputList[i + 6 + animCount]) {
                        "move" -> {
                            val animDestX = inputList[i + 7 + animCount].toFloat()
                            val animDestY = inputList[i + 8 + animCount].toFloat()
                            val time = inputList[i + 9 + animCount].toLong()
                            val cycle = inputList[i + 10 + animCount].toBoolean()
                            val moveanim = MoveAnimation(animDestX, animDestY, time, cycle)
                            moveAnimations.add(moveanim)

                            animations.add(moveanim)

                            animCount += 4
                            Log.d(TAG, "initAnim: move anim added, count = $animCount")
                        }
                        "scale" -> {
                            val destScale = inputList[i + 7 + animCount].toFloat()
                            val time = inputList[i + 8 + animCount].toLong()
                            val cycle = inputList[i + 9 + animCount].toBoolean()
                            val scaleAnim = ScaleAnimation(destScale, time, cycle)
                            scaleAnimations.add(scaleAnim)

                            animations.add(scaleAnim)

                            animCount += 3
                            Log.d(TAG, "initAnim: scale anim added, count = $animCount")
                        }
                        "rotate" -> {
                            val animAngle = inputList[i + 7 + animCount].toFloat()
                            val time = inputList[i + 8 + animCount].toLong()
                            val cycle = inputList[i + 9 + animCount].toBoolean()
                            val rotateAnim = RotateAnimation(animAngle, time, cycle)
                            rotateAnimations.add(rotateAnim)

                            animations.add(rotateAnim)

                            animCount += 3
                            Log.d(TAG, "initAnim: rotate anim added, count = $animCount")
                        }
                    }
                }
                val circ = AnimObjectCircle(
                    centerX,
                    centerY,
                    radius,
                    color,
                    moveAnimations,
                    rotateAnimations,
                    scaleAnimations,
                    animations
                )
                CircleObjectsList.add(circ)
                skip = 5 + animCount
                Log.d(TAG, "initAnim: added circle $circ, skip = $skip")
            }

        }
        Log.d(TAG, "initAnim: rectangles added: ${RectangleObjectsList.size}")
        Log.d(TAG, "initAnim: circles added: ${CircleObjectsList.size}")
        Log.d(
            TAG,
            "initAnim: total objects added: ${CircleObjectsList.size + RectangleObjectsList.size}"
        )
    }

    private fun createAnims() {
        canvasView.minimumHeight = canvasHeight
        canvasView.minimumWidth = canvasWidth
        canvasView.setBackgroundColor(Color.GRAY)

        for (rectObject in RectangleObjectsList) {
            val imageView = ImageView(this)

            imageView.rotation = rectObject.angle
            val params = RelativeLayout.LayoutParams(rectObject.width, rectObject.height)
            params.leftMargin =
                rectObject.centerX - rectObject.width / 2//to place the center but not the left border
            params.topMargin =
                rectObject.centerY - rectObject.height / 2//to place the center but not the top border

            val drawable = GradientDrawable()
            drawable.shape = GradientDrawable.RECTANGLE
            drawable.setColor(rectObject.color)
            imageView.setImageDrawable(drawable)

            var firstAnimObjectAnimator: ObjectAnimator? = null
            var secondAnimObjectAnimator: ObjectAnimator? = null
            var thirdAnimObjectAnimator: ObjectAnimator? = null

            val animatorSet = AnimatorSet()
            var animCount = 0
            for (anim in rectObject.Anims) {
                when (anim) {
                    is MoveAnimation -> {
                        val pvhX = PropertyValuesHolder.ofFloat(X, anim.destX)
                        val pvhY = PropertyValuesHolder.ofFloat(Y, anim.destY)
                        val objectAnimator =
                            ObjectAnimator.ofPropertyValuesHolder(imageView, pvhX, pvhY)
                                .setDuration(anim.time)

                        if (anim.cycle) {
                            objectAnimator.repeatCount = ObjectAnimator.INFINITE
                            objectAnimator.repeatMode = ObjectAnimator.REVERSE
                        }

                        objectAnimator.setAutoCancel(false)
                        objectAnimators.add(objectAnimator)

                        when (animCount) {
                            0 -> {
                                firstAnimObjectAnimator = objectAnimator
                                animCount++
                            }
                            1 -> {
                                secondAnimObjectAnimator = objectAnimator
                                animCount++
                            }
                            2 -> {
                                thirdAnimObjectAnimator = objectAnimator
                                animCount++
                            }
                        }
                    }

                    is RotateAnimation -> {
                        val objectAnimatorRotate =
                            ObjectAnimator.ofFloat(imageView, "rotation", anim.angle)
                                .setDuration(anim.time)
                        if (anim.cycle) {
                            objectAnimatorRotate.repeatCount = ObjectAnimator.INFINITE
                            objectAnimatorRotate.repeatMode = ObjectAnimator.REVERSE
                        }
                        objectAnimators.add(objectAnimatorRotate)

                        when (animCount) {
                            0 -> {
                                firstAnimObjectAnimator = objectAnimatorRotate
                                animCount++
                            }
                            1 -> {
                                secondAnimObjectAnimator = objectAnimatorRotate
                                animCount++
                            }
                            2 -> {
                                thirdAnimObjectAnimator = objectAnimatorRotate
                                animCount++
                            }
                        }
                    }

                    is ScaleAnimation -> {
                        val pvhX = PropertyValuesHolder.ofFloat(SCALE_X, anim.destScale)
                        val pvhY = PropertyValuesHolder.ofFloat(SCALE_Y, anim.destScale)

                        val objectAnimatorScale =
                            ObjectAnimator.ofPropertyValuesHolder(imageView, pvhX, pvhY)
                                .setDuration(anim.time)


                        if (anim.cycle) {
                            objectAnimatorScale.repeatCount = ObjectAnimator.INFINITE
                            objectAnimatorScale.repeatMode = ObjectAnimator.REVERSE
                        }

                        objectAnimatorScale.setAutoCancel(false)

                        objectAnimators.add(objectAnimatorScale)


                        when (animCount) {
                            0 -> {
                                firstAnimObjectAnimator = objectAnimatorScale
                                animCount++
                            }
                            1 -> {
                                secondAnimObjectAnimator = objectAnimatorScale
                                animCount++
                            }
                            2 -> {
                                thirdAnimObjectAnimator = objectAnimatorScale
                                animCount++
                            }
                        }

                    }

                }

            }

            if (firstAnimObjectAnimator != null) {
                Log.d(TAG, "createAnims: firstobjectanimator is not null")
                if (secondAnimObjectAnimator != null) {
                    Log.d(TAG, "createAnims: secondobjectanimator is not null")
                    if (thirdAnimObjectAnimator != null) {
                        animatorSet.playSequentially(
                            firstAnimObjectAnimator,
                            secondAnimObjectAnimator,
                            thirdAnimObjectAnimator
                        )
                    } else {
                        animatorSet.playSequentially(
                            firstAnimObjectAnimator,
                            secondAnimObjectAnimator
                        )
                    }
                }
                animatorSet.play(firstAnimObjectAnimator)
            }

            animatorSets.add(animatorSet)
            /* for (anim in rectObject.moveAnims) {

                 val objectAnimatorX =
                     ObjectAnimator.ofFloat(imageView, "x", (anim.destX - rectObject.centerX))
                         .setDuration(anim.time)

                 val objectAnimatorY =
                     ObjectAnimator.ofFloat(imageView, "y", (anim.destY - rectObject.centerY))
                         .setDuration(anim.time)

                 if (anim.cycle) {
                     objectAnimatorX.repeatCount = ObjectAnimator.INFINITE
                     objectAnimatorX.repeatMode = ObjectAnimator.REVERSE
                     objectAnimatorY.repeatCount = ObjectAnimator.INFINITE
                     objectAnimatorY.repeatMode = ObjectAnimator.REVERSE
                 }

                 objectAnimatorX.setAutoCancel(false)
                 objectAnimatorY.setAutoCancel(false)
                 objectAnimators.add(objectAnimatorX)
                 objectAnimators.add(objectAnimatorY)


             }

             for (rotateAnim in rectObject.rotateAnims) {
                 val objectAnimatorRotate =
                     ObjectAnimator.ofFloat(imageView, "rotation", rotateAnim.angle)
                         .setDuration(rotateAnim.time)
                 if (rotateAnim.cycle) {
                     objectAnimatorRotate.repeatCount = ObjectAnimator.INFINITE
                     objectAnimatorRotate.repeatMode = ObjectAnimator.REVERSE
                 }
                 objectAnimators.add(objectAnimatorRotate)
             }

             for (scaleAnim in rectObject.scaleAnims) {
                 val objectAnimatorScaleX =
                     ObjectAnimator.ofFloat(imageView, "scaleX", scaleAnim.destScale)
                         .setDuration(scaleAnim.time)
                 val objectAnimatorScaleY =
                     ObjectAnimator.ofFloat(imageView, "scaleY", scaleAnim.destScale)
                         .setDuration(scaleAnim.time)

                 if (scaleAnim.cycle) {
                     objectAnimatorScaleX.repeatCount = ObjectAnimator.INFINITE
                     objectAnimatorScaleX.repeatMode = ObjectAnimator.REVERSE
                 }
                 objectAnimatorScaleX.setAutoCancel(false)
                 objectAnimatorScaleY.setAutoCancel(false)

                 objectAnimators.add(objectAnimatorScaleX)
                 objectAnimators.add(objectAnimatorScaleY)
             }*/

            canvasView.addView(imageView, params)
        }

        for (circleObject in CircleObjectsList) {
            val imageView = ImageView(this)

            val params = RelativeLayout.LayoutParams(
                circleObject.radius.toInt(),
                circleObject.radius.toInt()
            )
            params.leftMargin =
                circleObject.centerX - circleObject.radius.toInt()//to place the center but not the left border
            params.topMargin =
                circleObject.centerY - circleObject.radius.toInt()//to place the center but not the top border

            val drawable = GradientDrawable()
            drawable.shape = GradientDrawable.RING
            drawable.setColor(circleObject.color)

            imageView.setImageDrawable(drawable)
            var firstAnimObjectAnimator: ObjectAnimator? = null
            var secondAnimObjectAnimator: ObjectAnimator? = null
            var thirdAnimObjectAnimator: ObjectAnimator? = null

            val animatorSet = AnimatorSet()

            for (anim in circleObject.Anims) {
                when (anim) {
                    is MoveAnimation -> {
                        val pvhX = PropertyValuesHolder.ofFloat(X, anim.destX)
                        val pvhY = PropertyValuesHolder.ofFloat(Y, anim.destY)
                        val objectAnimator = ObjectAnimator.ofPropertyValuesHolder(
                            imageView,
                            pvhX,
                            pvhY
                        ).setDuration(anim.time)

                        if (anim.cycle) {
                            objectAnimator.repeatCount = ObjectAnimator.INFINITE
                            objectAnimator.repeatMode = ObjectAnimator.REVERSE
                        }

                        objectAnimator.setAutoCancel(false)
                        objectAnimators.add(objectAnimator)

                        when (circleObject.Anims.indexOf(anim)) {
                            0 -> {
                                firstAnimObjectAnimator = objectAnimator
                            }
                            1 -> {
                                secondAnimObjectAnimator = objectAnimator
                            }
                            2 -> {
                                thirdAnimObjectAnimator = objectAnimator
                            }
                        }
                    }

                    is RotateAnimation -> {
                        val objectAnimatorRotate =
                            ObjectAnimator.ofFloat(imageView, "rotation", anim.angle)
                                .setDuration(anim.time)

                        if (anim.cycle) {
                            objectAnimatorRotate.repeatCount = ObjectAnimator.INFINITE
                            objectAnimatorRotate.repeatMode = ObjectAnimator.REVERSE
                        }

                        objectAnimators.add(objectAnimatorRotate)


                        when (circleObject.Anims.indexOf(anim)) {
                            0 -> {
                                firstAnimObjectAnimator = objectAnimatorRotate
                            }
                            1 -> {
                                secondAnimObjectAnimator = objectAnimatorRotate
                            }
                            2 -> {
                                thirdAnimObjectAnimator = objectAnimatorRotate
                            }
                        }
                    }

                    is ScaleAnimation -> {
                        val pvhX = PropertyValuesHolder.ofFloat(SCALE_X, anim.destScale)
                        val pvhY = PropertyValuesHolder.ofFloat(SCALE_Y, anim.destScale)

                        val objectAnimatorScale =
                            ObjectAnimator.ofPropertyValuesHolder(imageView, pvhX, pvhY)
                                .setDuration(anim.time)


                        if (anim.cycle) {
                            objectAnimatorScale.repeatCount = ObjectAnimator.INFINITE
                            objectAnimatorScale.repeatMode = ObjectAnimator.REVERSE
                        }

                        objectAnimatorScale.setAutoCancel(false)

                        objectAnimators.add(objectAnimatorScale)

                        when (circleObject.Anims.indexOf(anim)) {
                            0 -> {
                                firstAnimObjectAnimator = objectAnimatorScale
                            }
                            1 -> {
                                secondAnimObjectAnimator = objectAnimatorScale
                            }
                            2 -> {
                                thirdAnimObjectAnimator = objectAnimatorScale
                            }
                        }

                    }

                }

            }

            if (firstAnimObjectAnimator != null) {
                if (secondAnimObjectAnimator != null) {
                    if (thirdAnimObjectAnimator != null) {
                        animatorSet.playSequentially(
                            firstAnimObjectAnimator,
                            secondAnimObjectAnimator,
                            thirdAnimObjectAnimator
                        )
                    } else {
                        animatorSet.playSequentially(
                            firstAnimObjectAnimator,
                            secondAnimObjectAnimator
                        )
                    }
                }
            }
            animatorSets.add(animatorSet)

            /*for (moveAnim in circleObject.moveAnims) {
                val objectAnimatorX =
                    ObjectAnimator.ofFloat(imageView, "x", (moveAnim.destX - circleObject.centerX))
                        .setDuration(moveAnim.time)

                val objectAnimatorY =
                    ObjectAnimator.ofFloat(imageView, "y", (moveAnim.destY - circleObject.centerY))
                        .setDuration(moveAnim.time)

                if (moveAnim.cycle) {
                    objectAnimatorX.repeatCount = ObjectAnimator.INFINITE
                    objectAnimatorX.repeatMode = ObjectAnimator.REVERSE
                    objectAnimatorY.repeatCount = ObjectAnimator.INFINITE
                    objectAnimatorY.repeatMode = ObjectAnimator.REVERSE
                }

                objectAnimatorX.setAutoCancel(false)
                objectAnimatorY.setAutoCancel(false)
                objectAnimators.add(objectAnimatorX)
                objectAnimators.add(objectAnimatorY)

            }
            for (rotateAnim in circleObject.rotateAnims) {
                val objectAnimatorRotate =
                    ObjectAnimator.ofFloat(imageView, "rotation", rotateAnim.angle)
                        .setDuration(rotateAnim.time)
                if (rotateAnim.cycle) {
                    objectAnimatorRotate.repeatCount = ObjectAnimator.INFINITE
                    objectAnimatorRotate.repeatMode = ObjectAnimator.REVERSE
                }
                objectAnimators.add(objectAnimatorRotate)
            }
            for (scaleAnim in circleObject.scaleAnims) {
                val objectAnimatorScaleX =
                    ObjectAnimator.ofFloat(imageView, "scaleX", scaleAnim.destScale)
                        .setDuration(scaleAnim.time)
                val objectAnimatorScaleY =
                    ObjectAnimator.ofFloat(imageView, "scaleY", scaleAnim.destScale)
                        .setDuration(scaleAnim.time)

                if (scaleAnim.cycle) {
                    objectAnimatorScaleX.repeatCount = ObjectAnimator.INFINITE
                    objectAnimatorScaleX.repeatMode = ObjectAnimator.REVERSE
                    objectAnimatorScaleY.repeatCount = ObjectAnimator.INFINITE
                    objectAnimatorScaleY.repeatMode = ObjectAnimator.REVERSE
                }

                objectAnimatorScaleX.setAutoCancel(false)
                objectAnimatorScaleY.setAutoCancel(false)

                objectAnimators.add(objectAnimatorScaleX)
                objectAnimators.add(objectAnimatorScaleY)
            }*/

            canvasView.addView(imageView, params)
        }


    }


}