package uk.co.arjw.jobrunner.poc.model;

import java.util.UUID;

public class ShortId {

    public static String croppedUUID() throws Exception {
        return UUID.randomUUID().toString().replace("-", "").substring(0,10);
    }
}