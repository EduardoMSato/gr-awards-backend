package com.outsera.goldenraspberry.service;

import com.outsera.goldenraspberry.dto.ProducerIntervalDTO;
import com.outsera.goldenraspberry.dto.ProducerIntervalResponseDTO;
import com.outsera.goldenraspberry.model.Movie;
import com.outsera.goldenraspberry.repository.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service responsible for calculating producer award intervals.
 * <p>
 * Analyzes Golden Raspberry Awards data to find producers with
 * minimum and maximum intervals between consecutive wins.
 * </p>
 */
@Service
public class ProducerService {

    private static final Logger logger = LoggerFactory.getLogger(ProducerService.class);

    private final MovieRepository movieRepository;

    @Autowired
    public ProducerService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    /**
     * Gets producers with min and max intervals between consecutive wins.
     * <p>
     * Processes data in two passes:
     * 1. Build producer → sorted win years map
     * 2. Calculate intervals and find min/max simultaneously
     * </p>
     *
     * @return Response DTO with min and max interval lists
     */
    public ProducerIntervalResponseDTO getProducerIntervals() {
        logger.debug("Calculating producer intervals");

        List<Movie> winners = movieRepository.findByWinnerTrue();
        logger.debug("Found {} winning movies", winners.size());

        // Pass 1: Build producer → years map (TreeSet keeps years sorted on insert)
        Map<String, TreeSet<Integer>> producerYears = new HashMap<>();
        for (Movie movie : winners) {
            for (String producer : parseProducers(movie.getProducers())) {
                producerYears.computeIfAbsent(producer, k -> new TreeSet<>()).add(movie.getYear());
            }
        }
        logger.debug("Found {} unique producers", producerYears.size());

        // Pass 2: Calculate intervals and track min/max in a single pass
        int minInterval = Integer.MAX_VALUE;
        int maxInterval = Integer.MIN_VALUE;
        List<ProducerIntervalDTO> minIntervals = new ArrayList<>();
        List<ProducerIntervalDTO> maxIntervals = new ArrayList<>();

        for (Map.Entry<String, TreeSet<Integer>> entry : producerYears.entrySet()) {
            TreeSet<Integer> years = entry.getValue();
            if (years.size() < 2) continue;

            String producer = entry.getKey();
            Integer previousWin = null;
            for (Integer year : years) {
                if (previousWin == null) {
                    previousWin = year;
                    continue;
                }
                int followingWin = year;
                int interval = followingWin - previousWin;
                ProducerIntervalDTO dto = new ProducerIntervalDTO(producer, interval, previousWin, followingWin);

                if (interval < minInterval) {
                    minInterval = interval;
                    minIntervals.clear();
                    minIntervals.add(dto);
                } else if (interval == minInterval) {
                    minIntervals.add(dto);
                }

                if (interval > maxInterval) {
                    maxInterval = interval;
                    maxIntervals.clear();
                    maxIntervals.add(dto);
                } else if (interval == maxInterval) {
                    maxIntervals.add(dto);
                }
                previousWin = followingWin;
            }
        }

        logger.debug("Result: min interval={}, max interval={}", minInterval, maxInterval);
        return new ProducerIntervalResponseDTO(minIntervals, maxIntervals);
    }

    /**
     * Parses producer names from a comma/and-separated string.
     *
     * @param producersString Raw producers string from CSV
     * @return List of individual producer names
     */
    private List<String> parseProducers(String producersString) {
        if (producersString == null || producersString.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String[] names = producersString.replace(", ", " and ").split(" and ");
        List<String> producers = new ArrayList<>(names.length);
        for (String name : names) {
            String trimmed = name.trim();
            if (!trimmed.isEmpty()) {
                producers.add(trimmed);
            }
        }
        return producers;
    }
}
