package com.illegal.icam.domain

import android.graphics.Bitmap

interface SignClassifier {

    fun classify(bitmap: Bitmap, rotation: Int) : List<Classification>
}