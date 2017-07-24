package resources;

import protoson.messages.Greeting;
import providers.ProtoSONMessageProvider;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Path("/greetings/{visitor}")
@Consumes({MediaType.APPLICATION_JSON, ProtoSONMessageProvider.APPLICATION_PROTOBUF})
@Produces({MediaType.APPLICATION_JSON, ProtoSONMessageProvider.APPLICATION_PROTOBUF})

public class GreetingsResource {
    private final Map<String, String> greetings;
    private final String defaultGreeting;

    /**
     * Constructs a new greetings REST resource.
     *
     * @param defaultGreeting The greeting to return for any visitor without an explicitly set greeting.
     */
    public GreetingsResource(String defaultGreeting) {
        this.defaultGreeting = defaultGreeting;
        greetings = new ConcurrentHashMap<>();
    }

    /**
     * Returns the greeting for the given visitor. Returns a configured default greeting if no greeting
     * has yet been set for the visitor.
     *
     * @param visitor The visitor of the greeting to fetch.
     * @return The greeting for the given visitor or a default greeting if no specific greeting is set.
     */
    @GET
    public Greeting getGreeting(@PathParam("visitor") final String visitor) {
        final String greeting = greetings.getOrDefault(visitor, defaultGreeting);
        return Greeting.newBuilder().setGreeting(greeting).build();
    }

    /**
     * Updates the greeting for a particular visitor.
     *
     * @param visitor The visitor of the greeting to update.
     * @param greeting The new greeting for the given visitor.
     * @return The newly updated greeting for the given visitor.
     */
    @PUT
    public Greeting updateGreeting(@PathParam("visitor") final String visitor,
                                   final Greeting greeting) {
        greetings.put(visitor, greeting.getGreeting());
        return greeting;
    }
}
