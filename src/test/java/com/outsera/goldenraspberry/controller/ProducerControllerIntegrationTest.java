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
 * These tests validate that API responses match the expected results
 * derived from the standard movielist.csv file.
 * </p>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProducerControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * Tests that the API returns the exact expected min and max intervals
     * based on the standard movielist.csv file content.
     * <p>
     * Expected results from CSV:
     * - Min interval: Joel Silver with 1 year gap (1990 → 1991)
     * - Max interval: Matthew Vaughn with 13 year gap (2002 → 2015)
     * </p>
     */
    @Test
    void testGetProducerIntervals() {
        ResponseEntity<ProducerIntervalResponseDTO> response = restTemplate.getForEntity(
            "/api/producers/prize-intervals",
            ProducerIntervalResponseDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        ProducerIntervalResponseDTO body = response.getBody();

        // Validate min intervals match CSV-derived expected values
        assertNotNull(body.getMin());
        assertEquals(1, body.getMin().size(), "Expected exactly 1 producer with minimum interval");

        ProducerIntervalDTO minInterval = body.getMin().get(0);
        assertEquals("Joel Silver", minInterval.getProducer());
        assertEquals(1, minInterval.getInterval());
        assertEquals(1990, minInterval.getPreviousWin());
        assertEquals(1991, minInterval.getFollowingWin());

        // Validate max intervals match CSV-derived expected values
        assertNotNull(body.getMax());
        assertEquals(1, body.getMax().size(), "Expected exactly 1 producer with maximum interval");

        ProducerIntervalDTO maxInterval = body.getMax().get(0);
        assertEquals("Matthew Vaughn", maxInterval.getProducer());
        assertEquals(13, maxInterval.getInterval());
        assertEquals(2002, maxInterval.getPreviousWin());
        assertEquals(2015, maxInterval.getFollowingWin());
    }

    /**
     * Tests that all returned intervals have consistent and valid data.
     * Ensures interval = followingWin - previousWin for every entry.
     */
    @Test
    void testResponseStructure() {
        ResponseEntity<ProducerIntervalResponseDTO> response = restTemplate.getForEntity(
            "/api/producers/prize-intervals",
            ProducerIntervalResponseDTO.class
        );

        ProducerIntervalResponseDTO body = response.getBody();
        assertNotNull(body);

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

        // Min interval must be less than or equal to max interval
        assertTrue(
            body.getMin().get(0).getInterval() <= body.getMax().get(0).getInterval(),
            "Min interval should be less than or equal to max interval"
        );
    }
}
