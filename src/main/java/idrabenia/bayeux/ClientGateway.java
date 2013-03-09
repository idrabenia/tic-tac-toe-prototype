package idrabenia.bayeux;

import idrabenia.ApplicationContext;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.server.AbstractService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ilya Drabenia
 * @since 09.03.13
 */
public class ClientGateway {
    private final AbstractService service;

    public ClientGateway(AbstractService service) {
        this.service = service;
    }

    public void deliver(String player, String channel, Object... data) {
        Map<Object, Object> output = makeOutput(data);
        deliver(player, channel, output);
    }

    public void deliver(String player, String channel, Map<Object, Object> output) {
        ApplicationContext.get().getSessions().get(player).deliver(service.getServerSession(), channel, output, null);
    }

    public void deliver(List<String> players, String channel, Object... data) {
        for (String curPlayer : players) {
            deliver(curPlayer, channel, data);
        }
    }

    public Map<Object, Object> makeOutput(Object... data) {
        Map<Object, Object> output = new HashMap<Object, Object>();

        for (int i = 0; i < data.length; i += 2) {
            output.put(data[i], data[i + 1]);
        }

        return output;
    }

}
