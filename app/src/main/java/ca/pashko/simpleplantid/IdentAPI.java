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
	private static final String IMAGE1 = "/data/media/image_1.jpeg";
	private static final String IMAGE2 = "/data/media/image_2.jpeg";
	private static final String PROJECT = "all";
	private static final String URL = "https://my-api.plantnet.org/v2/identify/" + PROJECT + "?api-key=YOUR-PRIVATE-API-KEY-HERE";

	public static void makeRequest() {
		File file1 = new File(IMAGE1);
		File file2 = new File(IMAGE2);

		OkHttpClient client = new OkHttpClient();

		// Build the multipart request body
		RequestBody requestBody = new MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("images", file1.getName(),
						RequestBody.create(file1, MediaType.parse("image/jpeg")))
				.addFormDataPart("organs", "flower")
				.addFormDataPart("images", file2.getName(),
						RequestBody.create(file2, MediaType.parse("image/jpeg")))
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