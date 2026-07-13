package ca.pashko.simpleplantid;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class IdentAPI {
	File flowerImage;
	File leavesImage;

	private String apiKey;
	private static final String PROJECT = "all";
	private static final String URL = "https://my-api.plantnet.org/v2/identify/" + PROJECT + "?api-key=YOUR-PRIVATE-API-KEY-HERE";



	public IdentAPI(String apiKey, File flowerImage, File leavesImage) {
		this.apiKey = apiKey;
		this.flowerImage = flowerImage;
		this.leavesImage = leavesImage;

	}

	public static void makeRequest() {

		OkHttpClient client = new OkHttpClient();

		// Build the multipart request body
		RequestBody requestBody = new MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("images", this.flowerImage.getName(),
						RequestBody.create(this.flowerImage, MediaType.parse("image/jpeg")))
				.addFormDataPart("organs", "flower")
				.addFormDataPart("images", this.leavesImage.getName(),
						RequestBody.create(this.leavesImage, MediaType.parse("image/jpeg")))
				.addFormDataPart("organs", "leaf")
				.build();

		Request request = new Request.Builder()
				.url(URL)
				.post(requestBody)
				.build();

		try (Response response = client.newCall(request).execute()) {
			if (response.isSuccessful() && response.body() != null) {
				String jsonString = response.body().string();
				System.out.println(jsonString);
			} else {
				System.out.println("Request failed: " + response.code());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}