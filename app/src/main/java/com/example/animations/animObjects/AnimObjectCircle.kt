package com.example.animations.animObjects

import com.example.animations.anims.Anims

data class AnimObjectCircle(
    var centerX: Int,
    var centerY: Int,
    var radius: Float,
    var color: Int,//{black, red, white, yellow}.
    val Anims:ArrayList<Anims>
)