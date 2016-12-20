package app.rayscast.air.utils;


import org.mozilla.universalchardet.UniversalDetector;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class Utils {


    /**
     * Get the charset of the contents of an {@link InputStream}
     *
     * @param inputStream {@link InputStream}
     * @param languageCode Language code for charset override
     * @return Charset String name
     * @throws IOException
     */
    public static String inputstreamToCharsetString(InputStream inputStream, String languageCode) throws IOException {
        UniversalDetector charsetDetector = new UniversalDetector(null);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        UnicodeBOMInputStream bomInputStream = new UnicodeBOMInputStream(inputStream);
        bomInputStream.skipBOM();
        byte data[] = new byte[1024];
        int count;
        while ((count = bomInputStream.read(data)) != -1) {
            if (!charsetDetector.isDone()) {
                charsetDetector.handleData(data, 0, count);
            }
            byteArrayOutputStream.write(data, 0, count);
        }
        charsetDetector.dataEnd();

        String detectedCharset = charsetDetector.getDetectedCharset();
        charsetDetector.reset();

        if (detectedCharset == null || detectedCharset.isEmpty()) {
            detectedCharset = "UTF-8";
        } else if ("MACCYRILLIC".equals(detectedCharset)) {
            detectedCharset = "Windows-1256";
        }

        /*if (languageCode != null && sOverrideMap.containsKey(languageCode) && !detectedCharset.equals("UTF-8")) {
            detectedCharset = sOverrideMap.get(languageCode);
        }*/

        byte[] stringBytes = byteArrayOutputStream.toByteArray();
        Charset charset = Charset.forName(detectedCharset);
        CharsetDecoder decoder = charset.newDecoder();

        try {
            CharBuffer charBuffer = decoder.decode(ByteBuffer.wrap(stringBytes));
            return charBuffer.toString();
        } catch (CharacterCodingException e) {
            return new String(stringBytes, detectedCharset);
        }
    }

    /**
     * Save {@link InputStream} to {@link File}
     *
     * @param inputStream InputStream that will be saved
     * @param path        Path of the file
     * @throws IOException
     */
    public static void saveStringFile(InputStream inputStream, File path) throws IOException {
        String outputString = inputstreamToCharsetString(inputStream, null);
        saveStringToFile(outputString, path, "UTF-8");
    }

    /**
     * Save {@link String} to {@link File}
     *
     * @param inputStr String that will be saved
     * @param path     Path of the file
     * @throws IOException
     */
    public static void saveStringFile(String inputStr, File path) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(inputStr.getBytes());
        saveStringFile(inputStream, path);
    }

    /**
     * Save {@link String} array  to {@link File}
     *
     * @param inputStr String array that will be saved
     * @param path     {@link File}
     * @throws IOException
     */
    public static void saveStringFile(String[] inputStr, File path) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : inputStr) {
            stringBuilder.append(str).append("\n");
        }
        saveStringFile(stringBuilder.toString(), path);
    }

    /**
     * Save {@link String} to {@link File} witht the specified encoding
     *
     * @param string {@link String}
     * @param path   Path of the file
     * @param string Encoding
     * @throws IOException
     */
    public static void saveStringToFile(String string, File path, String encoding) throws IOException {
        CustomLog.e("META_AND_SUBS_TEST", "Saving to path: "+path.toString());
        if (path.exists()) {
            path.delete();
        }

        if ((path.getParentFile().mkdirs() || path.getParentFile().exists()) && (path.exists() || path.createNewFile())) {
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), encoding));
            writer.write(string);
            writer.close();
            CustomLog.d("META_AND_SUBS_TEST", "Saved!");
        }
    }

    /**
     * Get the charset of the contents of an {@link InputStream}
     *
     * @param inputStream {@link InputStream}
     * @return Charset String name
     * @throws IOException
     */
    public static String inputstreamToCharsetString(InputStream inputStream) throws IOException {
        return inputstreamToCharsetString(inputStream, null);
    }


}
