package de.plixo.galactic.common;

import com.google.gson.JsonElement;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

@Deprecated
public class JsonUtil {
    public static void saveJsonObj(File file, JsonElement json) {
        try {
            if (!file.exists()) {
                makeFile(file);
            }

            StringWriter stringWriter = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(stringWriter);
            jsonWriter.setIndent("\t");
            jsonWriter.setLenient(true);
            Streams.write(json, jsonWriter);
            String str = stringWriter.toString();
            FileWriter fw = new FileWriter(file);
            fw.write(str);
            fw.close();
        } catch (IOException var6) {
            var6.printStackTrace();
        }
    }

    public static void makeFile(File file) {
        if (!file.exists()) {
            try {
                {
                    var ignored = file.getParentFile().mkdirs();
                }
                var ignored = file.createNewFile();
            } catch (Exception var2) {
                var2.printStackTrace();
            }
        }

    }

}
