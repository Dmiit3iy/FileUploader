package com.dmiit3iy.fileuploadserver.repository;

import com.dmiit3iy.fileuploadserver.model.UserFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserFileRepository extends JpaRepository<UserFile,Long> {
    Optional<UserFile> findUserFileByPath(String path);
}
