package uk.co.arjw.jobrunner.poc.services;
 
import static java.nio.file.StandardWatchEventKinds.*;
 
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


 
public class DirectoryWatcher implements Runnable {
 
    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private final WatchService watcher;
    private final Map<WatchKey, Path> keys;
    private final EventListener listener;
    private boolean shutdown;
    /**
     * Creates a WatchService and registers the given directory
     */
    public DirectoryWatcher(Path dir, EventListener listener)  throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey, Path>();
        this.listener = listener;
        walkAndRegisterDirectories(dir);
    }
 
    /**
     * Register the given directory with the WatchService; This function will be called by FileVisitor
     */
    private void registerDirectory(Path dir) throws IOException 
    {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        keys.put(key, dir);
    }
 
    /**
     * Register the given directory, and all its sub-directories, with the WatchService.
     */
    private void walkAndRegisterDirectories(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                registerDirectory(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
 
    /**
     * Process all events for keys queued to the watcher
     */
    public void run() {
        while (!shutdown) {
 
            // wait for key to be signalled
            WatchKey key;
            try {                
                key = watcher.take();
                LOG.debug("taking key");
            } catch (InterruptedException x) {
                return;
            }
 
            Path dir = keys.get(key);
            if (dir == null) {
                LOG.error("WatchKey not recognized!!");
                continue;
            }
            for (WatchEvent<?> event : key.pollEvents()) {
                @SuppressWarnings("rawtypes")
                WatchEvent.Kind kind = event.kind();
 
                // Context for directory entry event is the file name of entry
                @SuppressWarnings("unchecked")
                Path name = ((WatchEvent<Path>)event).context();
                Path child = dir.resolve(name);
                
                // print out event
                LOG.info("{}: {}", event.kind().name(), child);
                listener.notify(event.kind().name(), child);
                // if directory is created, and watching recursively, then register it and its sub-directories
                if (kind == ENTRY_CREATE) {
                    try {
                        if (Files.isDirectory(child)) {
                            walkAndRegisterDirectories(child);
                        }
                    } catch (IOException x) {
                        // do something useful
                    }
                }
            }
 
            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);
 
                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public void shutdown() {
        this.shutdown = true;
    }

    
}