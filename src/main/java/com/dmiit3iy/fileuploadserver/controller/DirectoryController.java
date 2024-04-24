package com.dmiit3iy.fileuploadserver.controller;

import com.dmiit3iy.fileuploadserver.dto.ResponseResult;
import com.dmiit3iy.fileuploadserver.service.UserFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/directory")
public class DirectoryController {

    private UserFileService userFileService;

    @Autowired
    public void setUserFileService(UserFileService userFileService) {
        this.userFileService = userFileService;
    }

    @Value("${server.root}")
    private String serverRoot;


    @GetMapping("/all")
    public ResponseEntity<ResponseResult<List<File>>> get() {
        try {
            List<File> list = userFileService.get();
            return new ResponseEntity<>(new ResponseResult<>(null, list), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ResponseResult<>(e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<ResponseResult<File>> getRoot() {
        try {
            File file = userFileService.getRoot();
            return new ResponseEntity<>(new ResponseResult<>(null, file), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ResponseResult<>(e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Метод для проверки отображения
     *
     * @param f
     */
    public void print(File f) {
        for (File x : f.listFiles()) {
            if (x.isDirectory()) {
                System.out.println(x.getName() + "  каталог");
                print(x);
            } else {
                System.out.println(x.getName() + " файл");
            }
        }
    }
}
