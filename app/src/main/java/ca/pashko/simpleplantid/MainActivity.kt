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
import java.io.File
import java.io.FileOutputStream
import android.content.Context
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {
	
	val apiKey = BuildConfig.plantNetAPI
	var flowerSelected = false
	var leavesSelected = false
	val flowerNotSetText = "Photo of Flower"
	val flowerSetText = "Flower ✅"
	val leavesNotSetText = "Photo of Leaves"
	val leavesSetText = "Leaves ✅"
	
	val noPhotoDialog = MaterialAlertDialogBuilder(this)
	val infoDialog = MaterialAlertDialogBuilder(this)
	
	
	
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
		infoDialog.setTitle("Simple Plant ID")
		infoDialog.setMessage("© 2026 Damon Pashko \n GNU GPLv3 \n Pl@ntNet API key: $apiKey")
		infoDialog.setPositiveButton("OK") { dialog, _ ->
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
		val currentFlowerUri = flowerUri
		val currentLeavesUri = leavesUri
		
		if (currentFlowerUri != null && currentLeavesUri != null) {
			//TODO: Submit to API
			val flowerFile = getFileFromUri(this, currentFlowerUri)
			val leavesFile = getFileFromUri(this, currentLeavesUri)
			if (flowerFile != null && leavesFile != null) {
				lifecycleScope.launch {
					withContext(Dispatchers.IO) {
						val identAPI = IdentAPI(apiKey, flowerFile, leavesFile)
						val output = identAPI.makeRequest()
					}
				}
				
				
			}
			else{
				Log.e("Error", "File is null")
			}
		} else {
			noPhotoDialog.show()
		}
	}
	
	fun getFileFromUri(context: Context, uri: Uri): File? {
		// Create a temporary file in your app's cache directory
		val tempFile = File(context.cacheDir, "temp_media_file_${System.currentTimeMillis()}")
		
		return try {
			// Open an InputStream from the URI
			val inputStream = context.contentResolver.openInputStream(uri)
			
			// Open an OutputStream to the temp file
			val outputStream = FileOutputStream(tempFile)
			
			// Copy the data from the input stream to the output stream
			// The 'use' block ensures streams are automatically closed when done
			inputStream?.use { input ->
				outputStream.use { output ->
					input.copyTo(output)
				}
			}
			
			// Return the newly created File object
			tempFile
		} catch (e: Exception) {
			e.printStackTrace()
			null
		}
	}
	
	fun infoClick(){
		infoDialog.show()
	}
	
}