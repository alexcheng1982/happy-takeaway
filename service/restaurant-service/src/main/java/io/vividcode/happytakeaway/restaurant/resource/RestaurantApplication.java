package io.vividcode.happytakeaway.restaurant.resource;

import javax.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@OpenAPIDefinition(
    tags = {
      @Tag(name = "restaurant", description = "Restaurant"),
      @Tag(name = "menu", description = "Menu"),
      @Tag(name = "menuItem", description = "Menu item"),
      @Tag(name = "order", description = "Order")
    },
    info =
        @Info(
            title = "Restaurant Service API",
            version = "1.0.0",
            contact =
                @Contact(
                    name = "Support",
                    url = "http://example.com/contact",
                    email = "support@example.com"),
            license = @License(name = "MIT License", url = "https://opensource.org/licenses/MIT")))
public class RestaurantApplication extends Application {}
