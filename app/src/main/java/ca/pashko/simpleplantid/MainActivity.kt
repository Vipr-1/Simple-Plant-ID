package ca.pashko.simpleplantid

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
	
	private val apiKey = BuildConfig.plantNetAPI
	private var flowerSelected = false
	private var leavesSelected = false
	private val flowerSetText = "Flower ✅"
	private val leavesSetText = "Leaves ✅"
	
	private var flowerUri: Uri? = null
	private var leavesUri: Uri? = null
	
	private val pickFlower = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
		if (uri != null) {
			flowerSelected = true
			flowerUri = uri
			findViewById<Button>(R.id.flowerButton).text = flowerSetText
		}
	}
	
	private val pickLeaves = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
		if (uri != null) {
			leavesSelected = true
			leavesUri = uri
			findViewById<Button>(R.id.leavesButton).text = leavesSetText
		}
	}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContentView(R.layout.activity_main)
		ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
			val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
			// Add system bar insets to the existing 24dp padding we set in XML
			v.setPadding(
				systemBars.left,
				systemBars.top,
				systemBars.right,
				systemBars.bottom
			)
			insets
		}
		
		findViewById<Button>(R.id.flowerButton).setOnClickListener { flowerClick() }
		findViewById<Button>(R.id.leavesButton).setOnClickListener { leavesClick() }
		findViewById<Button>(R.id.submitButton).setOnClickListener { submitClick() }
		findViewById<ImageButton>(R.id.infoButton).setOnClickListener { infoClick() }
	}
	
	private fun flowerClick() {
		pickFlower.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
	}
	
	private fun leavesClick() {
		pickLeaves.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
	}
	
	private fun submitClick() {
		val currentFlowerUri = flowerUri
		val currentLeavesUri = leavesUri
		
		if (currentFlowerUri != null && currentLeavesUri != null) {
			val flowerFile = getFileFromUri(this, currentFlowerUri)
			val leavesFile = getFileFromUri(this, currentLeavesUri)
			
			if (flowerFile != null && leavesFile != null) {
				lifecycleScope.launch {
					try {
						val output: String = withContext(Dispatchers.IO) {
							val identAPI = IdentAPI(apiKey, flowerFile, leavesFile)
							identAPI.makeRequest()
						}
						
						if (output.startsWith("Request failed") || output == "error") {
							showDialog("Error", output)
						} else {
							val (sciName, comName) = parsePlantNames(output)
							showDialog("Results", "Scientific name: $sciName \n Common Names: $comName")
						}
					} catch (e: Exception) {
						Log.e("MainActivity", "Error identifying plant", e)
						showDialog("Error", "Failed to identify plant. Please check your connection or image quality.")
					}
				}
				resetPhotos()
			}
		} else {
			showDialog("Incorrect Submission", "Please select both a flower and leaves photo.")
		}
	}
	
	// Helper to create and show dialogs safely using the current context
	private fun showDialog(title: String, message: String) {
		MaterialAlertDialogBuilder(this)
			.setTitle(title)
			.setMessage(message)
			.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
			.show()
	}
	
	private fun getFileFromUri(context: Context, uri: Uri): File? {
		val tempFile = File(context.cacheDir, "temp_media_file_${System.currentTimeMillis()}")
		return try {
			context.contentResolver.openInputStream(uri)?.use { input ->
				FileOutputStream(tempFile).use { output ->
					input.copyTo(output)
				}
			}
			tempFile
		} catch (e: Exception) {
			null
		}
	}
	
	private fun parsePlantNames(apiResponse: String): Pair<String, String> {
		val jsonData = JSONObject(apiResponse)
		val resultsArray = jsonData.getJSONArray("results")
		if (resultsArray.length() == 0) return Pair("Unknown", "None")
		
		val mostLikelyPlant = resultsArray.getJSONObject(0)
		val speciesInfo = mostLikelyPlant.getJSONObject("species")
		val scientificName = speciesInfo.getString("scientificNameWithoutAuthor")
		val commonNamesArray = speciesInfo.optJSONArray("commonNames")
		val commonNamesList = mutableListOf<String>()
		
		if (commonNamesArray != null) {
			for (i in 0 until commonNamesArray.length()) {
				commonNamesList.add(commonNamesArray.getString(i))
			}
		}
		
		val commonNames = if (commonNamesList.isEmpty()) "None" else commonNamesList.joinToString(", ")
		return Pair(scientificName, commonNames)
	}
	fun resetPhotos() {
		flowerUri = null
		leavesUri = null
		flowerSelected = false
		leavesSelected = false
		findViewById<Button>(R.id.flowerButton).text = "Photo of Flower"
		findViewById<Button>(R.id.leavesButton).text = "Photo of Leaves"
	}
	private fun infoClick() {
		showDialog("Simple Plant ID", "© 2026 Damon Pashko \n GNU GPLv3 \n https://github.com/Vipr-1/Simple-Plant-ID \n Pl@ntNet API key: $apiKey")
	}
}