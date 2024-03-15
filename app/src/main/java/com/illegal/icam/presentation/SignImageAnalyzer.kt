package com.illegal.icam.presentation

import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.illegal.icam.domain.Classification
import com.illegal.icam.domain.SignClassifier

class SignImageAnalyzer(
    private val classifier: SignClassifier,
    private val onResults: (List<Classification>) -> Unit
): ImageAnalysis.Analyzer {

    private var frameSkipCounter = 0

    override fun analyze(image: ImageProxy) {

        if(frameSkipCounter % 40 ==0) {
            val rotationDegrees = image.imageInfo.rotationDegrees
            val bitmap = image
                .toBitmap()
                .centerCrop(321, 321)
            val results = classifier.classify(bitmap, rotationDegrees)
            onResults(results)
        }
        frameSkipCounter++

        image.close()
    }
}

fun resizePhoto(bitmap: Bitmap): Bitmap {


    val w = bitmap.width
    val h = bitmap.height
    val aspRat = w / h
    val W = 321
    val H = 321
    val b = Bitmap.createScaledBitmap(bitmap, W, H, false)

    return b


}