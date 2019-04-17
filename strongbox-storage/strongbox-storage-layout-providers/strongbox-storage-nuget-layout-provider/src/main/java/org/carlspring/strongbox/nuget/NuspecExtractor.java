/*
 * Copyright 2019 Carlspring Consulting & Development Ltd.
 * Copyright 2014 Dmitry Sviridov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.carlspring.strongbox.nuget;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.carlspring.strongbox.storage.metadata.nuget.NugetFormatException;
import org.carlspring.strongbox.storage.metadata.nuget.Nuspec;

/**
 * Extracts nuspec file out of a nupkg package.
 * 
 * We have abstracted this class from methods in the original project, as it's
 * no longer
 * actively developed, or maintained and since
 * we didn't want to have to maintain yet another fork.
 * 
 * Notes on the modification of the original class:
 * - The original class was called ClassicNupkg, which we have now renamed to
 * NuspecExtractor as we copied methods related to Nuspec extraction.
 * - The original package name has been changed to a more appropriate one for
 * our project.
 * - Applied the coding convention of the project.
 * 
 * @author sviridov and contributors
 *
 */
public class NuspecExtractor
{
    
    /**
     * Extract specification file from stream with NuPkg package
     *
     * @param package
     *            stream stream package
     * @return specification file
     * @throws IOException
     *             read error
     * @throws NugetFormatException
     *             XML in the package archive does not conform to the NuGet
     *             specification
     */
    public static final Nuspec loadNuspec(InputStream packageStream)
        throws IOException,
        NugetFormatException
    {
        try (ZipInputStream zipInputStream = new ZipInputStream(packageStream);)
        {
            ZipEntry entry;
            do
            {
                entry = zipInputStream.getNextEntry();
            } while (entry != null && !isNuspecZipEntry(entry));

            if (entry == null)
            {
                return null;
            }

            return Nuspec.parse(zipInputStream);
        }
    }

    /**
     * ZIP attachment is Nuspec XML specification
     *
     * @param entry
     *            zip attachment
     * @return true if the attachment matches the attachment with the
     *         specification
     */
    private static boolean isNuspecZipEntry(ZipEntry entry)
    {
        return !entry.isDirectory() && entry.getName().endsWith(Nuspec.DEFAULT_FILE_EXTENSION);
    }

}
