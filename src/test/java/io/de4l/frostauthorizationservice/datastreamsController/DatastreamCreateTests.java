package io.de4l.frostauthorizationservice.datastreamsController;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class DatastreamCreateTests {

    @Autowired
    private MockMvc mockMvc;

    private final String PATH_FROST = "/FROST-Server/v1.0/";
    private final String DATASTREAMS = "/Datastreams";
    // associated thing without properties (without de4lPublic -> private)
    private final String DATASTREAM_WITHOUT_PROPERTIES = PATH_FROST + "Things(1)" + DATASTREAMS;
    // thing with property de4lPublic : true
    private final String DATASTREAM_WITH_DE4L_PUBLIC = PATH_FROST + "Things(2)" + DATASTREAMS;
    // thing with property de4lOwner : "user"
    private final String DATASTREAM_WITH_DE4L_OWNER = PATH_FROST + "Things(3)" + DATASTREAMS;
    // thing with property de4lConsumer : ["user3, user, user2"]
    private final String DATASTREAM_WITH_DE4L_CONSUMER = PATH_FROST + "Things(4)" + DATASTREAMS;

    private final String DUMMY_DATASTREAM = """
            {"name": "Test",
            "description" : "Test",
            "unitOfMeasurement": {},
            "observationType": "Test",
            "Sensor": {"@iot.id": 1},
            "ObservedProperty": {"@iot.id": 1}
            }""";

    // THINGS WITHOUT PROPERTIES

    @Test
    @WithMockUser(username = "admin", authorities = {"frost_admin"})
    void performCreateRequest_AdminAndWithPrivateThing_Created() throws Exception {
        mockMvc.perform(post(DATASTREAM_WITHOUT_PROPERTIES)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(DUMMY_DATASTREAM)
                .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user")
    void performCreateRequest_UserAndWithPrivateThing_NotFound() throws Exception {
        mockMvc.perform(post(DATASTREAM_WITHOUT_PROPERTIES)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(DUMMY_DATASTREAM)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void performCreateRequest_AnonymousAndWithPrivateThing_NotFound() throws Exception {
        mockMvc.perform(post(DATASTREAM_WITHOUT_PROPERTIES)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(DUMMY_DATASTREAM)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    // PUBLIC THINGS

    @Test
    @WithMockUser(username = "admin", authorities = {"frost_admin"})
    void performCreateRequest_AdminAndWithPublicProperty_Created() throws Exception {
        mockMvc.perform(post(DATASTREAM_WITH_DE4L_PUBLIC)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(DUMMY_DATASTREAM)
                .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user")
    void performCreateRequest_UserAndWithPublicProperty_NotFound() throws Exception {
        mockMvc.perform(post(DATASTREAM_WITH_DE4L_PUBLIC)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(DUMMY_DATASTREAM)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void performCreateRequest_AnonymousAndWithPublicProperty_NotFound() throws Exception {
        mockMvc.perform(post(DATASTREAM_WITH_DE4L_PUBLIC)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(DUMMY_DATASTREAM)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    // THINGS WITH OWNER

    @Test
    @WithMockUser(username = "admin", authorities = {"frost_admin"})
    void performCreateRequest_AdminAndWithThingOwnerProperty_Created() throws Exception {
        mockMvc.perform(post(DATASTREAM_WITH_DE4L_OWNER)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(DUMMY_DATASTREAM)
                .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user")
    void performCreateRequest_ThingOwnerAndWithThingOwnerProperty_Created() throws Exception {
        mockMvc.perform(post(DATASTREAM_WITH_DE4L_OWNER)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(DUMMY_DATASTREAM)
                .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    void performCreateRequest_AnonymousAndWithThingOwnerProperty_NotFound() throws Exception {
        mockMvc.perform(post(DATASTREAM_WITH_DE4L_OWNER)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(DUMMY_DATASTREAM)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    // THINGS WITH CONSUMER

    @Test
    @WithMockUser(username = "admin", authorities = {"frost_admin"})
    void performCreateRequest_AdminAndWithThingConsumerPropertyProperty_Created() throws Exception {
        mockMvc.perform(post(DATASTREAM_WITH_DE4L_CONSUMER)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(DUMMY_DATASTREAM)
                .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user")
    void performCreateRequest_ThingConsumerAndWithThingConsumerPropertyProperty_NotFound() throws Exception {
        mockMvc.perform(post(DATASTREAM_WITH_DE4L_CONSUMER)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(DUMMY_DATASTREAM)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void performCreateRequest_AnonymousAndWithThingConsumerPropertyProperty_NotFound() throws Exception {
        mockMvc.perform(post(DATASTREAM_WITH_DE4L_CONSUMER)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(DUMMY_DATASTREAM)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

}
