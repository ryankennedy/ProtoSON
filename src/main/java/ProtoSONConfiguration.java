import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;

public class ProtoSONConfiguration extends Configuration {
    @Valid
    @NotEmpty
    private String defaultGreeting = "Hello, World!";

    /**
     * Gets the default greeting message.
     *
     * @return The default greeting message.
     */
    public String getDefaultGreeting() {
        return defaultGreeting;
    }
}
