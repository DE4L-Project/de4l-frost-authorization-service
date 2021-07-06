package io.de4l.frostauthorizationservice.observationsController;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class ObservationsReadTests {

    @Autowired
    private MockMvc mockMvc;

    private final String PATH_OBSERVATIONS = "/FROST-Server/v1.0/Observations";

    @Test
    @WithMockUser(username = "admin", authorities = {"frost_admin"})
    void performReadRequest_Admin_Ok() throws Exception {
        mockMvc.perform(get(PATH_OBSERVATIONS))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user")
    void performReadRequest_User_Ok() throws Exception {
        mockMvc.perform(get(PATH_OBSERVATIONS))
                .andExpect(status().isOk());
    }

    @Test
    void performReadRequest_Anonymous_Ok() throws Exception {
        mockMvc.perform(get(PATH_OBSERVATIONS))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"frost_admin"})
    void removeFilterFromResponse_Anonymous_NextLinkWithoutFilterParameter() throws Exception {
        mockMvc.perform(get(PATH_OBSERVATIONS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.['@iot.nextLink']", endsWith("Observations?$skip=100")));
    }

}
