package com.example.mike.birdalarm;

interface Subject {

    void registerObserver(AlarmObserver observer);

    void removeObserver(AlarmObserver observer);

    void notifyObservers();

}
