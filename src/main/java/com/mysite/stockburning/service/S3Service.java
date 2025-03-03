package com.mysite.stockburning.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.mysite.stockburning.configuration.S3Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Config s3Config;


    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String uploadPostImage(MultipartFile file) {
        // 파일명 설정 (UUID + 원본 파일명)
        String post_folderName = "post_image/";
        String fileName = post_folderName + UUID.randomUUID();

        // 메타데이터 설정 (파일 크기 및 타입)
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            // S3에 파일 업로드
            s3Config.amazonS3Client().putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

            // 업로드된 이미지의 URL 반환
            return s3Config.amazonS3Client().getUrl(bucketName, fileName).toString();
        } catch (IOException e) {
            throw new RuntimeException("게시글 이미지 업로드 실패", e);
        }
    }
    public String uploadUserImage(MultipartFile file){
        String user_folderName = "user_image/";
        String fileName = user_folderName + UUID.randomUUID();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        try{
            s3Config.amazonS3Client().putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            return s3Config.amazonS3Client().getUrl(bucketName, fileName).toString();
        }catch(IOException e){
            throw new RuntimeException("유저 이미지 업로드 실패", e);
        }
    }
    public void deleteS3Image(String imagePath){
        String folderName = imagePath.split("/")[3];
        String fileName = imagePath.split("/")[4];
        String result = folderName + "/" + fileName;
        try{
            s3Config.amazonS3Client().deleteObject(new DeleteObjectRequest(bucketName, result));
        }catch(AmazonServiceException e){
            log.error("S3 게시글 이미지 삭제 중 오류 발생");
        }catch(SdkClientException e){
            log.error("S3 게시글 이미지 클라이언트 오류 발생");
        }

    }

}
