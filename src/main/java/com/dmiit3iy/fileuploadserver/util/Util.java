package com.dmiit3iy.fileuploadserver.util;

import com.dmiit3iy.fileuploadserver.model.UserFile;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import net.lingala.zip4j.ZipFile;
public class Util {

    public static String getMD5Hash(String fileName) throws IOException {
        try (InputStream is = Files.newInputStream(Paths.get(fileName))) {
            return DigestUtils.md5Hex(is);
        }
    }
    /**
     * Метод для проверки того что файл является ZIP архивом
     *
     * @param file
     * @return
     */
    public static boolean isZip(File file) {
        String fileExtension = file.getName().substring(file.getName().lastIndexOf('.') + 1);
        if (fileExtension.equals("zip")) {
            return true;
        }
        return false;
    }


    /**
     * Метод для распаковки содержимого архива и последующего удаления исходного файла
     *
     * @param str
     * @throws IOException
     */
    public static void extractZip(String str) throws IOException {

        String strWithoutExtension = str.substring(0, str.lastIndexOf('.'));
        try (ZipFile zipFile = new ZipFile(str)) {
            zipFile.extractAll(strWithoutExtension);
        }
        File file = new File(str);
        file.delete();
    }

    public static void processFilesFromFolder(File folder) throws IOException {
        File[] folderEntries = folder.listFiles();
        List<File> fileArrayList = Arrays.asList(folderEntries);
        Iterator iterator = fileArrayList.iterator();
        while (iterator.hasNext()) {
            File f = (File) iterator.next();
            if (isZip(f)) {
                extractZip(f.getAbsolutePath());
                String str = f.getAbsolutePath();
                String strWithoutExtension = str.substring(0, str.lastIndexOf('.'));
                File file = new File(strWithoutExtension);
                if (file.isDirectory()) {
                    processFilesFromFolder(file);
                    continue;
                }
            }
            if (f.isDirectory()) {
                processFilesFromFolder(f);
                continue;
            }
        }

    }

    /**
     * Компоратор для сравнения файлов по хэшу
     */
    public static Comparator<UserFile> userFileComparator = new Comparator<UserFile>() {
        @Override
        public int compare(UserFile o1, UserFile o2) {
            return o1.getHash().compareTo(o2.getHash());
        }
    };

}
