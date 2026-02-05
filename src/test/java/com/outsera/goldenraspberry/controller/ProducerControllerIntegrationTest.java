package com.outsera.goldenraspberry.controller;

import com.outsera.goldenraspberry.dto.ProducerIntervalDTO;
import com.outsera.goldenraspberry.dto.ProducerIntervalResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for ProducerController endpoint.
 * <p>
 * Tests the complete flow from HTTP request through service layer
 * to database, using the actual CSV data loaded on startup.
 * </p>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProducerControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * Tests the GET /api/producers/prize-intervals endpoint.
     * <p>
     * Verifies:
     * - HTTP 200 OK response
     * - Response body structure (min and max arrays)
     * - Correct minimum interval producer (Joel Silver, 1 year)
     * - Correct maximum interval producer (Matthew Vaughn, 13 years)
     * </p>
     */
    @Test
    void testGetProducerIntervals() {
        // When
        ResponseEntity<ProducerIntervalResponseDTO> response = restTemplate.getForEntity(
            "/api/producers/prize-intervals",
            ProducerIntervalResponseDTO.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        ProducerIntervalResponseDTO body = response.getBody();

        // Verify min interval
        assertNotNull(body.getMin());
        assertFalse(body.getMin().isEmpty());

        ProducerIntervalDTO minInterval = body.getMin().get(0);
        assertEquals("Joel Silver", minInterval.getProducer());
        assertEquals(1, minInterval.getInterval());
        assertEquals(1990, minInterval.getPreviousWin());
        assertEquals(1991, minInterval.getFollowingWin());

        // Verify max interval
        assertNotNull(body.getMax());
        assertFalse(body.getMax().isEmpty());

        ProducerIntervalDTO maxInterval = body.getMax().get(0);
        assertEquals("Matthew Vaughn", maxInterval.getProducer());
        assertEquals(13, maxInterval.getInterval());
        assertEquals(2002, maxInterval.getPreviousWin());
        assertEquals(2015, maxInterval.getFollowingWin());
    }

    /**
     * Tests that the response contains valid data structures.
     * <p>
     * Ensures all fields are populated and intervals are positive.
     * </p>
     */
    @Test
    void testResponseStructure() {
        // When
        ResponseEntity<ProducerIntervalResponseDTO> response = restTemplate.getForEntity(
            "/api/producers/prize-intervals",
            ProducerIntervalResponseDTO.class
        );

        // Then
        ProducerIntervalResponseDTO body = response.getBody();
        assertNotNull(body);

        // Verify all min intervals have valid data
        body.getMin().forEach(interval -> {
            assertNotNull(interval.getProducer());
            assertFalse(interval.getProducer().isEmpty());
            assertTrue(interval.getInterval() > 0);
            assertTrue(interval.getPreviousWin() > 0);
            assertTrue(interval.getFollowingWin() > interval.getPreviousWin());
            assertEquals(
                interval.getInterval(),
                interval.getFollowingWin() - interval.getPreviousWin()
            );
        });

        // Verify all max intervals have valid data
        body.getMax().forEach(interval -> {
            assertNotNull(interval.getProducer());
            assertFalse(interval.getProducer().isEmpty());
            assertTrue(interval.getInterval() > 0);
            assertTrue(interval.getPreviousWin() > 0);
            assertTrue(interval.getFollowingWin() > interval.getPreviousWin());
            assertEquals(
                interval.getInterval(),
                interval.getFollowingWin() - interval.getPreviousWin()
            );
        });
    }
}
