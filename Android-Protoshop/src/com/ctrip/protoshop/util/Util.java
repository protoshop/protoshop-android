package com.ctrip.protoshop.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.ctrip.protoshop.ProtoshopApplication;
import com.ctrip.protoshop.constans.Constans;

public class Util {
    public static Typeface getIconTypeface(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/fontawesome.ttf");
    }

    public static File getRootFile() {
        File file;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            file = new File(Environment.getExternalStorageDirectory(), Constans.APP_FOLDER);
            if (!file.exists()) {
                file.mkdirs();
            }
        } else {
            file = Environment.getDataDirectory();
        }
        return file;
    }

    public static File getUserRootFile() {
        String userName = ProtoshopApplication.getInstance().userName;
        File file = new File(getRootFile(), userName);

        if (!file.exists()) {
            file.mkdirs();
        }

        return file;
    }
    
    public static File getAppFloder(String appId){
        File file = new File(getUserRootFile(), appId);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    

    public static void saveScence(String appID, String id, String content) {
        try {
            File file = new File(getUserRootFile(), appID);
            if (!file.exists()) {
                file.mkdirs();
            }

            File contentFile = new File(file, id);
            if (!contentFile.exists()) {
                contentFile.createNewFile();
            }

            FileWriter writer = new FileWriter(contentFile);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readStream(InputStream inputStream) {

        StringBuffer buffer = new StringBuffer();
        try {
            String str;
            InputStreamReader streamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader reader = new BufferedReader(streamReader);
            try {
                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                    buffer.append("\n");
                }
                reader.close();
                streamReader.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
          
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
        return buffer.toString();
    }

    public static String readFileContent(File floder, String fileName) {
        try {
            File file = new File(floder, fileName);
            if (file.exists()) {
                FileInputStream inputStream = new FileInputStream(file);
                return readStream(inputStream);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getTodayDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String str = "最后更新:今天";
        return str + format.format(date);
    }

    /**
     * 解压到指定目录
     * @param zipPath
     * @param descDir
     * @author isea533
     */
    public static void unZipFiles(String zipPath, String descDir) throws IOException {
        unZipFiles(new File(zipPath), descDir);
    }

    /**
     * 解压文件到指定目录
     * @param zipFile
     * @param descDir
     * @author isea533
     */

    @SuppressWarnings("rawtypes")
    public static void unZipFiles(File zipFile, String descDir) throws IOException {
        File pathFile = new File(descDir);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        ZipFile zip = new ZipFile(zipFile);
        for (Enumeration entries = zip.entries(); entries.hasMoreElements();) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            String zipEntryName = entry.getName();
            InputStream in = zip.getInputStream(entry);
            String outPath = (descDir + File.separator + zipEntryName).replaceAll("\\*", "/");;
            //判断路径是否存在,不存在则创建文件路径  
            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
            if (!file.exists()) {
                file.mkdirs();
            }
            //判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压  
            if (new File(outPath).isDirectory()) {
                continue;
            }
            //输出文件路径信息  
            System.out.println(outPath);

            OutputStream out = new FileOutputStream(outPath);
            byte[] buf1 = new byte[1024];
            int len;
            while ((len = in.read(buf1)) > 0) {
                out.write(buf1, 0, len);
            }
            in.close();
            out.close();
        }
        System.out.println("******************解压完毕********************");
    }

    public static void saveFile(String fileName, String content) {
        try {
            File file = new File(getUserRootFile(), fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        }
    }

    public static void hideSoftkeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String spliceParam(Map<String, String> params) {
        StringBuffer buffer = new StringBuffer();
        if (params != null) {
            Set<String> keys = params.keySet();
            for (String key : keys) {
                buffer.append("&");
                buffer.append(key);
                buffer.append("=");
                buffer.append(params.get(key));
            }
        }
        return buffer.toString();
    }
}
