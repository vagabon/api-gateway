package com.base.infrastructure.controller;

import java.util.List;

import com.base.domain.model.BaseDomain;
import com.base.infrastructure.service.BaseService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BaseControllerTest {

    static class TestDomain extends BaseDomain {
    }

    @RestController
    @RequestMapping("/test")
    static class TestController extends BaseController<TestDomain> {
        TestController(BaseService<TestDomain, ?> service) {
            super(service);
        }
    }

    @Mock
    private BaseService<TestDomain, ?> service;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController(service)).build();
    }

    private TestDomain domain(Long id) {
        TestDomain d = new TestDomain();
        d.setId(id);
        return d;
    }

    @Test
    void ping_retourne200() throws Exception {
        when(service.ping()).thenReturn("ping");

        mockMvc.perform(get("/test/ping")).andExpect(status().isOk())
            .andExpect(content().string("ping"));
    }

    @Test
    void findAll_appellePage0Max500TriParId() throws Exception {
        when(service.findAll(0, 500, "id")).thenReturn(List.of(domain(1L), domain(2L)));

        mockMvc.perform(get("/test")).andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void findById_retourneLeDomaine() throws Exception {
        when(service.findById(1L)).thenReturn(domain(1L));

        mockMvc.perform(get("/test/1")).andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void findById_idNonNumerique_retourne400() throws Exception {
        mockMvc.perform(get("/test/abc")).andExpect(status().isBadRequest());
    }

    @Test
    @SuppressWarnings("unchecked")
    void findBy_decoupeValuesParVirgule() throws Exception {
        when(service.findBy(anyString(), anyList(), any(), any()))
            .thenReturn(List.of(domain(1L)));

        mockMvc
            .perform(get("/test/findBy").param("fields", "name,active")
                .param("values", "john,true")
                .param("first", "5").param("max", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1));

        ArgumentCaptor<List<Object>> captor = ArgumentCaptor.forClass(List.class);
        verify(service).findBy(eq("name,active"), captor.capture(), eq(5), eq(20));
        org.assertj.core.api.Assertions.assertThat(captor.getValue())
            .containsExactly("john", "true");
    }

    @Test
    void findBy_sansFirstNiMax_transmetNull() throws Exception {
        when(service.findBy(anyString(), anyList(), any(), any())).thenReturn(List.of());

        mockMvc
            .perform(get("/test/findBy").param("fields", "name").param("values", "john"))
            .andExpect(status().isOk());

        verify(service).findBy(eq("name"), anyList(), eq(null), eq(null));
    }

    @Test
    void create_retourneLeDomaineCree() throws Exception {
        when(service.create(any(TestDomain.class))).thenReturn(domain(1L));

        mockMvc
            .perform(post("/test").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void create_jsonInvalide_retourne400() throws Exception {
        mockMvc
            .perform(post("/test").contentType(MediaType.APPLICATION_JSON)
                .content("{pas du json"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void update_retourneLeDomaineModifie() throws Exception {
        when(service.update(any(TestDomain.class))).thenReturn(domain(1L));

        mockMvc
            .perform(put("/test").contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":1}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void delete_retourneTrue() throws Exception {
        when(service.delete(1L)).thenReturn(true);

        mockMvc.perform(delete("/test/1")).andExpect(status().isOk())
            .andExpect(content().string("true"));
    }

    @Test
    void desactivate_retourneLeDomaine() throws Exception {
        TestDomain off = domain(1L);
        off.setActive(false);
        when(service.desactivate(1L)).thenReturn(off);

        mockMvc.perform(delete("/test/desactivate/1")).andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }
}