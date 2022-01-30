package pl.glmc.serverlinker.common;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Compression {
    public static byte[] compress(String data) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length());
            GZIPOutputStream gzip = new GZIPOutputStream(bos);
            gzip.write(data.getBytes(StandardCharsets.UTF_8));
            gzip.close();
            byte[] compressed = bos.toByteArray();
            bos.close();

            return compressed;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String decompress(final byte[] compressed) {
        try (final var bis = new ByteArrayInputStream(compressed); GZIPInputStream gis = new GZIPInputStream(bis)) {
            byte[] bytes = IOUtils.toByteArray(gis);

            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
