package io.de4l.frostauthorizationservice.thingsController;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class SpecificThingReadTests {

	@Autowired
	private MockMvc mockMvc;

	private final String frostPrefix = "/FROST-Server/v1.0/";
	private final String THING_WITHOUT_PROPERTIES = frostPrefix + "Things(1)";
	private final String THING_WITH_DE4L_PUBLIC = frostPrefix + "Things(2)";
	// with property de4lOwner = "user"
	private final String THING_WITH_DE4L_OWNER = frostPrefix + "Things(3)";
	// with property de4lConsumer = ["user3, user, user2"]
	private final String THING_WITH_DE4L_CONSUMER = frostPrefix + "Things(4)";

	// THINGS WITHOUT PROPERTIES

	@Test
	@WithMockUser(username = "admin", authorities = {"frost_admin"})
	public void getSpecificThing_AdminRoleAndThingNotPublic_OK() throws Exception {
		this.mockMvc.perform(get(THING_WITHOUT_PROPERTIES))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user", authorities = {})
	public void getSpecificThing_NotAdminAndThingNotPublic_OK() throws Exception {
		this.mockMvc.perform(get(THING_WITHOUT_PROPERTIES))
				.andExpect(status().isNotFound());
	}

	@Test
	public void getSpecificThing_NotAuthenticatedAndThingNotPublic_NotFound() throws Exception {
		this.mockMvc.perform(get(THING_WITHOUT_PROPERTIES))
				.andExpect(status().isNotFound());
	}

	// PUBLIC THINGS

	@Test
	@WithMockUser(username = "admin", authorities = {"frost_admin"})
	public void getSpecificThing_AdminRoleAndThingPublic_OK() throws Exception {
		this.mockMvc.perform(get(THING_WITH_DE4L_PUBLIC))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user", authorities = {})
	public void getSpecificThing_NotAdminAndThingPublic_OK() throws Exception {
		this.mockMvc.perform(get(THING_WITH_DE4L_PUBLIC))
				.andExpect(status().isOk());
	}

	@Test
	public void getSpecificThing_NotAuthenticatedAndThingPublic_OK() throws Exception {
		this.mockMvc.perform(get(THING_WITH_DE4L_PUBLIC))
				.andExpect(status().isOk());
	}

	// THINGS WITH OWNER

	@Test
	@WithMockUser(username = "admin", authorities = {"frost_admin"})
	public void getSpecificThing_AdminRoleAndWithThingOwner_OK() throws Exception {
		this.mockMvc.perform(get(THING_WITH_DE4L_OWNER))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user", authorities = {})
	public void getSpecificThing_ThingOwnerAndWithThingOwner_OK() throws Exception {
		this.mockMvc.perform(get(THING_WITH_DE4L_OWNER))
				.andExpect(status().isOk());
	}

	@Test
	public void getSpecificThing_NotAuthenticatedAndWithThingOwner_OK() throws Exception {
		this.mockMvc.perform(get(THING_WITH_DE4L_OWNER))
				.andExpect(status().isNotFound());
	}

	// THINGS WITH CONSUMER

	@Test
	@WithMockUser(username = "admin", authorities = {"frost_admin"})
	public void getSpecificThing_AdminRoleAndWithThingConsumer_OK() throws Exception {
		this.mockMvc.perform(get(THING_WITH_DE4L_CONSUMER))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user", authorities = {})
	public void getSpecificThing_ThingOwnerAndWithThingConsumer_OK() throws Exception {
		this.mockMvc.perform(get(THING_WITH_DE4L_CONSUMER))
				.andExpect(status().isOk());
	}

	@Test
	public void getSpecificThing_NotAuthenticatedAndWithThingConsumer_OK() throws Exception {
		this.mockMvc.perform(get(THING_WITH_DE4L_CONSUMER))
				.andExpect(status().isNotFound());
	}

}
