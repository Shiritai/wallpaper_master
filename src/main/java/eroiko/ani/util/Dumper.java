package eroiko.ani.util;

import java.io.*;

public class Dumper {
    /* 負責讀資料 + 寫檔案 */
    public static void dump(InputStream in, OutputStream out) throws IOException {
        try (var input = new BufferedInputStream(in); 
        var output = new BufferedOutputStream(out)){ // try auto close
            var data = new byte[16384];
            var length = 0;
            while ((length = input.read(data)) != -1){
                output.write(data, 0, length);
            }
        }
    }

    public static void dump(InputStream in, OutputStream out, int approximateSize) throws IOException {
        try (var input = new BufferedInputStream(in); 
        var output = new BufferedOutputStream(out)){ // try auto close
            var data = new byte[approximateSize]; // 可以設定大小
            var length = 0;
            while ((length = input.read(data)) != 0){
                output.write(data, 0, length);
            }
        }
    }

    public static void dump(Reader in, Writer out){
        try (in; out){
            var data = new char[1024];
            int length = 0;
            while ((length = in.read(data)) != 0){
                out.write(data, 0, length);
            }
        } catch (IOException ie){
            ie.printStackTrace();
        }
    }
}
