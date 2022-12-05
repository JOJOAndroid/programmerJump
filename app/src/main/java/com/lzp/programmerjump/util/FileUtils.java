package com.lzp.programmerjump.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class FileUtils {

    private static final String TAG = "FileUtils";

    private FileUtils() {
    }

    public static String write(String semantic, String fileName) {
        FileOutputStream fos = null;
        try {
            File dir = new File(Environment.getExternalStorageDirectory(), "flow-request");
            dir.mkdir();
            File file = new File(dir, fileName);
            fos = new FileOutputStream(file);
            fos.write(semantic.getBytes());
            fos.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    public static boolean exists(String fileName) {
        File file = new File(Environment.getExternalStorageDirectory(),
                fileName);
        return file != null && file.exists();
    }

    public static void remove(String fileName) {
        File file = new File(Environment.getExternalStorageDirectory(),
                fileName);
        if (exists(fileName)) {
            file.delete();
        }
    }

    public static boolean copyAssetsFile(Context context, String assetPath, String desFileName) throws Exception {
        InputStream is = null;
        OutputStream os = null;
        try {
            AssetManager am = context.getAssets();
            is = am.open(assetPath);
            File outFile = new File(desFileName);
            /**
             * 以下逻辑在拷贝软降噪配置文件有可能导致拷贝失败，失败的场景：
             * 1. 先安装语音APP。再push语音数据,然后启动语音。
             * 这种case可以触发拷贝逻辑，但是会在下面这个if语句内直接return，造成拷贝失败
             */
            /*int assetLen = is.available();
            if (outFile.exists() && outFile.length() == assetLen
                    && outFile.lastModified() > PackageUtils.getApkUpdateTime(context)) {
                LogUtils.d(TAG, "copyAssetsFile file exists");
                return true;
            }*/

            File out = new File(desFileName);
            if (out.getParentFile() != null) {
            if (!out.getParentFile().exists()) {
                out.getParentFile().mkdirs();
            }
            }
            os = new FileOutputStream(outFile);
            byte[] buf = new byte[8096];
            int len;
            while ((len = is.read(buf)) > 0) {
                os.write(buf, 0, len);
            }

        } catch (Exception e) {
            Log.e(TAG, "copyAssetsFile fail" + e);
            throw e;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                }
            }
        }
        Log.i(TAG, "copyAssetsFile ok");
        return true;
    }

    public static boolean copyFile(File sourceFile, File targetFile) {
        if (!sourceFile.exists()) {
            return false;
        }
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            byte[] b = new byte[1024];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            outBuff.flush();
        } catch (IOException e) {
            Log.e(TAG, "copy file error" + e);
            return false;
        } finally {
            if (inBuff != null)
                try {
                    inBuff.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (outBuff != null)
                try {
                    outBuff.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return true;
    }

    public static boolean copyFileSmart(File sourceFile, File targetFile) {
        if (!sourceFile.exists()) {
            return false;
        }
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            if (inBuff.available() == targetFile.length()) {
                inBuff.close();
                return true;
            }

            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
            byte[] b = new byte[1024];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            outBuff.flush();
        } catch (IOException e) {
            return false;
        } finally {
            if (inBuff != null)
                try {
                    inBuff.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (outBuff != null)
                try {
                    outBuff.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return true;
    }

    // 复制文件夹
    public static boolean copyDirectory(String sourceDir, String targetDir) throws IOException {
        if (!(new File(sourceDir).exists())) {
            return false;
        }

        boolean result = true;
        File tFile = new File(targetDir);
        if (!tFile.exists()) {
            tFile.mkdirs();
        }

        File[] file = (new File(sourceDir)).listFiles();
        if (file == null) {
            return result;
        }
        for (int i = 0; i < file.length; i++) {
            if (file[i].isFile()) {
                File sourceFile = file[i];
                File targetFile = new File(new File(targetDir).getAbsolutePath() + File.separator + file[i].getName());
                result = (result && copyFile(sourceFile, targetFile));
            }
            if (file[i].isDirectory()) {
                String dir1 = sourceDir + "/" + file[i].getName();
                String dir2 = targetDir + "/" + file[i].getName();
                result = (result && copyDirectory(dir1, dir2));
            }
        }

        return result;
    }

    /**
     * 删除文件或目录
     *
     * @param filepath
     * @throws IOException
     */
    public static boolean delete(String filepath) throws IOException {
        File f = new File(filepath);
        if (f.exists()) {
            if (f.isDirectory()) {
                File delFile[] = f.listFiles();
                if (delFile != null) {
                    if (delFile.length == 0) {// 若目录下没有文件则直接删除
                        return f.delete();
                    } else {
                        boolean ret = true;
                        int i = delFile.length;
                        for (int j = 0; j < i; j++) {
                            if (delFile[j].isDirectory()) {
                                ret = (ret && delete(delFile[j].getAbsolutePath()));// 递归调用del方法并取得子目录路径
                            } else {
                                ret = (ret && delFile[j].delete());// 删除文件
                            }
                        }
                        return ret;
                    }
                } else {
                    return false;
                }
            } else if (f.isFile()) {
                return f.delete();
            }
        } else {
        }
        return false;
    }

    /**
     * Delete corresponding path, file or directory.
     *
     * @param file path to delete.
     */
    public static void delete(File file) {
        delete(file, false);
    }

    /**
     * Delete corresponding path, file or directory.
     *
     * @param file      path to delete.
     * @param ignoreDir whether ignore directory. If true, all files will be deleted while directories is reserved.
     */
    public static void delete(File file, boolean ignoreDir) {
        if (file == null || !file.exists()) {
            return;
        }
        if (file.isFile()) {
            file.delete();
            return;
        }

        File[] fileList = file.listFiles();
        if (fileList == null) {
            return;
        }

        for (File f : fileList) {
            delete(f, ignoreDir);
        }
        // delete the folder if need.
        if (!ignoreDir) file.delete();
    }

    /**
     * Delete corresponding path, file or directory.
     *
     * @param file   path to delete.
     * @param filter filter to determine which file should be deleted, null for all files.
     */
    public static void delete(File file, FileFilter filter) {
        if (file == null || !file.exists()) {
            return;
        }
        if (file.isFile()) {
            // delete file if need.
            if (filter == null || filter.accept(file)) {
                file.delete();
            }
            return;
        }

        File[] fileList = file.listFiles();
        if (fileList == null) {
            return;
        }

        for (File f : fileList) {
            delete(f, filter);
        }
        // delete the folder if need.
        if (filter == null || filter.accept(file)) {
            file.delete();
        }
    }


    public static void writeFileData(String fileName, String message) {
        FileOutputStream fout = null;
        try {
            File fWrite = new File(fileName);
            fout = new FileOutputStream(fWrite);
            byte[] bytes = message.getBytes();
            fout.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void appendFileData(String fileName, String message) {
        FileOutputStream fout = null;
        try {
            File fWrite = new File(fileName);
            fout = new FileOutputStream(fWrite, true);
            byte[] bytes = message.getBytes();
            fout.write(bytes);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取指定文件大小
     *
     * @param
     * @return
     */
    public static long getFileSize(String fileName) {
        long size = -1;
        FileInputStream fis = null;
        try {
            File file = new File(fileName);
            if (file.exists()) {
                fis = new FileInputStream(file);
                size = fis.available();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return size;
    }

    public static boolean externalMemoryAvailable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取外置存储剩余空间，返回的数值单位是byte
     *
     * @return
     */
    public static long getAvailableExternalMemorySize() {
        long bytesAvailable = 0L;
        if (externalMemoryAvailable()) {
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
            } else {
                bytesAvailable = (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
            }
        }
        return bytesAvailable;
    }

    public static void createFileAndWriteFileData(String path, String message) {
        FileOutputStream fout = null;
        try {
            File fWrite = new File(path);
            if (!fWrite.exists()) {
                fWrite.createNewFile();
            }
            fout = new FileOutputStream(fWrite);
            byte[] bytes = message.getBytes();
            fout.write(bytes);
            Log.i(TAG, "write wecarid into sdcard");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static String readFileData(String fileName) {
        return readFileData(fileName, true);
    }

    public static String readFileData(String fileName, boolean autoCreate) {
        String res = "";
        FileInputStream fin = null;
        try {
            File fRead = new File(fileName);
            if (!fRead.exists() && autoCreate) {
                boolean ret = fRead.createNewFile();
                Log.d(TAG, "readFileData, but not exit. create = " + ret);
            }
            if (fRead.exists()) {
                fin = new FileInputStream(fRead);
                int length = fin.available();
                byte[] buffer = new byte[length];
                int read = fin.read(buffer);
                if (read < 0) {
                    Log.w(TAG, "file empty");
                }
                res = new String(buffer, "UTF-8");
            }
        } catch (Exception e) {
            Log.e(TAG, "readFileData fail" + e);
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        return res;
    }

    /**
     * 解析文件生成string类型
     * @param fileName
     * @return
     */
    public static String parseFiletoString(Context context,String fileName) {
        StringBuilder builder = new StringBuilder();
        AssetManager assets = context.getAssets();
        InputStream is = null;
        BufferedReader br = null;
        try {
            is = assets.open(fileName);
            br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (Exception e) {

            }
        }
        return builder.toString();
    }

    /**
     * 解析本地应用
     * @param path
     * @return
     */
    public static String parseLocalAppInfoFile(String path) {
        StringBuilder builder = new StringBuilder();
        String rootPath = Environment.getExternalStorageDirectory()
                .getPath();
        File file = new File(rootPath + path);
        if (!file.exists()) {
            return null;
        }
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            inputStream = new FileInputStream(file);
            if (inputStream != null) {
                inputStreamReader = new InputStreamReader(inputStream);
                bufferedReader = new BufferedReader(inputStreamReader);
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    builder.append(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }
}
