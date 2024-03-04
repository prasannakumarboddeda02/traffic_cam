package com.illegal.icam

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.illegal.icam.data.TfLiteSignClassifier
import com.illegal.icam.domain.Classification
import com.illegal.icam.presentation.CameraPreview
import com.illegal.icam.presentation.SignImageAnalyzer
import com.illegal.icam.ui.theme.IcamTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(!hasCameraPermission()){
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA),0
            )
        }
        setContent {
            IcamTheme {
                // A surface container using the 'background' color from the theme
                var classifications by remember{
                    mutableStateOf(emptyList<Classification>())
                }
                val analyzer = remember{
                    SignImageAnalyzer(
                        classifier = TfLiteSignClassifier(
                            context = applicationContext
                        ),
                        onResults = {
                            classifications = it
                        }
                    )
                }
                val controller = remember {
                   LifecycleCameraController(applicationContext).apply {
                       setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
                       setImageAnalysisAnalyzer(
                           ContextCompat.getMainExecutor(applicationContext),
                           analyzer
                       )
                   }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    CameraPreview(controller = controller, modifier = Modifier.fillMaxSize())

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                    ) {
                        classifications.forEach{
                            Log.d("image:",it.name)
                            Text(
                                text = it.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(8.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp,
                                color = Color.White
                                )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .width(321.dp)
                            .height(321.dp)
                            .border(width = 2.dp, color = Color.Green, shape = RectangleShape)
                            .align(Alignment.Center)
                    )
                }
            }
        }
    }

    private fun hasCameraPermission() = ContextCompat.checkSelfPermission(
        this,Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}
