package com.io.bio;

import java.io.*;

/**
 * @author bxwang
 */
public class BIOFileReader {
    private static final String PATH = "C:\\Users\\bxwang\\Desktop\\new.txt";


    public static void main(String[] args) {
        // 1. 定义文件路径
        File file = new File(PATH);
        // 2. 定义读入流
        FileInputStream fileInputStream = null;
        // 3. 定义字符流 对二进制流进行解码  "US-ASCII"
        BufferedReader reader = null;
        IOException processException = null;

        try {
            fileInputStream = new FileInputStream(file);

//            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            reader = new BufferedReader(new InputStreamReader(fileInputStream));

            reader.lines().forEach(System.out::println);

        } catch (IOException pException) {
            processException = pException;

        } finally {
            // 流的关闭
            // 1 只关闭最外层流
            // 2 由外到内逐个关闭
            // 3 将外层流放在try-catch-resources中
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    if (processException != null) {
                        //throw new MyException(processException, e, "Error message..." + PATH);
                    } else {
                        //throw new MyException(e, "Error closing InputStream for file " + PATH;
                    }
                }
            }
            if (processException != null) {
                //throw new MyException(processException, "Error processing InputStream for file " + PATH;
            }
        }
    }
}
