package com.datalinkedai.employee.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.datalinkedai.employee.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class KnowledgeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Knowledge.class);
        Knowledge knowledge1 = new Knowledge();
        knowledge1.setId("id1");
        Knowledge knowledge2 = new Knowledge();
        knowledge2.setId(knowledge1.getId());
        assertThat(knowledge1).isEqualTo(knowledge2);
        knowledge2.setId("id2");
        assertThat(knowledge1).isNotEqualTo(knowledge2);
        knowledge1.setId(null);
        assertThat(knowledge1).isNotEqualTo(knowledge2);
    }
}
