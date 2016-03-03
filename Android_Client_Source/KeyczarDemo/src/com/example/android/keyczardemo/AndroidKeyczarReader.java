/**
 * Copyright 2012 Kenny Root
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.keyczardemo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.keyczar.KeyMetadata;
import org.keyczar.exceptions.KeyczarException;
import org.keyczar.interfaces.KeyczarReader;

import android.content.res.AssetManager;
import android.content.res.Resources;

/**
 * Provides a method to read <a href="http://www.keyczar.org/">Keyczar</a> keys
 * from Android application resources. All keys must be stored in a subdirectory
 * under the application's "assets" directory.
 * 
 * @author Kenny Root
 */
public class AndroidKeyczarReader implements KeyczarReader {
    private final static String META = "meta";

    private final static Charset CHARSET_UTF8 = Charset.forName("UTF-8");

    private final AssetManager mAssetManager;
    
    private final String mSubDirectory;

    /**
     * Constructs a new Keyczar key reader from Android application resources.
     *
     * @param resources
     *            resources for the current Android application
     * @param assetSubDirectory
     *            subdirectory under the application's "assets" directory. Can
     *            be {@code null}.
     */
    public AndroidKeyczarReader(Resources resources, String assetSubDirectory) {
        mAssetManager = resources.getAssets();
        mSubDirectory = assetSubDirectory;
    }

    /* (non-Javadoc)
     * @see org.keyczar.interfaces.KeyczarReader#getKey()
     */
    public String getKey() throws KeyczarException {
        final KeyMetadata metadata = KeyMetadata.read(getMetadata());
        final String keyName = String.valueOf(metadata.getPrimaryVersion().getVersionNumber());
        return getFileContentAsString(keyName);
    }

    /* (non-Javadoc)
     * @see org.keyczar.interfaces.KeyczarReader#getKey(int)
     */
    public String getKey(int keyVersion) throws KeyczarException {
        return getFileContentAsString(String.valueOf(keyVersion));
    }

    /* (non-Javadoc)
     * @see org.keyczar.interfaces.KeyczarReader#getMetadata()
     */
    public String getMetadata() throws KeyczarException {
        return getFileContentAsString(META);
    }

    private String getFileContentAsString(String filename) throws KeyczarException {
        try {
            final char[] buf = new char[1024];
            final StringBuilder sb = new StringBuilder();
            final InputStream is = mAssetManager.open(getFullFilename(filename));
            final InputStreamReader isr = new InputStreamReader(is, CHARSET_UTF8);

            int numRead;
            while ((numRead = isr.read(buf)) > 0) {
                sb.append(buf, 0, numRead);
            }
            return sb.toString();
        } catch (IOException e) {
            throw new KeyczarException("Couldn't read Keyczar 'meta' file from assets/", e);
        }
    }

    private String getFullFilename(String filename) {
        if (mSubDirectory == null) {
            return filename;
        }

        return mSubDirectory + File.separator + filename;
    }
}
