/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.main.download;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DependencyDownloaderClassLoader extends URLClassLoader {

    private static final URL[] EMPTY_URL_ARRAY = new URL[0];

    public DependencyDownloaderClassLoader(ClassLoader parent) {
        super(EMPTY_URL_ARRAY, parent);
    }

    public void addFile(File file) {
        try {
            super.addURL(file.toURI().toURL());
        } catch (MalformedURLException e) {
            throw new DownloadException("Error adding JAR to classloader: " + file, e);
        }
    }

    public List<String> getDownloaded() {
        return Arrays.stream(getURLs()).map(URL::getFile).collect(Collectors.toList());
    }
}
