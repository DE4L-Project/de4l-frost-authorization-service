package io.de4l.frostauthorizationservice.thingsController;

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
class ThingCreateTests {

    @Autowired
    private MockMvc mockMvc;

    private final String PATH_THINGS = "/FROST-Server/v1.0/Things";
    private final String DUMMY_THING = "{\"name\":\"test\", \"description\":\"test\"}";

    @Test
    @WithMockUser(username = "admin", authorities = {"frost_admin"})
    void performCreateRequest_Admin_Created() throws Exception {
        mockMvc.perform(post(PATH_THINGS)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(DUMMY_THING)
                .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user")
    void performCreateRequest_User_Unauthorized() throws Exception {
        mockMvc.perform(post(PATH_THINGS)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(DUMMY_THING)
                .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void performCreateRequest_Anonymous_Unauthorized() throws Exception {
        mockMvc.perform(post(PATH_THINGS)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(DUMMY_THING)
                .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}
