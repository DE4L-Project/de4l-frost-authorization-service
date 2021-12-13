package io.de4l.frostauthorizationservice.observationsController;

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
class ObservationCreateTests {

    @Autowired
    private MockMvc mockMvc;

    private final String PATH_FROST = "/FROST-Server/v1.0/";
    private final String OBSERVATIONS = "/Observations";
    // associated thing without properties (without de4lPublic -> private)
    private final String OBSERVATION_WITHOUT_PROPERTIES = PATH_FROST + "Datastreams(1)" + OBSERVATIONS;
    // associated thing with property de4lPublic : true
    private final String OBSERVATION_WITH_DE4L_PUBLIC = PATH_FROST + "Datastreams(4)" + OBSERVATIONS;
    // associated thing with property de4lOwner : "user"
    private final String OBSERVATION_WITH_DE4L_OWNER = PATH_FROST + "Datastreams(8)" + OBSERVATIONS;
    // associated thing with property de4lConsumer : ["user3, user, user2"]
    private final String OBSERVATION_WITH_DE4L_CONSUMER = PATH_FROST + "Datastreams(11)" + OBSERVATIONS;

    private final String DUMMY_OBSERVATION = """
            {
              "phenomenonTime": "2021-02-01T18:01:00.000Z",
              "resultTime" : "2021-02-01T18:01:10.000Z",
              "result" : 12.6
            }""";

    // THINGS WITHOUT PROPERTIES

    @Test
    @WithMockUser(username = "admin", authorities = {"frost_admin"})
    void performCreateRequest_AdminAndWithPrivateThing_Created() throws Exception {
        mockMvc.perform(post(OBSERVATION_WITHOUT_PROPERTIES)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(DUMMY_OBSERVATION)
                .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user")
    void performCreateRequest_UserAndWithPrivateThing_NotFound() throws Exception {
        mockMvc.perform(post(OBSERVATION_WITHOUT_PROPERTIES)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(DUMMY_OBSERVATION)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void performCreateRequest_AnonymousAndWithPrivateThing_NotFound() throws Exception {
        mockMvc.perform(post(OBSERVATION_WITHOUT_PROPERTIES)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(DUMMY_OBSERVATION)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    // PUBLIC THINGS

    @Test
    @WithMockUser(username = "admin", authorities = {"frost_admin"})
    void performCreateRequest_AdminAndWithPublicProperty_Created() throws Exception {
        mockMvc.perform(post(OBSERVATION_WITH_DE4L_PUBLIC)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(DUMMY_OBSERVATION)
                .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user")
    void performCreateRequest_UserAndWithPublicProperty_NotFound() throws Exception {
        mockMvc.perform(post(OBSERVATION_WITH_DE4L_PUBLIC)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(DUMMY_OBSERVATION)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void performCreateRequest_AnonymousAndWithPublicProperty_NotFound() throws Exception {
        mockMvc.perform(post(OBSERVATION_WITH_DE4L_PUBLIC)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(DUMMY_OBSERVATION)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    // THINGS WITH OWNER

    @Test
    @WithMockUser(username = "admin", authorities = {"frost_admin"})
    void performCreateRequest_AdminAndWithThingOwnerProperty_Created() throws Exception {
        mockMvc.perform(post(OBSERVATION_WITH_DE4L_OWNER)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(DUMMY_OBSERVATION)
                .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user")
    void performCreateRequest_ThingOwnerAndWithThingOwnerProperty_Created() throws Exception {
        mockMvc.perform(post(OBSERVATION_WITH_DE4L_OWNER)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(DUMMY_OBSERVATION)
                .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    void performCreateRequest_AnonymousAndWithThingOwnerProperty_NotFound() throws Exception {
        mockMvc.perform(post(OBSERVATION_WITH_DE4L_OWNER)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(DUMMY_OBSERVATION)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    // THINGS WITH CONSUMER

    @Test
    @WithMockUser(username = "admin", authorities = {"frost_admin"})
    void performCreateRequest_AdminAndWithThingConsumerPropertyProperty_Created() throws Exception {
        mockMvc.perform(post(OBSERVATION_WITH_DE4L_CONSUMER)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(DUMMY_OBSERVATION)
                .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user")
    void performCreateRequest_ThingConsumerAndWithThingConsumerPropertyProperty_NotFound() throws Exception {
        mockMvc.perform(post(OBSERVATION_WITH_DE4L_CONSUMER)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(DUMMY_OBSERVATION)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void performCreateRequest_AnonymousAndWithThingConsumerPropertyProperty_NotFound() throws Exception {
        mockMvc.perform(post(OBSERVATION_WITH_DE4L_CONSUMER)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(DUMMY_OBSERVATION)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

}
