package uk.co.arjw.jobrunner.poc.services;

import java.nio.file.Path;

interface EventListener {
    void notify(String eventName, Path path);
}