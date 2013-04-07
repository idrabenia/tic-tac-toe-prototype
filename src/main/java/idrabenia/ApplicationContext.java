package idrabenia;

import akka.actor.ActorRef;
import idrabenia.domain.Game;
import idrabenia.domain.Player;
import org.cometd.bayeux.server.ServerSession;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Ilya Drabenia
 * @since 09.03.13
 */
public class ApplicationContext {
    private final CopyOnWriteArrayList<String> freePlayers = new CopyOnWriteArrayList<String>();
    private final ConcurrentHashMap<String, ActorRef> games = new ConcurrentHashMap<String, ActorRef>();
    private final ConcurrentHashMap<String, ServerSession> sessions = new ConcurrentHashMap<String, ServerSession>();
    private final List<ActorRef> upcomingGames = new CopyOnWriteArrayList<ActorRef>();

    private static final ApplicationContext instance = new ApplicationContext();

    public static ApplicationContext get() {
        return instance;
    }

    public CopyOnWriteArrayList<String> getFreePlayers() {
        return freePlayers;
    }

    public ConcurrentHashMap<String, ActorRef> getGames() {
        return games;
    }

    public ConcurrentHashMap<String, ServerSession> getSessions() {
        return sessions;
    }

    public List<ActorRef> getUpcomingGames() {
        return upcomingGames;
    }
}
