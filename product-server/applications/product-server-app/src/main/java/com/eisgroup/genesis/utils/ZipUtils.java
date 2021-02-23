/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.eisgroup.genesis.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.IOUtils;

/**
 * Utils to work with zip archives
 *
 * @author dlevchuk
 * @since 10.9
 */
public final class ZipUtils {

    private ZipUtils() {}

    public static byte[] zip(Supplier<ZipEntryWithContent>... zipEntries) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(baos))) {

            for (Supplier<ZipEntryWithContent> zipEntry : zipEntries) {

                zipOutputStream.putNextEntry(zipEntry.get().getZipEntry());
                IOUtils.copy(zipEntry.get().getContent(), zipOutputStream);
                zipOutputStream.closeEntry();
            }

            zipOutputStream.finish();
            zipOutputStream.flush();

            return baos.toByteArray();
        }
    }

    public interface ZipEntryWithContent {
        ZipEntry getZipEntry();
        InputStream getContent();
    }
}
