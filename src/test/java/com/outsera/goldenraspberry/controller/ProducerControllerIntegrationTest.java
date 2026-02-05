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
     * - Min intervals are less than or equal to max intervals
     * - Data consistency and correctness
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

        // Verify min interval exists and is valid
        assertNotNull(body.getMin());
        assertFalse(body.getMin().isEmpty());

        ProducerIntervalDTO minInterval = body.getMin().get(0);
        assertNotNull(minInterval.getProducer());
        assertFalse(minInterval.getProducer().isEmpty());
        assertTrue(minInterval.getInterval() > 0);
        assertTrue(minInterval.getFollowingWin() > minInterval.getPreviousWin());
        assertEquals(
            minInterval.getInterval(),
            minInterval.getFollowingWin() - minInterval.getPreviousWin()
        );

        // Verify max interval exists and is valid
        assertNotNull(body.getMax());
        assertFalse(body.getMax().isEmpty());

        ProducerIntervalDTO maxInterval = body.getMax().get(0);
        assertNotNull(maxInterval.getProducer());
        assertFalse(maxInterval.getProducer().isEmpty());
        assertTrue(maxInterval.getInterval() > 0);
        assertTrue(maxInterval.getFollowingWin() > maxInterval.getPreviousWin());
        assertEquals(
            maxInterval.getInterval(),
            maxInterval.getFollowingWin() - maxInterval.getPreviousWin()
        );

        // Verify min interval is less than or equal to max interval
        assertTrue(minInterval.getInterval() <= maxInterval.getInterval(),
            "Min interval should be less than or equal to max interval");
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
