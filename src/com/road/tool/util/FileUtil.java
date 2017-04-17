package com.road.tool.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil
{
    public static String readFile(String filePath)
    {
        try
        {
            byte[] encoded = Files.readAllBytes(Paths.get(filePath));
            return new String(encoded, "UTF-8");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return "";
    }

    public static void wireFile(String fileName, String content)
    {
        File entityFile = new File(fileName);
        if (!entityFile.exists())
        {
            try
            {
                entityFile.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        try
        {
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(entityFile), "UTF-8");

            BufferedWriter writer = new BufferedWriter(out);
            writer.write(content);
            writer.flush();
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
