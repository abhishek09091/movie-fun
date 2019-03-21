package org.superbiz.moviefun.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.superbiz.moviefun.S3Store;
import org.superbiz.moviefun.blobstore.BlobStore;
import org.superbiz.moviefun.blobstore.FileStore;
import org.superbiz.moviefun.blobstore.ServiceCredentials;


@Configuration
public class S3Configuration {

    @Bean
    public BlobStore blobStore(){
        return new FileStore();
    }

    @Bean
    public BlobStore blobStore(ServiceCredentials serviceCredentials, @Value("${vcap.services.photo-storage.credentials.endpoint:#{null}}") String endpoint) {

        String photoStorageAccessKeyId = serviceCredentials.getCredential("photo-storage", "user-provided", "access_key_id");
        String photoStorageSecretKey = serviceCredentials.getCredential("photo-storage", "user-provided", "secret_access_key");
        String photoStorageBucket = serviceCredentials.getCredential("photo-storage", "user-provided", "bucket");

        AWSCredentials credentials = new BasicAWSCredentials(photoStorageAccessKeyId, photoStorageSecretKey);
        AmazonS3Client s3Client = new AmazonS3Client(credentials);

        if (endpoint != null) {
            s3Client.setEndpoint(endpoint);
        }

        return new S3Store(s3Client, photoStorageBucket);
    }

    @Bean
    ServiceCredentials serviceCredentials(@Value("${vcap.services}") String vcapServices) {
        System.out.println(vcapServices);
        return new ServiceCredentials(vcapServices);
    }
}
