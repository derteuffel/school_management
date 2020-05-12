package com.derteuffel.school.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class Multipart {

    @Value("${file.upload-dir}")
    private String location ;

    public void store(MultipartFile file){

        try {

           if (!(file.isEmpty())) {
               // Get the file and save it somewhere
               byte[] bytes = file.getBytes();
               Path path = Paths.get( location + file.getOriginalFilename());
               Files.write(path, bytes);
           }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
