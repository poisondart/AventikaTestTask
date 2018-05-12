package ru.testtask.aventika.aventicatesttask;

public interface JSONFetcher {
    String getJSON(String query);
    void parseResults(String jsonString);
}
