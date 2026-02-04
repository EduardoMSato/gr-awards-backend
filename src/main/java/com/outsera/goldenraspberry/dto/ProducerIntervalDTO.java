package com.outsera.goldenraspberry.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object representing a producer's interval between consecutive awards.
 * <p>
 * Used in the API response to show min/max intervals.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProducerIntervalDTO {

    private String producer;
    private Integer interval;
    private Integer previousWin;
    private Integer followingWin;
}
