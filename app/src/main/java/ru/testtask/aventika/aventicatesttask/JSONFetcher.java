package ru.testtask.aventika.aventicatesttask;

/*Интерфейс, определяющий методы получения JSON-файла по запросу и преобразованию файла в объект модеои*/

public interface JSONFetcher {
    String getJSON(String query);
    void parseResults(String jsonString);
}
