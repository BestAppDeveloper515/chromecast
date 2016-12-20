package app.rayscast.air.utils;

import com.google.common.io.Files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;

public class SubtitleConverter {

    private static SubtitleConverter instance = null;

    public static SubtitleConverter getInstance(){
        if (instance == null){
            instance = new SubtitleConverter();
        }
        return instance;
    }

    public File convert(File other, File dir, String lang) throws IOException{

        String name = other.getName();
        int index = name.lastIndexOf('.');
        String ext = name.substring(index + 1);
        String noext = name.substring(0, index);

        String vttFilePath = dir + "/" + noext + ".vtt";
        File vttFile = null;

        switch (ext){
            case "vtt":
                vttFile = new File(vttFilePath);
                Files.copy(other, vttFile);
                break;
            case "srt":
                vttFile = convertSrt(other, vttFilePath, lang);
                break;


        }

        return vttFile;
    }

    private File convertSrt(File srt, String vttFilePath, String lang) throws IOException{
        File vtt = new File(vttFilePath);

        BufferedReader in = null;
        Writer out = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(srt);
            String title = Utils.inputstreamToCharsetString(fileInputStream, lang);
            StringReader reader = new StringReader(title);
            in = new BufferedReader(reader);
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(vtt),"UTF-8"));

            out.write("WEBVTT FILE");
            out.write('\n');
            out.write('\n');

            String line = null;
            while ((line = in.readLine()) != null) {
                //#number of segment
                out.write(line);
                out.write('\n');

                //timing
                line = in.readLine();
                line = line.replaceAll(",",".");
                out.write(line);
                out.write('\n');

                //text
                StringBuilder sb = new StringBuilder();
                line = in.readLine();
                sb.append(line);
                while (!(line = in.readLine()).equals("")){
                    sb.append(" ");
                    sb.append(line);
                }
                out.write(sb.toString());
                out.write('\n');
                out.write('\n');

            }

        }catch(NullPointerException npe){
            int x = 3;
        }finally{
            if (in != null)
                in.close();
            if (out != null) {
                out.flush();
                out.close();
            }
        }

        return vtt;
    }
}
