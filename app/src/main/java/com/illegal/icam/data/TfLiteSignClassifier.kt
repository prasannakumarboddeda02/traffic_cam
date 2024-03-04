package com.illegal.icam.data

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.Surface
import com.illegal.icam.domain.Classification
import com.illegal.icam.domain.SignClassifier
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions
import org.tensorflow.lite.task.vision.classifier.ImageClassifier

class TfLiteSignClassifier(
    private val context: Context,
    private val threshold: Float = 0.8f,
    private val maxResults: Int = 3
): SignClassifier {

    private var classifier: ImageClassifier? = null

    private fun setClassifier(){
        val baseOptions = BaseOptions.builder()
            .setNumThreads(3)
            .build()
        val options = ImageClassifier.ImageClassifierOptions.builder()
            .setBaseOptions(baseOptions)
            .setMaxResults(maxResults)
            .setScoreThreshold(threshold)
            .build()
        try{
            classifier = ImageClassifier.createFromFileAndOptions(
                context,
                "Traffic.tflite",
                options
            )
        }
        catch(e: IllegalStateException){
            e.printStackTrace()
        }
    }
    override fun classify(bitmap: Bitmap, rotation: Int): List<Classification> {
        if(classifier == null){
            setClassifier()
        }

        val imageProcessor = ImageProcessor.Builder().build()
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))

        val imageProcessingOptions = ImageProcessingOptions.builder()
            .setOrientation(getOrientationFromRotation(rotation))
            .build()

        val results = classifier?.classify(tensorImage, imageProcessingOptions)

        Log.d("result:",results.toString())

        return results?.flatMap { classifications ->
            classifications.categories.map { category ->
                Classification(
                    name = category.label,
                    score = category.score
                )
            }
        }?.distinctBy { it.name } ?: emptyList()
    }

    private fun getOrientationFromRotation(rotation: Int): ImageProcessingOptions.Orientation{
        return when(rotation){
            Surface.ROTATION_270 -> ImageProcessingOptions.Orientation.BOTTOM_RIGHT
            Surface.ROTATION_90 -> ImageProcessingOptions.Orientation.TOP_LEFT
            Surface.ROTATION_180 -> ImageProcessingOptions.Orientation.RIGHT_BOTTOM
            else -> ImageProcessingOptions.Orientation.RIGHT_TOP
        }
    }
}