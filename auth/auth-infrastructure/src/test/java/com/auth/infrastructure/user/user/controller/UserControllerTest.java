package com.auth.infrastructure.user.user.controller;

import java.util.Arrays;
import java.util.List;

import com.auth.domain.profile.model.Profile;
import com.auth.domain.user.user.model.User;
import com.auth.infrastructure.TestApplication;
import com.auth.infrastructure.TestSecurityConfig;
import com.auth.infrastructure.user.user.service.UserService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = { TestApplication.class })
@Import(TestSecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void me_returnsUser_whenAuthenticated() throws Exception {
        var user = new User();
        user.setUsername("vagabond");
        var profile = new Profile();
        profile.setName("USER");
        profile.setRoles("USER");
        user.setProfiles(Arrays.asList(profile));
        when(userService.findById(42L)).thenReturn(user);

        mockMvc.perform(get("/v1/users/me")
            .with(jwt().jwt(j -> j.subject("42"))
                .authorities(new SimpleGrantedAuthority("ROLE_USER"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("vagabond"));

        verify(userService).findById(42L);
    }

    @Test
    void me_returns401_whenNoToken() throws Exception {
        mockMvc.perform(get("/v1/users/me"))
            .andExpect(status().isUnauthorized());

        verifyNoInteractions(userService);
    }

    @Test
    void findAll_returns200_withRoleUser() throws Exception {
        when(userService.findAll(0, 500, "id")).thenReturn(List.of(new User()));

        mockMvc.perform(get("/v1/users")
            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void findAll_returns403_withoutRoleUser() throws Exception {
        mockMvc.perform(get("/v1/users")
            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_OTHER"))))
            .andExpect(status().isForbidden());
    }

    @Test
    void findAll_returns401_whenNoToken() throws Exception {
        mockMvc.perform(get("/v1/users"))
            .andExpect(status().isUnauthorized());
    }

}
