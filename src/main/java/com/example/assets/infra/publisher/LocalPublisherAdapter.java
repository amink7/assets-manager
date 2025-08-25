package com.example.assets.infra.publisher;

import com.example.assets.domain.ports.AssetPublisherPort;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Local implementation of AssetPublisherPort that writes files
 * to the local "uploads" directory.
 */
@Component
public class LocalPublisherAdapter implements AssetPublisherPort {

    private final Path root = Paths.get(System.getProperty("user.dir"), "uploads");

    public LocalPublisherAdapter() throws IOException {
        Files.createDirectories(root);
    }

    @Override
    public PublishedInfo publish(byte[] data, String filename, String contentType) {
        try {
            String ext = filename.contains(".")
                    ? filename.substring(filename.lastIndexOf('.'))
                    : "";

            Path path = root.resolve(UUID.randomUUID() + ext);
            Files.write(path, data);

            return new PublishedInfo(path.toUri().toString(), data.length);
        } catch (IOException e) {
            throw new RuntimeException("Error writing file to local storage", e);
        }
    }
}
