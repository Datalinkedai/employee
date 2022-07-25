package com.datalinkedai.employee.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.datalinkedai.employee.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TestedTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tested.class);
        Tested tested1 = new Tested();
        tested1.setId("id1");
        Tested tested2 = new Tested();
        tested2.setId(tested1.getId());
        assertThat(tested1).isEqualTo(tested2);
        tested2.setId("id2");
        assertThat(tested1).isNotEqualTo(tested2);
        tested1.setId(null);
        assertThat(tested1).isNotEqualTo(tested2);
    }
}
