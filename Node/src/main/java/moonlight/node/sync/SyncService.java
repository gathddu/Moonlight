package moonlight.node.sync;

import moonlight.node.service.IpdsClient;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SyncService {
    private final String nodeId;
    private final IpdsClient ipds;
    private final Path syncDirectory;
    private final Path logFile;
    private final ScheduledExecutorService scheduler;

    public SyncService(String nodeId, IpdsClient ipds) {
        this.nodeId = nodeId;
	this.ipds = ipds;
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
        scheduler.scheduleAtFixedRate(this::syncWithLeader, 15, 60, TimeUnit.SECONDS);
        System.out.println("[Sync] Service started (watching: " + syncDirectory + ")");
    }

    public void stop() {
        scheduler.shutdown();
        System.out.println("[Sync] Service stopped");
    }

    private void syncWithLeader() {
    	String[] leaderInfo = ipds.getLeaderInfo();

    	if (leaderInfo == null) {
        	System.out.println("[Sync] No leader available, skipping sync");
        	return;
    	}

	String leaderName = leaderInfo[0];
	String leaderIp = leaderInfo[1];
	
	System.out.println("[Sync] Checking leader... got: " + leaderName + " (" + leaderIp + ")");
    	if (leaderName.equals(nodeId)) {
        	System.out.println("[Sync] This node is leader, serving files");
        	return;
    	}

	// push local changes to leader first
        try {
            ProcessBuilder pushPb = new ProcessBuilder(
                "rsync", "-avz", syncDirectory.toString() + "/", "rsync://" + leaderIp + ":873/shared/"
            );
            pushPb.redirectErrorStream(true);
            Process pushProcess = pushPb.start();

            BufferedReader pushReader = new BufferedReader(new InputStreamReader(pushProcess.getInputStream()));
            String pushLine;
            while ((pushLine = pushReader.readLine()) != null) {
                if (!pushLine.isBlank()) {
                    appendToLog("push: " + pushLine);
                }
            }

            int pushExit = pushProcess.waitFor();
            if (pushExit == 0) {
                appendToLog("Push to " + leaderName + " completed");
            } else {
                appendToLog("Push to " + leaderName + " failed (exit: " + pushExit + ")");
            }
        } catch (Exception e) {
            System.err.println("[Sync] Push error: " + e.getMessage());
        }

    	String source = "rsync://" + leaderIp + ":873/shared/";
    	String dest = syncDirectory.toString() + "/";

    	try {
            ProcessBuilder pb = new ProcessBuilder("rsync", "-avz", "--delete", source, dest);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
               if (!line.isBlank()) {
                   appendToLog("rsync: " + line);
               }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                appendToLog("Sync from " + leaderName + " completed");
            } else {
                appendToLog("Sync from " + leaderName + " failed (exit: " + exitCode + ")");
            }
        } catch (Exception e) {
            System.err.println("[Sync] Error: " + e.getMessage());
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
