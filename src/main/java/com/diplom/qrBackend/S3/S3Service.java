package com.diplom.qrBackend.S3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
public class S3Service {

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String key = "images/" + fileName;
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        amazonS3.putObject(new PutObjectRequest(bucketName, key, file.getInputStream(), metadata));
        return amazonS3.getUrl(bucketName, key).toString();
    }

    public byte[] downloadFile(String fileUrl) throws IOException {
        S3Object s3Object = amazonS3.getObject(bucketName, fileUrl);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        return IOUtils.toByteArray(inputStream);
    }

    public void deleteFile(String fileUrl) {
        amazonS3.deleteObject(bucketName, fileUrl);
    }


}
