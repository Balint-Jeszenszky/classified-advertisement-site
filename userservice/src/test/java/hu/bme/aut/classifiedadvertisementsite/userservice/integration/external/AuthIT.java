package hu.bme.aut.classifiedadvertisementsite.userservice.integration.external;

import hu.bme.aut.classifiedadvertisementsite.userservice.api.external.model.RegistrationRequest;
import hu.bme.aut.classifiedadvertisementsite.userservice.api.external.model.VerifyEmailRequest;
import hu.bme.aut.classifiedadvertisementsite.userservice.model.EmailVerification;
import hu.bme.aut.classifiedadvertisementsite.userservice.repository.EmailVerificationRepository;
import hu.bme.aut.classifiedadvertisementsite.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static hu.bme.aut.classifiedadvertisementsite.userservice.integration.util.TestHelper.asJsonString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AuthIT {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailVerificationRepository emailVerificationRepository;

    @BeforeEach
    void init() {
        emailVerificationRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void registerUserSuccessful() throws Exception {
        RegistrationRequest registrationRequest = new RegistrationRequest()
                .username("user")
                .email("email@test.test")
                .password("password")
                .confirmPassword("password");
        RequestBuilder request = MockMvcRequestBuilders.post("/external/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(registrationRequest));

        mockMvc.perform(request)
                .andExpect(status().isCreated());
    }

    @Test
    void emailVerificationSuccessful() throws Exception {
        String email = "email@test.test";
        String username = "user";
        RegistrationRequest registrationRequest = new RegistrationRequest()
                .username(username)
                .email(email)
                .password("password")
                .confirmPassword("password");
        RequestBuilder request = MockMvcRequestBuilders.post("/external/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(registrationRequest));
        mockMvc.perform(request)
                .andExpect(status().isCreated());

        EmailVerification emailVerification = emailVerificationRepository.findAll().get(0);

        request = MockMvcRequestBuilders.post("/external/auth/verifyEmail")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(new VerifyEmailRequest().key(emailVerification.getKey())));

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertEquals(0, emailVerificationRepository.findAll().size());
        assertEquals(email, userRepository.findByUsername(username).get().getEmail());
    }
}
