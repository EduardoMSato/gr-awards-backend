package com.outsera.goldenraspberry.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Response DTO wrapping min and max producer intervals.
 * <p>
 * Represents the complete API response format with both
 * minimum and maximum intervals.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProducerIntervalResponseDTO {

    private List<ProducerIntervalDTO> min;
    private List<ProducerIntervalDTO> max;
}
