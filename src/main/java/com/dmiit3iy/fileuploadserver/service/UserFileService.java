package com.dmiit3iy.fileuploadserver.service;

import com.dmiit3iy.fileuploadserver.model.UserFile;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;

public interface UserFileService {
    UserFile add(MultipartFile document, String serverPath);
    UserFile getFileMime (HttpServletResponse response, String filename);
    List<UserFile> getAllFiles();
    List<File> get();
    File getRoot();
}
