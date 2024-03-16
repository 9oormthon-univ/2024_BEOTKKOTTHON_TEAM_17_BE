package org.goormuniv.ponnect.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonStorageUtil {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private static final String PROFILE_IMAGE = "profileOR";

    private final AmazonS3Client amazonS3Client;


    //Upload To S3 with Object
    public String uploadProfileQR(Long userId, ByteArrayOutputStream byteArrayOutputStream) throws Exception {
        log.info("업로드 메서드 호출");
        String generateFileName = UUID.randomUUID().toString().substring(0,8) + ".png";

        String filename = userId +  File.separator + PROFILE_IMAGE + File.separator +  generateFileName;
        log.info(filename);
        byte[] qrCodeBytes = byteArrayOutputStream.toByteArray();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType("image/png" + ";charset=utf-8");
        objectMetadata.setContentEncoding("UTF-8");
        objectMetadata.setContentLength(qrCodeBytes.length);
        InputStream inputStream = new ByteArrayInputStream(qrCodeBytes);
        amazonS3Client.putObject(bucket, filename, inputStream, objectMetadata);
        log.info(amazonS3Client.getUrl(bucket, filename).toString());
        return amazonS3Client.getUrl(bucket, filename).toString();

    }


    @Deprecated
    //Delete for Object
    public void deleteFile(String uploadFilePath) {
        List<String> splitPath = Arrays.stream(uploadFilePath.split("/")).toList().subList(3, 6);
        String oAuth2Id = splitPath.get(0);
        String postId = splitPath.get(1);
        String fileName = splitPath.get(2);
        String s3Key = oAuth2Id + "/" + postId + "/" + fileName;
        log.info(s3Key);

        try {
            boolean isObjectExist = amazonS3Client.doesObjectExist(bucket, s3Key);
            if (isObjectExist) {
                log.info("존재함");
                amazonS3Client.deleteObject(bucket, s3Key);
            }
            else{
                log.info("존재 안함");
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            log.info("Delete File failed");
        }

    }


}