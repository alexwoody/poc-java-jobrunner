package uk.co.arjw.jobrunner.poc.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

@Builder(toBuilder = true)
@Getter
public class JobProgress {
        private final Logger LOG = LoggerFactory.getLogger(getClass());
        private Job job;
        @Singular
        private List<TPDMRecord> processedRecords;

        public boolean isComplete() {
                return getPercentageComplete() == 100;
        }

        public boolean hasTimedOut() {

                LocalDateTime timeOutAt = job.getCreatedDateTime().plusSeconds(job.getTimeout());
                LOG.info("time out at:{}/n time now at:{} ", DateTimeFormatter.ISO_DATE_TIME.format(timeOutAt),
                                DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now()));
                return timeOutAt.isBefore(LocalDateTime.now());
        }

        public boolean isPartiallyComplete() {
                int percentage = getPercentageComplete();
                return percentage > 0 && percentage < 100;
        }

        public int getPercentageComplete() {
                return calculatePrecentage();
        }

        private int calculatePrecentage() {
                double toProcess = job.getMessage().getRecords().size();
                double processed = processedRecords.stream().distinct().count();
                
                if (toProcess == 0.0)
                        return 0;
                return Double.valueOf(processed / toProcess * 100.00).intValue();
        }
}
