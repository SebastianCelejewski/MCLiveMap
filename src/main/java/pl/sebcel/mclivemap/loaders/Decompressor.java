package pl.sebcel.mclivemap.loaders;

import java.io.ByteArrayOutputStream;
import java.util.zip.Inflater;

public class Decompressor {

    private byte[] buffer = new byte[256 * 256 * 256];
    private Inflater decompressor = new Inflater();

    public synchronized byte[] decompress(byte[] input, int offset, int length) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            decompressor.reset();
            decompressor.setInput(input, offset, length);
            int readBytes = -1;
            while (readBytes != 0) {
                readBytes = decompressor.inflate(buffer);
                out.write(buffer, 0, readBytes);
            }
            out.close();

            return out.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to decompress data: " + ex.getMessage(), ex);
        }
    }
}