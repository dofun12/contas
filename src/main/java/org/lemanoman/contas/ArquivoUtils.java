package org.lemanoman.contas;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ArquivoUtils {

    public static void copyFromClasspath(String resource, File output, Class clazz){
        try {

            InputStream inputStream = clazz.getResourceAsStream(resource);
            FileOutputStream fos = new FileOutputStream(output);

            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            fos.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
