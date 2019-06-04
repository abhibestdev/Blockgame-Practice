package us.blockgame.practice.managers;

import us.blockgame.practice.event.Event;
import us.blockgame.practice.manager.Manager;
import us.blockgame.practice.manager.ManagerHandler;

public class EventManager extends Manager {

    private Event currentEvent;

    public EventManager(ManagerHandler managerHandler) {
        super(managerHandler);
    }

    public Event getCurrentEvent() {
        return currentEvent;
    }

    public boolean currentEvent() {
        return currentEvent != null;
    }

    public void setCurrentEvent(Event currentEvent) {
        this.currentEvent = currentEvent;
    }
}
