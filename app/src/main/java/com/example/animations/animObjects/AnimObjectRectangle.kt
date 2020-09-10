package com.example.animations.animObjects

import com.example.animations.anims.Anims


data class AnimObjectRectangle(
    var centerX: Int,
    var centerY: Int,
    var width: Int,
    var height: Int,
    var angle: Float,
    var color: Int,//{black, red, white, yellow}.
    val Anims:ArrayList<Anims>
)