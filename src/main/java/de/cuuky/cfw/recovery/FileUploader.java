/*
 * MIT License
 * 
 * Copyright (c) 2020-2022 CuukyOfficial
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.cuuky.cfw.recovery;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class FileUploader {

	private static final String UPLOAD_URL = "https://api.anonfile.com/upload?token=894b0ea821338221", LINE_FEED = "\r\n";

	private File file;

	public FileUploader(File file) {
		this.file = file;
	}

	private JSONObject getJSONObject(JSONObject object, String path) {
		return (JSONObject) object.get(path);
	}

	private String uploadToServer(String link) {
		try {
			String boundary = "===" + System.currentTimeMillis() + "===";

			URL url = new URL(UPLOAD_URL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setUseCaches(false);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			connection.setRequestProperty("User-Agent", "CFW - CuukyFrameWork");

			OutputStream out = connection.getOutputStream();
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, "UTF-8"), true);

			writer.append("--" + boundary).append(LINE_FEED);
			writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + this.file.getName() + "\"").append(LINE_FEED);
			writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(this.file.getName())).append(LINE_FEED);
			writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
			writer.append(LINE_FEED);
			writer.flush();

			FileInputStream inputStream = new FileInputStream(this.file);
			byte[] buffer = new byte[4096];
			int bytesRead = -1;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
			out.flush();
			inputStream.close();

			writer.append(LINE_FEED);
			writer.flush();

			writer.append(LINE_FEED).flush();
			writer.append("--" + boundary + "--").append(LINE_FEED);
			writer.close();

			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
				return null;

			return new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public String uploadFile(String link) {
		try {
			String response = uploadToServer(link);

			if (response == null)
				return null;

			JSONObject object = (JSONObject) JSONValue.parseWithException(response);
			return (String) getJSONObject(getJSONObject(getJSONObject(object, "data"), "file"), "url").get("short");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
