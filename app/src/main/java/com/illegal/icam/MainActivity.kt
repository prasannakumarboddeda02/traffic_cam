package com.illegal.icam

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
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
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
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

    private lateinit var mediaPlayer :MediaPlayer
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
                Log.d("classifications:",classifications.toString())
                val analyzer = remember{
                    SignImageAnalyzer(
                        classifier = TfLiteSignClassifier(
                            context = applicationContext
                        ),
                        onResults = {
                            classifications = it
                            playSound(classifications = classifications)
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
                            Text(
                                text = if(it.name=="Roundabout mandatory") "stop" else it.name,
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

    private fun playSound(classifications: List<Classification>){
        if(classifications.isEmpty()) return
        var s = classifications[0].name
        s=s.substring(0,s.length-1)
        val resource: Int = when(s){
            "Speed limit (20km/h)" -> R.raw.speed_20
            "Speed limit (30km/h)" -> R.raw.speed_30
            "Speed limit (50km/h)" -> R.raw.speed_50km
            "Speed limit (60km/h)" ->R.raw.speed_60km
            "Speed limit (70km/h)" -> R.raw.speed_70km
            "Speed limit (80km/h)" -> R.raw.speed_80km
            "End of speed limit (80km/h)" -> R.raw.end_of_speedlimit_80km_per_hour
            "Speed limit (100km/h)" -> R.raw.speed_limit_100km_per_hour
            "Speed limit (120km/h)" -> R.raw.speed_limit_120km_per_hour
            "No passing" -> R.raw.no_passing
            "No passing veh over 3.5 tons" -> R.raw.no_passing_for_vehicles_over_35_tons
            "Right-of-way at intersection" -> R.raw.right_of_way_at_intersection
            "Priority road" -> R.raw.priority_road
            "Yield" -> R.raw.yield
            "Stop" -> R.raw.stop
            "No vehicles" -> R.raw.no_vehicles
            "Vehicle > 3.5 tons prohibited" -> R.raw.vehicles_greater_than_35_tons_prohibited
            "No entry" -> R.raw.no_entry
            "General caution" -> R.raw.general_caution
            "Dangerous curve left" -> R.raw.dangerous_curve_left
            "Dangerous curve right" -> R.raw.dangerous_curve_right
            "Double curve" -> R.raw.double_curve
            "Bumpy road" -> R.raw.bumpy_road
            "Slippery road" -> R.raw.slippery_road
            "Road narrows on the right" -> R.raw.road_narrow_o_the_right
            "Road work" -> R.raw.road_work
            "Traffic signals" -> R.raw.traffic_signals
            "Pedestrians" -> R.raw.pedestrians
            "Children crossing" -> R.raw.children_crossing
            "Bicycles crossing" -> R.raw.bicycles_crossing
            "Beware of ice/snow" -> R.raw.beware_of_ice_or_snow
            "Wild animals crossing" -> R.raw.wild_animals_crossing
            "End speed + passing limits" -> R.raw.end_speed_and_passing_limits
            "Turn right ahead" -> R.raw.turn_right_ahead
            "Turn left ahead" -> R.raw.turn_left_ahead
            "Ahead only" -> R.raw.ahead_only
            "Go straight or right" -> R.raw.go_straight_or_right
            "Go straight or left" -> R.raw.go_straight_or_left
            "Keep right" -> R.raw.keep_right
            "Keep left" -> R.raw.keep_left
            "Roundabout mandatory" -> R.raw.stop
            "End of no passing" -> R.raw.end_of_no_passing
            "End no passing vehicle > 3.5 tons" -> R.raw.end_no_passing_vehicles_greater_than_35_tons
            else -> return
        }
        mediaPlayer = MediaPlayer.create(this,resource)
        mediaPlayer.start()
    }
}
