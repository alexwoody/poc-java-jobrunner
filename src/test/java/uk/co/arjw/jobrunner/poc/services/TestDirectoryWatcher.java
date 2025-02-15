package uk.co.arjw.jobrunner.poc.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.co.arjw.jobrunner.poc.services.DirectoryWatcher;
import uk.co.arjw.jobrunner.poc.services.EventListener;

public class TestDirectoryWatcher {

    private static final String WATCHED_DIRECTORY = "./target/watched-directory";

    @BeforeEach
    public void init() {
        new File(WATCHED_DIRECTORY).mkdir();
    }

    @Test
    public void testDirectoryWatcherCreate() throws Exception {
        ArrayList<String> answer = new ArrayList<>();
        Path dir = new File(WATCHED_DIRECTORY).toPath();
        DirectoryWatcher watcher = new DirectoryWatcher(dir, new EventListener(){
            @Override
            public void notify(String eventName, Path path) {
                answer.add(eventName);
                assertNotNull(eventName);
                assertEquals(eventName,"ENTRY_CREATE");
            }
        });
        watcher.start();
        Thread.sleep(5000);
        new File(WATCHED_DIRECTORY + "/testCreate").createNewFile();
        Thread.sleep(5000);
        watcher.shutdown();
        new File(WATCHED_DIRECTORY + "/testCreate").delete();
        assertNotNull(answer.get(0));
    }

    @Test
    public void testDirectoryWatcherDelete() throws Exception {
        
        ArrayList<String> answer = new ArrayList<>();
        Path dir = new File(WATCHED_DIRECTORY).toPath();
        new File(WATCHED_DIRECTORY + "/testDelete").createNewFile();
        DirectoryWatcher watcher = new DirectoryWatcher(dir, new EventListener(){
            @Override
            public void notify(String eventName, Path path) {
                answer.add(eventName);
                assertNotNull(eventName);
                assertEquals(eventName,"ENTRY_DELETE");
            }
        });
        watcher.start();
        Thread.sleep(5000);
        new File(WATCHED_DIRECTORY + "/testDelete").delete();
        Thread.sleep(5000);
        watcher.shutdown();
        assertNotNull(answer.get(0));
    }
}
