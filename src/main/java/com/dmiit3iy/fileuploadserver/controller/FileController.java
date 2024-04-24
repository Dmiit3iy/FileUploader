package com.dmiit3iy.fileuploadserver.controller;

import com.dmiit3iy.fileuploadserver.dto.ResponseResult;
import com.dmiit3iy.fileuploadserver.service.UserFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/file")
public class FileController {
    @Value("${server.root}")
    private String serverRoot;
    UserFileService userFileService;

    @Autowired
    public void setUserFileService(UserFileService userFileService) {
        this.userFileService = userFileService;
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ResponseResult<String>> save(@RequestPart MultipartFile document, @RequestParam String path) {
        try {
            userFileService.add(document, path);
            return new ResponseEntity<>(new ResponseResult<>(null, "File uploaded successfully"), HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ResponseResult<>("File already exist", null), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/dir")
    public ResponseEntity<ResponseResult<String>> addDir(@RequestParam String path) {
        File file = new File(serverRoot, path);
        if (file.exists()) {
            return new ResponseEntity<>(new ResponseResult<>(null, "Directory already exists"), HttpStatus.BAD_REQUEST);
        }
        if (file.mkdir()) {
            return new ResponseEntity<>(new ResponseResult<>(null, "File uploaded successfully"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ResponseResult<>(null, "Directory not created"), HttpStatus.BAD_REQUEST);
    }


}
