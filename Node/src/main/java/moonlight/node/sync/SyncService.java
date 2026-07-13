package moonlight.node.sync;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SyncService {
    private final String nodeId;
    private final Path syncDirectory;
    private final Path logFile;
    private final ScheduledExecutorService scheduler;

    public SyncService(String nodeId) {
        this.nodeId = nodeId;
        this.syncDirectory = Path.of(System.getenv().getOrDefault("SYNC_DIR", "./shared"));
        this.logFile = Path.of(System.getenv().getOrDefault("LOG_DIR", "./logs"), "sync.log");
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        // create directories if they don't exist
        try {
            Files.createDirectories(syncDirectory);
            Files.createDirectories(logFile.getParent());
        } catch (IOException e) {
            System.err.println("[Sync] Failed to create directories: " + e.getMessage());
        }

        // check for changes every 60 seconds
        scheduler.scheduleAtFixedRate(this::checkForChanges, 10, 60, TimeUnit.SECONDS);
        System.out.println("[Sync] Service started (watching: " + syncDirectory + ")");
    }

    public void stop() {
        scheduler.shutdown();
        System.out.println("[Sync] Service stopped");
    }

    private void checkForChanges() {
        try {
            Files.walk(syncDirectory)
                    .filter(Files::isRegularFile)
                    .forEach(this::logFileStatus);
        } catch (IOException e) {
            System.err.println("[Sync] Error scanning files: " + e.getMessage());
        }
    }

    private void logFileStatus(Path file) {
        try {
            long lastModified = Files.getLastModifiedTime(file).toMillis();
            long size = Files.size(file);
            String entry = String.format("%s | %s | %d bytes | %s",
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    syncDirectory.relativize(file),
                    size,
                    lastModified
            );
            appendToLog(entry);
        } catch (IOException e) {
            System.err.println("[Sync] Error reading file: " + file);
        }
    }

    private void appendToLog(String entry) {
        try (BufferedWriter writer = Files.newBufferedWriter(logFile, 
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(entry);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("[Sync] Error writing log: " + e.getMessage());
        }
    }
}
