package com.goal.user.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectResult;
import com.goal.user.config.OSSConfig;
import com.goal.user.service.FileService;
import com.goal.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Resource
    private OSSConfig ossConfig;

    @Override
    public String uploadFile(MultipartFile file) {

        // 1. 获取相关配置
        String bucketName = ossConfig.getBucketName();
        String endpoint = ossConfig.getEndpoint();
        String accessKeyId = ossConfig.getAccessKeyId();
        String accessKeySecret = ossConfig.getAccessKeySecret();

        OSS ossClient = new OSSClientBuilder()
                .build(endpoint, accessKeyId, accessKeySecret);


        // 2. 获取文件原始名称
        String originalFilename = file.getOriginalFilename();

        // 3. 以日期作为文件夹名称
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String folder = dtf.format(now);

        // 4. 拼接路径 user/2023/12/1/random.jpg OSS会自动创建
        String fileName = CommonUtil.generateUUID();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

        String newFileName = "user/" + folder + "/" + fileName + extension;

        try {
            PutObjectResult putObjectResult = ossClient.putObject(bucketName, newFileName, file.getInputStream());
            // 拼接返回文件的访问路径
            if (putObjectResult != null) {
                return "https://" + bucketName + "." + endpoint + "/" + newFileName;
            }

        } catch (IOException e) {
            log.error("文件[{}]上传失败", originalFilename);
        } finally {
            // 关闭OSS客户端
            ossClient.shutdown();
        }

        return null;
    }
}
