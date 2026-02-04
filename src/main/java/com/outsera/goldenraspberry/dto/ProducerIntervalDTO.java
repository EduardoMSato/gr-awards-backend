package com.outsera.goldenraspberry.dto;

/**
 * Data Transfer Object representing a producer's interval between consecutive awards.
 * <p>
 * Used in the API response to show min/max intervals.
 * </p>
 */
public class ProducerIntervalDTO {

    private String producer;
    private Integer interval;
    private Integer previousWin;
    private Integer followingWin;

    /**
     * Default constructor.
     */
    public ProducerIntervalDTO() {
    }

    /**
     * Constructor with all fields.
     *
     * @param producer      Producer name
     * @param interval      Years between wins
     * @param previousWin   Year of earlier win
     * @param followingWin  Year of later win
     */
    public ProducerIntervalDTO(String producer, Integer interval, Integer previousWin, Integer followingWin) {
        this.producer = producer;
        this.interval = interval;
        this.previousWin = previousWin;
        this.followingWin = followingWin;
    }

    // Getters and Setters

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public Integer getPreviousWin() {
        return previousWin;
    }

    public void setPreviousWin(Integer previousWin) {
        this.previousWin = previousWin;
    }

    public Integer getFollowingWin() {
        return followingWin;
    }

    public void setFollowingWin(Integer followingWin) {
        this.followingWin = followingWin;
    }
}
