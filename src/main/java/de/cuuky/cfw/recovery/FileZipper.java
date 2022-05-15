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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileZipper {

	private static final int BUFFER_SIZE = 4096;

	protected File zipFile;

	public FileZipper(File zipFile) {
		this.zipFile = zipFile;
	}

	private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
		File oldFile = new File(filePath);
		if (oldFile.exists())
			oldFile.delete();

		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
		byte[] bytesIn = new byte[BUFFER_SIZE];
		int read = 0;
		while ((read = zipIn.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();
	}

	private void zipFile(File file, ZipOutputStream outputStream, Path root) {
		if (file.getName().endsWith(".zip"))
			return;

		try {
			Path orgPath = Paths.get(file.getPath());
			Path zipFilePath = root.relativize(orgPath);

			outputStream.putNextEntry(new ZipEntry(zipFilePath.toString()));
			byte[] buffer = Files.readAllBytes(orgPath);
			outputStream.write(buffer, 0, buffer.length);
			outputStream.closeEntry();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void zipFolder(File file, ZipOutputStream outputStream, Path root) {
		for (File toZip : file.listFiles()) {
			if (toZip.isFile())
				zipFile(toZip, outputStream, root);
			else
				zipFolder(toZip, outputStream, root);
		}
	}

	public void zip(ArrayList<File> files, Path rootFrom) {
		try {
			File file1 = new File(zipFile.getParent());
			if (!file1.isDirectory())
				file1.mkdirs();
			if (!zipFile.exists())
				zipFile.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		String zipFileName = zipFile.getPath();
		try {
			FileOutputStream fileoutputStream = null;
			ZipOutputStream outputStream = new ZipOutputStream(fileoutputStream = new FileOutputStream(zipFileName));

			for (File toZip : files)
				if (toZip.isFile())
					zipFile(toZip, outputStream, rootFrom);
				else
					zipFolder(toZip, outputStream, rootFrom);

			outputStream.close();
			fileoutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean unzip(String destDirectory) {
		try {
			File destDir = new File(destDirectory);
			if (!destDir.exists())
				destDir.mkdir();
			ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFile));
			ZipEntry entry = zipIn.getNextEntry();
			while (entry != null) {
				String filePath = destDirectory + File.separator + entry.getName();
				if (!entry.isDirectory()) {
					extractFile(zipIn, filePath);
				} else {
					File dir = new File(filePath);
					dir.mkdir();
				}
				zipIn.closeEntry();
				entry = zipIn.getNextEntry();
			}
			zipIn.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public File getZipFile() {
		return this.zipFile;
	}
}