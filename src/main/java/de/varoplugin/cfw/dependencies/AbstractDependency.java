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

package de.varoplugin.cfw.dependencies;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.bukkit.plugin.Plugin;

abstract class AbstractDependency implements Dependency {

    private final String name;
    private final File folder;
    private final File file;
    private final String link;
    private final String sha512sum;
    private boolean loaded;

    AbstractDependency(String name, String folder, String link, String sha512sum) {
        this.name = name;
        this.folder = new File(folder);
        this.file = new File(this.folder, this.getName() + ".jar");
        this.link = link;
        this.sha512sum = sha512sum;
    }

    @Override
    public void load(Plugin plugin) throws Throwable {
        if (this.isLoaded())
            return;

        this.loaded = true;

        if (!this.folder.exists())
            this.folder.mkdirs();

        // download jar if necessary
        if (!this.file.exists() || this.file.length() == 0) {
            this.file.createNewFile();

            HttpURLConnection connection = (HttpURLConnection) new URL(this.link).openConnection();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (ReadableByteChannel readableByteChannel = Channels.newChannel(connection.getInputStream()); FileOutputStream fileOutputStream = new FileOutputStream(this.file)) {
                    fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                }
            } else
                throw new IOException("Invalid response code " + connection.getResponseCode());
        }

        // check signature
        this.checkSignature();

        this.init(plugin);
    }

    public void checkSignature() throws IOException, InvalidSignatureException, NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
        try (InputStream inputStream = new FileInputStream(this.file); DigestInputStream digestInputStream = new DigestInputStream(inputStream, messageDigest)) {
            byte[] buffer = new byte[4096];
            while (digestInputStream.read(buffer) == buffer.length)
                ;
        }

        String fileHash = Base64.getEncoder().encodeToString(messageDigest.digest());
        if (!fileHash.equals(this.sha512sum))
            throw new InvalidSignatureException(this.getFile(), this.sha512sum, fileHash);
    }

    protected abstract void init(Plugin plugin) throws Throwable;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public File getFile() {
        return this.file;
    }

    @Override
    public URL getUrl() throws MalformedURLException {
        return this.getFile().toURI().toURL();
    }

    @Override
    public boolean isLoaded() {
        return this.loaded;
    }
}
