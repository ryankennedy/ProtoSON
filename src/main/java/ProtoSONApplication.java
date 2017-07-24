import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import providers.ProtoSONMessageProvider;
import resources.GreetingsResource;

public class ProtoSONApplication extends Application<ProtoSONConfiguration> {
    public static void main(String[] args) throws Exception {
        new ProtoSONApplication().run(args);
    }

    @Override
    public String getName() {
        return "demonstration-service";
    }

    public void run(ProtoSONConfiguration configuration, Environment environment) throws Exception {
        // Register the ProtoSON message body provider.
        environment.jersey().register(new ProtoSONMessageProvider());

        // Register the Greetings REST resource.
        environment.jersey().register(new GreetingsResource(configuration.getDefaultGreeting()));
    }
}
