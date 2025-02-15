package uk.co.arjw.jobrunner.poc.services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.co.arjw.jobrunner.poc.TPDMException;
import uk.co.arjw.jobrunner.poc.model.Job;
import uk.co.arjw.jobrunner.poc.model.TPDMRecord;
import uk.co.arjw.jobrunner.poc.model.Job.JobStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileJobStore implements JobStore {

    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private final DirectoryWatcher dirWatchService;
    private final ObjectMapper serilazer = new ObjectMapper();
    private final Path ROOT_PATH;
    private Map<String, Job> listedJobs = new HashMap<>();

    public FileJobStore(String rootPath) throws Exception {
        this.ROOT_PATH = Paths.get(rootPath);
        ROOT_PATH.toFile().mkdir();
        dirWatchService = new DirectoryWatcher(ROOT_PATH, this);
        dirWatchService.start();
    }

    public void add(Job job) {
        try {
            writeToStore(job);
            listedJobs.put(job.getId(), job);
        } catch (Exception e) {
            throw new TPDMException("job not stored");
        }
    }

    public void lock(Job job) {
        try {

        } catch (Exception e) {
            throw new TPDMException("job not found or updated");
        }
    }

    public void update(Job job) {
        try {
            writeToStore(job);
            listedJobs.put(job.getId(), job);
        } catch (Exception e) {
            throw new TPDMException("job not found or updated");
        }
    }

    public void remove(Job job) {
        try {
            removeFromStore(job);
            listedJobs.remove(job.getId());
        } catch (Exception e) {
            throw new TPDMException("job not stored");
        }
    }

    public List<Job> filter(Predicate<Job> predicate) {
        return listedJobs.values().stream().filter(predicate).collect(Collectors.toList());
    }

    public List<Job> filterByStatus(JobStatus status) {
        return listedJobs.values().stream().filter(job -> job.getStatus() == status).collect(Collectors.toList());
    }

    public Job filterById(String id) {
        return listedJobs.get(id);
    }

    public List<Job> getAll() {
        return new ArrayList<>(listedJobs.values());
    }

    public int size() {
        return listedJobs.size();
    }

    @Override
    public void notify(String eventName, Path path) {
        try {
            if(path.toFile().isDirectory()) {
                DirectoryStream<Path> dirPaths = Files.newDirectoryStream(path);
                for (Path file : dirPaths) {
                    if(isFileJobDefinition(file)) {
                        Job job = read(file, Job.class);
                        if(!listedJobs.containsKey(job.getId())) {
                            listedJobs.put(job.getId(), job);
                        }                
                    }
                }
            } else {
                if(isFileJobDefinition(path)) {
                    Job job = read(path, Job.class);
                    listedJobs.put(job.getId(), job);
                }
            }
        } catch (Exception e) {
            LOG.error("failed to load Job from defintion :{}", path.toString(),e);
        }
    }

    private void writeToStore(Job job) throws Exception {

        String path = getPath(job);
        new File(path).mkdir();
        BufferedWriter writer = new BufferedWriter(new FileWriter(path + getDefRecordName(job)));
        String serialized = serilazer.writeValueAsString(job);
        LOG.debug("writing to file {}", serialized);
        writer.write(serialized);
        writer.close();
    }

    private void removeFromStore(Job job) {
        new File(getPath(job) + getDefRecordName(job)).delete();
    }

    private String getPath(Job job) {
        return ROOT_PATH.toString() + File.separator + job.getId() + File.separator;
    }

    private String getDefRecordName(Job job) {
        return job.getId() + "-job";
    }

    public List<TPDMRecord> getJobRecords(Job job) {
        List<TPDMRecord> records = new ArrayList<>();
        Path dirPath = Paths.get(getPath(job));
        // @TODO this is not complete
        try (DirectoryStream<Path> dirPaths = Files.newDirectoryStream(dirPath)) {
            for (Path file : dirPaths) {
                if (!isFileJobDefinition(file)) {
                    records.add(read(file, TPDMRecord.class));
                }
            }
        } catch (Exception e) {
            LOG.error("Error loading records for job {}", job.getId(), e);
        }
        return records;
    }

    private <T> T read(Path file, Class<T> t) throws Exception {
        StringBuilder builder = new StringBuilder();
        Files.lines(Paths.get(file.toString()), StandardCharsets.UTF_8).forEach(builder::append);
        return serilazer.readValue(builder.toString(), t);
    }

    private boolean isFileJobDefinition(Path file) {
        return file.getFileName().toString().contains("-job");
    }

}