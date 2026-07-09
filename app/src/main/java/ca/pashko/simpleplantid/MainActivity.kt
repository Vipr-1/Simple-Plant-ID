package ca.pashko.simpleplantid
import android.net.Uri
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
import androidx.activity.result.ActivityResultLauncher
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class MainActivity : AppCompatActivity() {
	
	var flowerSelected = false
	var leavesSelected = false
	val flowerNotSetText = "Photo of Flower"
	val flowerSetText = "Flower ✅"
	val leavesNotSetText = "Photo of Leaves"
	val leavesSetText = "Leaves ✅"
	
	val noPhotoDialog = MaterialAlertDialogBuilder(this)
	
	
	
	var flowerUri: Uri? = null
	var leavesUri: Uri? = null
	
	val pickFlower = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
		if (uri != null) {
			flowerSelected = true
			flowerUri = uri
			val flowerButton: Button = findViewById(R.id.flowerButton)
			flowerButton.text = flowerSetText
		}
	}
	val pickLeaves = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
		if (uri != null) {
			leavesSelected = true
			leavesUri = uri
			val leavesButton: Button = findViewById(R.id.leavesButton)
			leavesButton.text = leavesSetText
		}
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContentView(R.layout.activity_main)
		ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
			val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
			v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
			insets
		}
		
		
		val flowerButton: Button = findViewById(R.id.flowerButton)
		val leavesButton: Button = findViewById(R.id.leavesButton)
		val submitButton: Button = findViewById(R.id.submitButton)
		val infoButton: ImageButton = findViewById(R.id.infoButton)
		flowerButton.setOnClickListener { flowerClick() }
		leavesButton.setOnClickListener { leavesClick() }
		submitButton.setOnClickListener { submitClick() }
		infoButton.setOnClickListener { infoClick() }
		noPhotoDialog.setTitle("Incorrect Submission")
		noPhotoDialog.setMessage("Please select both a flower and leaves photo.")
		noPhotoDialog.setPositiveButton("OK") { dialog, _ ->
			dialog.dismiss()
		}
	}
	
	fun flowerClick(){
		pickFlower.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
	}
	fun leavesClick(){
		pickLeaves.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
		
	}
	fun submitClick() {
		if (flowerUri != null && leavesUri != null) {
			//TODO: Submit to API
		} else {
			noPhotoDialog.show()
		}
	}
	fun infoClick(){
	
	}
	
}