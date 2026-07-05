package ca.pashko.simpleplantid

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import android.util.Log




class MainActivity : AppCompatActivity() {
	
	var flowerSelected = false
	var leavesSelected = false
	val flowerNotSetText = "Photo of Flower"
	val flowerSetText = "Flower ✅"
	val leavesNotSetText = "Photo of Leaves"
	val leavesSetText = "Leaves ✅"
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContentView(R.layout.activity_main)
		ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
			val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
			v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
			insets
		}
		val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
			if (uri != null) {
				Log.d("PhotoPicker", "Selected URI: $uri")
				// TODO: Use the URI to display the image (e.g., using Glide or Coil)
			} else {
				Log.d("PhotoPicker", "No media selected")
			}
		}
		val flowerButton: Button = findViewById(R.id.flowerButton)
		val leavesButton: Button = findViewById(R.id.leavesButton)
		val submitButton: Button = findViewById(R.id.submitButton)
		val infoButton: ImageButton = findViewById(R.id.infoButton)
		flowerButton.setOnClickListener { flowerClick() }
		leavesButton.setOnClickListener { leavesClick() }
		submitButton.setOnClickListener { submitClick() }
		infoButton.setOnClickListener { infoClick() }
	}
	
	fun flowerClick(){
	
	}
	fun leavesClick(){
	
	}
	fun submitClick(){
	
	}
	fun infoClick(){
	
	}
	
}