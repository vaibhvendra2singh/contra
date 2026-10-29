package com.contra.oms.jira;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.http.HttpMethod.POST;

class JiraRestClientTest {
    private static final String BASE_URL = "https://jira.example.test";

    @Test
    void createsBugWithOrderContextWhenNoOpenDuplicateExists() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo(BASE_URL + "/rest/api/3/search/jql"))
                .andExpect(method(POST))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("order-42")))
                .andRespond(withSuccess("{\"issues\":[]}", MediaType.APPLICATION_JSON));
        server.expect(requestTo(BASE_URL + "/rest/api/3/issue"))
                .andExpect(method(POST))
                .andExpect(content().string(org.hamcrest.Matchers.allOf(
                        org.hamcrest.Matchers.containsString("order-42"),
                        org.hamcrest.Matchers.containsString("PAYMENT_FAILED"),
                        org.hamcrest.Matchers.containsString("auto-created"))))
                .andRespond(withSuccess("{\"key\":\"OM2-999\"}", MediaType.APPLICATION_JSON));

        JiraRestClient jira = client(builder);
        assertEquals("OM2-999", jira.createOperationalBug("order-42", "PAYMENT_FAILED", Instant.EPOCH).orElseThrow());
        server.verify();
    }

    @Test
    void reusesExistingOpenBugInsteadOfCreatingDuplicate() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo(BASE_URL + "/rest/api/3/search/jql"))
                .andExpect(method(POST))
                .andRespond(withSuccess("{\"issues\":[{\"key\":\"OM2-88\"}]}", MediaType.APPLICATION_JSON));

        JiraRestClient jira = client(builder);
        assertEquals("OM2-88", jira.createOperationalBug("order-42", "PAYMENT_FAILED", Instant.EPOCH).orElseThrow());
        server.verify();
    }

    private JiraRestClient client(RestClient.Builder builder) {
        return new JiraRestClient(builder, BASE_URL, "", "", "OM2", "https://site.example.test", "172");
    }
}
