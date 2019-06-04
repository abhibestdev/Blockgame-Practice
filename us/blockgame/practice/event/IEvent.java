package us.blockgame.practice.event;

import java.util.ArrayList;
import java.util.UUID;

public interface IEvent {

    void start();
    void stop(String winnerName);
    EventType getEventType();
    boolean hasStarted();
    ArrayList<UUID> getMembers();
    ArrayList<UUID> getSpectators();
    ArrayList<UUID> getAllPlayers();
    void setStarted(boolean started);
}
