package com.outsera.goldenraspberry.controller;

import com.outsera.goldenraspberry.dto.ProducerIntervalResponseDTO;
import com.outsera.goldenraspberry.service.ProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for producer award interval endpoints.
 * <p>
 * Provides API endpoints for analyzing Golden Raspberry Awards
 * producer data and calculating award intervals.
 * </p>
 */
@RestController
@RequestMapping("/api/producers")
public class ProducerController {

    private static final Logger logger = LoggerFactory.getLogger(ProducerController.class);

    private final ProducerService producerService;

    @Autowired
    public ProducerController(ProducerService producerService) {
        this.producerService = producerService;
    }

    /**
     * Gets producers with minimum and maximum intervals between consecutive wins.
     * <p>
     * Returns a response containing:
     * - min: List of producers with the shortest interval between wins
     * - max: List of producers with the longest interval between wins
     * </p>
     *
     * @return Response containing min and max producer intervals
     */
    @GetMapping("/prize-intervals")
    public ResponseEntity<ProducerIntervalResponseDTO> getProducerIntervals() {
        logger.debug("GET /api/producers/prize-intervals - Fetching producer intervals");

        ProducerIntervalResponseDTO response = producerService.getProducerIntervals();

        logger.debug("Returning {} min intervals and {} max intervals",
            response.getMin().size(),
            response.getMax().size());

        return ResponseEntity.ok(response);
    }
}
