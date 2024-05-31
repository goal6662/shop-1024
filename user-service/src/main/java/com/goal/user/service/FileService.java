package com.goal.user.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    /**
     * 上传文件
     * @param file 要上传的文件
     * @return 文件的访问路径
     */
    String uploadFile(MultipartFile file);

}
