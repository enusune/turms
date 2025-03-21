package integration.im.turms.server.common.service.session;

import im.turms.common.constant.DeviceType;
import im.turms.server.common.bo.location.Coordinates;
import im.turms.server.common.bo.session.UserSessionId;
import im.turms.server.common.cluster.node.Node;
import im.turms.server.common.plugin.PluginManager;
import im.turms.server.common.property.TurmsProperties;
import im.turms.server.common.property.TurmsPropertiesManager;
import im.turms.server.common.property.env.common.location.LocationProperties;
import im.turms.server.common.property.env.common.location.UsersNearbyRequestProperties;
import im.turms.server.common.redis.RedisProperties;
import im.turms.server.common.redis.TurmsRedisClientManager;
import im.turms.server.common.redis.codec.context.RedisCodecContextPool;
import im.turms.server.common.service.session.SessionLocationService;
import im.turms.server.common.testing.BaseIntegrationTest;
import io.lettuce.core.GeoCoordinates;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author James Chen
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SessionLocationServiceIT extends BaseIntegrationTest {

    static final SessionLocationService SESSION_LOCATION_SERVICE;

    static final int METERS_PER_LATITUDE_DEGREE = 111_320;

    static final long USER_1_ID = 1;
    static final DeviceType USER_1_DEVICE = DeviceType.ANDROID;
    static final Coordinates USER_1_COORDINATES_1 = new Coordinates(10, 10);
    static final Coordinates USER_1_COORDINATES_2 = new Coordinates(10, 20);

    static final long USER_2_ID = 2;
    static final DeviceType USER_2_DEVICE = DeviceType.ANDROID;
    static final Coordinates USER_2_COORDINATES = new Coordinates(10, 30);

    static final long USER_3_ID = 3;
    static final DeviceType USER_3_DEVICE = DeviceType.ANDROID;
    static final Coordinates USER_3_COORDINATES = new Coordinates(10, 40);

    static final long NONEXISTENT_USER_ID = 99999;
    static final DeviceType NONEXISTENT_USER_DEVICE = DeviceType.ANDROID;

    static {
        Node node = mock(Node.class);
        when(node.getSharedProperties()).thenReturn(new TurmsProperties().toBuilder()
                .location(new LocationProperties().toBuilder()
                        .usersNearbyRequest(new UsersNearbyRequestProperties()
                                .toBuilder()
                                .maxDistanceMeters(Integer.MAX_VALUE)
                                .build())
                        .build())
                .build());

        PluginManager pluginManager = mock(PluginManager.class);
        when(pluginManager.isEnabled()).thenReturn(false);

        TurmsPropertiesManager propertiesManager = mock(TurmsPropertiesManager.class);
        when(propertiesManager.getLocalProperties())
                .thenReturn(new TurmsProperties().toBuilder()
                        .location(new LocationProperties().toBuilder()
                                .enabled(true)
                                .treatUserIdAndDeviceTypeAsUniqueUser(true)
                                .build())
                        .build());

        RedisProperties redisProperties = new RedisProperties()
                .toBuilder()
                .uriList(List.of("redis://%s:%d".formatted(ENV.getRedisHost(), ENV.getRedisPort())))
                .build();
        TurmsRedisClientManager manager =
                new TurmsRedisClientManager(redisProperties, RedisCodecContextPool.GEO_USER_SESSION_ID_CODEC_CONTEXT);
        SESSION_LOCATION_SERVICE = new SessionLocationService(node,
                propertiesManager,
                manager);
    }

    @Order(0)
    @Test
    void upsertUserLocation_shouldInsert_ifNotExists() {
        StepVerifier
                .create(SESSION_LOCATION_SERVICE.upsertUserLocation(USER_1_ID, USER_1_DEVICE, USER_1_COORDINATES_1, new Date()))
                .as("The location of user " + USER_1_ID + " should be inserted")
                .expectComplete()
                .verify(DEFAULT_IO_TIMEOUT);
        StepVerifier
                .create(SESSION_LOCATION_SERVICE.upsertUserLocation(USER_2_ID, USER_2_DEVICE, USER_2_COORDINATES, new Date()))
                .as("The location of user " + USER_2_ID + " should be inserted")
                .expectComplete()
                .verify(DEFAULT_IO_TIMEOUT);
        StepVerifier
                .create(SESSION_LOCATION_SERVICE.upsertUserLocation(USER_3_ID, USER_3_DEVICE, USER_3_COORDINATES, new Date()))
                .as("The location of user " + USER_3_ID + " should be inserted")
                .expectComplete()
                .verify(DEFAULT_IO_TIMEOUT);
    }

    @Order(1)
    @Test
    void upsertUserLocation_shouldUpdate_ifExists() {
        Mono<Void> upsertUserLocation =
                SESSION_LOCATION_SERVICE.upsertUserLocation(USER_1_ID, USER_1_DEVICE, USER_1_COORDINATES_2, new Date());
        StepVerifier
                .create(upsertUserLocation)
                .expectComplete()
                .verify(DEFAULT_IO_TIMEOUT);
    }

    @Order(10)
    @Test
    void getUserLocation_shouldGet_ifExists() {
        Mono<GeoCoordinates> getUserLocation = SESSION_LOCATION_SERVICE.getUserLocation(USER_1_ID, USER_1_DEVICE);
        StepVerifier
                .create(getUserLocation)
                .expectNextMatches(coordinates -> {
                    assertThat(coordinates.getX().intValue()).isEqualTo((int) USER_1_COORDINATES_2.longitude());
                    assertThat(coordinates.getY().intValue()).isEqualTo((int) USER_1_COORDINATES_2.latitude());
                    return true;
                })
                .expectComplete()
                .verify(DEFAULT_IO_TIMEOUT);
    }

    @Order(11)
    @Test
    void getUserLocation_shouldComplete_ifNotExists() {
        Mono<GeoCoordinates> getUserLocation = SESSION_LOCATION_SERVICE.getUserLocation(NONEXISTENT_USER_ID, NONEXISTENT_USER_DEVICE);
        StepVerifier
                .create(getUserLocation)
                .expectComplete()
                .verify(DEFAULT_IO_TIMEOUT);
    }

    @Order(20)
    @Test
    void queryNearbyUsers_shouldGetNearbyUsers() {
        StepVerifier
                .create(SESSION_LOCATION_SERVICE
                        .queryNearbyUsers(USER_1_ID, USER_1_DEVICE, (short) 100, 15 * METERS_PER_LATITUDE_DEGREE, true, true))
                .expectNextMatches(geo -> geo.getMember().equals(new UserSessionId(USER_2_ID, USER_2_DEVICE)))
                .as("Test queryNearestUserSessionIds for user " + USER_1_ID)
                .expectComplete()
                .verify(DEFAULT_IO_TIMEOUT);
        StepVerifier
                .create(SESSION_LOCATION_SERVICE
                        .queryNearbyUsers(USER_2_ID, USER_2_DEVICE, (short) 100, 15 * METERS_PER_LATITUDE_DEGREE, true, true))
                .expectNextMatches(geo -> List.of(new UserSessionId(USER_1_ID, USER_1_DEVICE), new UserSessionId(USER_3_ID, USER_3_DEVICE))
                        .contains(geo.getMember()))
                .expectNextMatches(geo -> List.of(new UserSessionId(USER_1_ID, USER_1_DEVICE), new UserSessionId(USER_3_ID, USER_3_DEVICE))
                        .contains(geo.getMember()))
                .as("Test queryNearestUserSessionIds for user " + USER_2_ID)
                .expectComplete()
                .verify(DEFAULT_IO_TIMEOUT);
        StepVerifier
                .create(SESSION_LOCATION_SERVICE
                        .queryNearbyUsers(NONEXISTENT_USER_ID, NONEXISTENT_USER_DEVICE, (short) 100,
                                15 * METERS_PER_LATITUDE_DEGREE, true, true))
                .as("Test queryNearestUserSessionIds for user " + NONEXISTENT_USER_ID)
                .expectComplete()
                .verify(DEFAULT_IO_TIMEOUT);
    }

    @Order(30)
    @Test
    void removeUserLocation_shouldSucceed_ifExists() {
        StepVerifier
                .create(SESSION_LOCATION_SERVICE
                        .removeUserLocation(USER_1_ID, USER_1_DEVICE))
                .expectComplete()
                .verify(DEFAULT_IO_TIMEOUT);
    }

    @Order(31)
    @Test
    void removeUserLocation_shouldSucceed_ifNotExists() {
        StepVerifier
                .create(SESSION_LOCATION_SERVICE
                        .removeUserLocation(NONEXISTENT_USER_ID, NONEXISTENT_USER_DEVICE))
                .expectComplete()
                .verify(DEFAULT_IO_TIMEOUT);
    }

}