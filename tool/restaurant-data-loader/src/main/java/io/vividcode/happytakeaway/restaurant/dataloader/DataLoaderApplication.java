package io.vividcode.happytakeaway.restaurant.dataloader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import io.vividcode.happytakeaway.restaurant.api.v1.Address;
import io.vividcode.happytakeaway.restaurant.api.v1.web.AssociateMenuItemsWebRequest;
import io.vividcode.happytakeaway.restaurant.api.v1.web.AssociateMenuItemsWebResponse;
import io.vividcode.happytakeaway.restaurant.api.v1.web.CreateMenuItemWebRequest;
import io.vividcode.happytakeaway.restaurant.api.v1.web.CreateMenuWebRequest;
import io.vividcode.happytakeaway.restaurant.api.v1.web.CreateRestaurantWebRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@QuarkusMain
public class DataLoaderApplication implements QuarkusApplication {

  private static final Faker faker = Faker.instance(Locale.CHINA);
  private static final Logger logger = LoggerFactory.getLogger(DataLoaderApplication.class);

  @Inject ObjectMapper objectMapper;

  @Inject @RestClient RestaurantServiceClient restaurantServiceClient;

  @Inject @RestClient FileUploadServiceClient fileUploadServiceClient;

  @Override
  public int run(String... args) throws Exception {
    Path dataPath =
        Paths.get(DataLoaderApplication.class.getResource("/test-data/restaurants").toURI());
    Files.newDirectoryStream(dataPath, entry -> entry.toFile().isDirectory())
        .forEach(
            restaurantPath -> {
              try {
                Restaurant restaurant =
                    this.objectMapper.readValue(
                        restaurantPath.resolve("restaurant.json").toFile(), Restaurant.class);
                String restaurantId = this.createRestaurant(restaurant);
                logger.info("Created restaurant {} with id {}", restaurant.getName(), restaurantId);
                String menuId = this.createMenu(restaurantId);
                logger.info(
                    "Create menu for restaurant {} with id {}", restaurant.getName(), menuId);
                Path menuItemsPath = restaurantPath.resolve("menuitems");
                Set<String> menuItemIds = new HashSet<>();
                Files.newDirectoryStream(menuItemsPath, entry -> entry.toFile().isDirectory())
                    .forEach(
                        menuItemPath -> {
                          try {
                            MenuItem menuItem =
                                this.objectMapper.readValue(
                                    menuItemPath.resolve("menuitem.json").toFile(), MenuItem.class);
                            UploadResult uploadResult =
                                this.fileUploadServiceClient.upload(
                                    FileUploadBody.builder()
                                        .file(
                                            Files.newInputStream(menuItemPath.resolve("cover.jpg")))
                                        .build());
                            String coverImage =
                                uploadResult.getFiles().length > 0
                                    ? uploadResult.getFiles()[0].getPath()
                                    : null;
                            String menuItemId =
                                this.createMenuItem(restaurantId, menuItem, coverImage);
                            logger.info(
                                "Create menu item {} with id {}", menuItem.getName(), menuItemId);
                            menuItemIds.add(menuItemId);
                          } catch (IOException e) {
                            logger.warn("Ignore menu item file {}", menuItemPath.toAbsolutePath());
                          }
                        });
                this.associateMenuItems(restaurantId, menuId, menuItemIds);
              } catch (IOException e) {
                logger.warn("Ignore restaurant file {}", restaurantPath.toAbsolutePath());
              }
            });
    return 0;
  }

  private String createRestaurant(Restaurant restaurant) {
    com.github.javafaker.Address address = faker.address();
    Response response =
        this.restaurantServiceClient.createRestaurant(
            CreateRestaurantWebRequest.builder()
                .name(restaurant.getName())
                .description(restaurant.getDescription())
                .phoneNumber(faker.phoneNumber().phoneNumber())
                .address(
                    Address.builder()
                        .code(faker.number().digits(10))
                        .addressLine(address.fullAddress())
                        .lng(Double.parseDouble(address.longitude()))
                        .lat(Double.parseDouble(address.latitude()))
                        .build())
                .build());
    return this.extractId(response);
  }

  private String createMenu(String restaurantId) {
    Response response =
        this.restaurantServiceClient.createMenu(
            restaurantId, CreateMenuWebRequest.builder().name("default").current(true).build());
    return this.extractId(response);
  }

  private String createMenuItem(String restaurantId, MenuItem menuItem, String coverImage) {
    Response response =
        this.restaurantServiceClient.createMenuItem(
            restaurantId,
            CreateMenuItemWebRequest.builder()
                .name(menuItem.getName())
                .coverImage(coverImage)
                .description(menuItem.getDescription())
                .price(BigDecimal.valueOf(menuItem.getPrice()))
                .build());
    return this.extractId(response);
  }

  private void associateMenuItems(String restaurantId, String menuId, Set<String> menuItemIds) {
    AssociateMenuItemsWebResponse response =
        this.restaurantServiceClient.associateMenuItems(
            restaurantId, menuId, new AssociateMenuItemsWebRequest(menuItemIds));
    logger.info("Associate menu {} with items {}", response.getMenuId(), response.getMenuItems());
  }

  private String extractId(Response response) {
    String location = response.getHeaderString(HttpHeaders.LOCATION);
    return location.substring(location.lastIndexOf("/") + 1);
  }
}
