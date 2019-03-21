package org.superbiz.moviefun;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import org.apache.tika.Tika;
import org.superbiz.moviefun.blobstore.Blob;
import org.superbiz.moviefun.blobstore.BlobStore;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

public class S3Store implements BlobStore {

    private AmazonS3Client s3Client;
    private String photoStorageBucket;


    public S3Store(AmazonS3Client s3Client, String photoStorageBucket) {

        this.s3Client = s3Client;
        this.photoStorageBucket = photoStorageBucket;
    }

    @Override
    public void put(Blob blob) throws IOException {
        s3Client.putObject(photoStorageBucket,blob.name,blob.inputStream,new ObjectMetadata());
    }

    @Override
    public Optional<Blob> get(String name) throws IOException{
        S3Object fullObject = s3Client.getObject(photoStorageBucket,name);

        if(!s3Client.doesObjectExist(photoStorageBucket,name)){
            return Optional.empty();
        }
        S3ObjectInputStream objectContent = fullObject.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectContent);
        return Optional.of(new Blob(name,new ByteArrayInputStream(bytes),new Tika().detect(bytes)));
    }

    @Override
    public void deleteAll() {

    }


    private Blob defaultCoverBlob(){
            return new Blob("default-cover",S3Store.class.getClassLoader().getResourceAsStream("default-cover.jpg"),"image/jpg");
    }

}
