package com.datalinkedai.employee.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.datalinkedai.employee.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class KnowledgeCentralTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(KnowledgeCentral.class);
        KnowledgeCentral knowledgeCentral1 = new KnowledgeCentral();
        knowledgeCentral1.setId("id1");
        KnowledgeCentral knowledgeCentral2 = new KnowledgeCentral();
        knowledgeCentral2.setId(knowledgeCentral1.getId());
        assertThat(knowledgeCentral1).isEqualTo(knowledgeCentral2);
        knowledgeCentral2.setId("id2");
        assertThat(knowledgeCentral1).isNotEqualTo(knowledgeCentral2);
        knowledgeCentral1.setId(null);
        assertThat(knowledgeCentral1).isNotEqualTo(knowledgeCentral2);
    }
}
