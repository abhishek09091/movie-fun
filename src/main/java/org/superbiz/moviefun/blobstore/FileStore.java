package org.superbiz.moviefun.blobstore;

import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;


public class FileStore implements BlobStore {

    @Override
    public void put(Blob blob) throws IOException {
        File targetFile = new File(blob.name);
        targetFile.delete();
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();

        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            outputStream.write(IOUtils.toByteArray(blob.inputStream));
        }
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        File coverFile = new File(name);
        if(!coverFile.exists()){
            return Optional.empty();
        }
        return Optional.of(new Blob(name, new FileInputStream(coverFile), new Tika().detect(coverFile)));
    }

    @Override
    public void deleteAll() {

    }
}
