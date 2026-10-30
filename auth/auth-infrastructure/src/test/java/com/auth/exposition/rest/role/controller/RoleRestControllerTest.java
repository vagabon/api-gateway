package com.auth.exposition.rest.role.controller;

import com.auth.domain.profile.model.Profile;
import com.base.application.CreateUsecase;
import com.base.application.FindByIdUsecase;
import com.base.application.FindByUsecase;
import com.base.application.UpdateUsecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RoleRestControllerTest {

    @Mock
    FindByUsecase<Profile> findByUsecase;
    @Mock
    FindByIdUsecase<Profile> findByIdUsecase;
    @Mock
    CreateUsecase<Profile> createUsecase;
    @Mock
    UpdateUsecase<Profile> updateUsecase;
    @InjectMocks
    RoleRestController controller;

    @Test
    void getters_shouldExposeInjectedUsecases() {
        assertThat(controller.getFindByUsecase()).isSameAs(findByUsecase);
        assertThat(controller.getFindByIdUsecase()).isSameAs(findByIdUsecase);
        assertThat(controller.getCreateUsecase()).isSameAs(createUsecase);
        assertThat(controller.getUpdateUsecase()).isSameAs(updateUsecase);
    }
}