package com.base.infrastructure.utils.query;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class QueryUtilsTest {

    @Test
    void getLike_entourePourcentEtMetEnMajuscules() {
        assertThat(QueryUtils.getLike("john")).isEqualTo("%JOHN%");
    }

    @Test
    void getLike_valeurNull_retourneDeuxPourcents() {
        assertThat(QueryUtils.getLike(null)).isEqualTo("%%");
    }

    @Test
    void getLike_valeurVide_retourneDeuxPourcents() {
        assertThat(QueryUtils.getLike("")).isEqualTo("%%");
    }

    @Test
    void getLike_conserveLesEspacesEtCaracteresSpeciaux() {
        assertThat(QueryUtils.getLike("Jean Dupont-1")).isEqualTo("%JEAN DUPONT-1%");
    }

    @ParameterizedTest
    @CsvSource({ "abc,%ABC%", "ABC,%ABC%", "aBc,%ABC%", "été,%ÉTÉ%" })
    void getLike_normaliseEnMajuscules(String input, String expected) {
        assertThat(QueryUtils.getLike(input)).isEqualTo(expected);
    }
}