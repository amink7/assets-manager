package com.example.assets.infra.publisher;

import com.example.assets.domain.ports.AssetPublisherPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.*;

class LocalPublisherAdapterTest {

    @Test
    void publish_shouldWriteFileAndReturnInfo(@TempDir Path tempDir) throws Exception {
        String oldUserDir = System.getProperty("user.dir");
        System.setProperty("user.dir", tempDir.toString());
        try {
            LocalPublisherAdapter adapter = new LocalPublisherAdapter();
            byte[] data = "hello".getBytes();

            AssetPublisherPort.PublishedInfo info = adapter.publish(data, "file.txt", "text/plain");

            assertThat(info.size()).isEqualTo(data.length);
            Path stored = Path.of(URI.create(info.url()));
            assertThat(Files.exists(stored)).isTrue();
            assertThat(Files.readAllBytes(stored)).isEqualTo(data);
        } finally {
            System.setProperty("user.dir", oldUserDir);
        }
    }

    @Test
    void publish_shouldThrowRuntimeExceptionWhenWriteFails(@TempDir Path tempDir) throws Exception {
        String oldUserDir = System.getProperty("user.dir");
        System.setProperty("user.dir", tempDir.toString());
        try {
            LocalPublisherAdapter adapter = new LocalPublisherAdapter();
            Path uploads = Paths.get(System.getProperty("user.dir"), "uploads");
            Files.delete(uploads);
            Files.createFile(uploads);
            byte[] data = "hello".getBytes();

            assertThatThrownBy(() -> adapter.publish(data, "file.txt", "text/plain"))
                    .isInstanceOf(RuntimeException.class);
        } finally {
            System.setProperty("user.dir", oldUserDir);
        }
    }
}