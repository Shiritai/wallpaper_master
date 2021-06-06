package eroiko.ani.model.CLI.util;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class Dumper {
    public static void dump(Reader in, Writer out){
        try (in; out){
            var data = new char[1024];
            int length = 0;
            while ((length = in.read(data)) != -1){
                out.write(data, 0, length);
            }
        } catch (IOException ie){
            ie.printStackTrace();
        }
    }
}
