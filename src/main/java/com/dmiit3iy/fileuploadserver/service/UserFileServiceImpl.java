package com.dmiit3iy.fileuploadserver.service;

import com.dmiit3iy.fileuploadserver.model.UserFile;
import com.dmiit3iy.fileuploadserver.repository.UserFileRepository;
import com.dmiit3iy.fileuploadserver.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Service
public class UserFileServiceImpl implements UserFileService {
    @Value("${server.root}")
    private String rootPath;

    UserFileRepository userFileRepository;

    @Autowired
    public void setUserFileRepository(UserFileRepository userFileRepository) {
        this.userFileRepository = userFileRepository;
    }

    @Override
    public UserFile add(MultipartFile document, String serverPath) {
        try {
            String tmpPath = serverPath + "\\tmp";
            File tmp = new File(tmpPath);
            if (!tmp.exists()) {
                tmp.mkdir();
            }

            if (!document.isEmpty()) {
                String filename = document.getOriginalFilename();
                byte[] bytes = document.getBytes();
                File file = new File(tmpPath, filename);
                try (BufferedOutputStream bufferedOutputStream
                             = new BufferedOutputStream(new FileOutputStream(file))) {
                    bufferedOutputStream.write(bytes);
                }
                if (Util.isZip(file)) {
                    Util.extractZip(file.getPath());
                    String str = file.getPath();
                    file = new File(str.substring(0, str.lastIndexOf('.')));
                    if (file.isDirectory()) {
                        Util.processFilesFromFolder(file);
                    }
                }

                File[] folderEntries = tmp.listFiles();
                List<File> fileArrayList = Arrays.asList(folderEntries);
                Iterator iterator = fileArrayList.iterator();
                while (iterator.hasNext()) {
                    File f = (File) iterator.next();
                    if (f.isFile()) {
                        String pathF = serverPath + "\\" + f.getName();
                        if (userFileRepository.findUserFileByPath(pathF).isEmpty()) {
                            Files.copy(f.toPath(), Path.of(pathF), StandardCopyOption.REPLACE_EXISTING);
                            UserFile userFile = new UserFile();
                            String hash = Util.getMD5Hash(f.getPath());
                            userFile.setHash(hash);
                            userFile.setName(f.getName());
                            userFile.setPath(pathF);
                            userFileRepository.save(userFile);
                        } else {
                            UserFile uf = userFileRepository.findUserFileByPath(pathF).orElseThrow(() -> new IllegalArgumentException("Такого пользователя нет"));
                            String hash = Util.getMD5Hash(f.getPath());
                            if (!uf.getHash().equals(hash)) {
                                UserFile userFile = new UserFile();
                                userFile.setVersion(uf.getVersion() + 1);
                                String[] mass = filename.split("\\.");
                                String newFilename = mass[0] + "_V" + userFile.getVersion() + "." + mass[1];
                                userFile.setHash(hash);
                                userFile.setName(newFilename);
                                userFile.setPath(serverPath + "\\" + newFilename);
                                userFileRepository.save(userFile);
                                Files.copy(f.toPath(), Path.of(serverPath + "\\" + newFilename), StandardCopyOption.REPLACE_EXISTING);
                            }
                            Files.copy(f.toPath(), Path.of(pathF), StandardCopyOption.REPLACE_EXISTING);
                        }

                    }
                    if (f.isDirectory()) {

                        String pathF = serverPath + "\\" + f.getName();
                        File dest = new File(pathF);
                        if (!dest.exists()) {
                            Files.copy(f.toPath(), Path.of(pathF), StandardCopyOption.REPLACE_EXISTING);
                        }
                        processFilesFromFolderForCopy(f);
                        continue;
                    }
                }

                processFilesFromForDelete(tmp);
            }


        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("This file already added!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    @Override
    public UserFile getFileMime(HttpServletResponse response, String filename) {
        return null;
    }

    @Override
    public List<UserFile> getAllFiles() {
        return userFileRepository.findAll();
    }

    @Override
    public List<File> get() {
        File file = new File(rootPath);
        File[] files = file.listFiles();
        List<File> list = Arrays.asList(files);
        return list;
    }

    @Override
    public File getRoot() {
        File file = new File(rootPath);
        return file;
    }


    public boolean isDirectoryEmpty(File directory) {
        String[] files = directory.list();
        return files.length == 0;
    }

    public static void processFilesFromFolderForCopy(File folder) throws IOException {
        File[] folderEntries = folder.listFiles();
        List<File> fileArrayList = Arrays.asList(folderEntries);
        Iterator iterator = fileArrayList.iterator();
        while (iterator.hasNext()) {
            File f = (File) iterator.next();
            if (f.isFile()) {
                System.out.println(f.getAbsolutePath());

            }
            if (f.isDirectory()) {
                processFilesFromFolderForCopy(f);
                continue;
            }
        }
    }

    public static void processFilesFromForDelete(File folder) throws IOException {
        File[] folderEntries = folder.listFiles();
        List<File> fileArrayList = Arrays.asList(folderEntries);
        Iterator iterator = fileArrayList.iterator();
        while (iterator.hasNext()) {
            File f = (File) iterator.next();
            if (f.isFile()) {
                f.delete();
            }
            if (f.isDirectory()) {
                processFilesFromFolderForCopy(f);
                continue;
            }
        }
        folder.delete();
    }
}
