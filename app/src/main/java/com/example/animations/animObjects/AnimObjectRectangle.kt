package com.example.animations.animObjects

import android.graphics.Color
import com.example.animations.anims.Anims
import com.example.animations.anims.MoveAnimation
import com.example.animations.anims.RotateAnimation
import com.example.animations.anims.ScaleAnimation

data class AnimObjectRectangle(
    var centerX: Int,
    var centerY: Int,
    var width: Int,
    var height: Int,
    var angle: Float,
    var color: Int,//{black, red, white, yellow}.
    val moveAnims:ArrayList<MoveAnimation>,
    val rotateAnims:ArrayList<RotateAnimation>,
    val scaleAnims:ArrayList<ScaleAnimation>,
    val Anims:ArrayList<Anims>
)