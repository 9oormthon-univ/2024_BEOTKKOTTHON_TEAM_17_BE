package org.goormuniv.ponnect.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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

    private static final String PROFILE_IMAGE = "profileImg";

    private final AmazonS3Client amazonS3Client;


    //Upload To S3 with Object
    public String uploadProfileImg(String username, MultipartFile object) throws Exception {
        log.info("업로드 메서드 호출");
        String originalName = object.getOriginalFilename();
        String extension = Objects.requireNonNull(originalName).substring(originalName.lastIndexOf(".") + 1);
        String generateFileName = UUID.randomUUID() + "." + extension;
        log.info(generateFileName);


        String filename = username +  File.separator + PROFILE_IMAGE + File.separator +  generateFileName;
        log.info(filename);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(object.getContentType() + ";charset=utf-8");
        objectMetadata.setContentEncoding("UTF-8");
        objectMetadata.setContentLength(object.getInputStream().available());

        amazonS3Client.putObject(bucket, filename, object.getInputStream(), objectMetadata);
        log.info(amazonS3Client.getUrl(bucket, filename).toString());
        return amazonS3Client.getUrl(bucket, filename).toString();

    }


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