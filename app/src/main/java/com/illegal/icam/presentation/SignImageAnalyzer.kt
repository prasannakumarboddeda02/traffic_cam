package com.illegal.icam.presentation

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

        if(frameSkipCounter % 30 ==0) {
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