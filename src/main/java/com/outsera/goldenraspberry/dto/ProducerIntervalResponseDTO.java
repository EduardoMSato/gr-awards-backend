package com.outsera.goldenraspberry.dto;

import java.util.List;

/**
 * Response DTO wrapping min and max producer intervals.
 * <p>
 * Represents the complete API response format with both
 * minimum and maximum intervals.
 * </p>
 */
public class ProducerIntervalResponseDTO {

    private List<ProducerIntervalDTO> min;
    private List<ProducerIntervalDTO> max;

    /**
     * Default constructor.
     */
    public ProducerIntervalResponseDTO() {
    }

    /**
     * Constructor with both lists.
     *
     * @param min List of producers with minimum interval
     * @param max List of producers with maximum interval
     */
    public ProducerIntervalResponseDTO(List<ProducerIntervalDTO> min, List<ProducerIntervalDTO> max) {
        this.min = min;
        this.max = max;
    }

    // Getters and Setters

    public List<ProducerIntervalDTO> getMin() {
        return min;
    }

    public void setMin(List<ProducerIntervalDTO> min) {
        this.min = min;
    }

    public List<ProducerIntervalDTO> getMax() {
        return max;
    }

    public void setMax(List<ProducerIntervalDTO> max) {
        this.max = max;
    }
}
