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
import java.util.stream.Collectors;

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
     *
     * @return Response DTO with min and max interval lists
     */
    public ProducerIntervalResponseDTO getProducerIntervals() {
        logger.debug("Calculating producer intervals");

        // Step 1: Load all winning movies
        List<Movie> winners = movieRepository.findByWinnerTrue();
        logger.debug("Found {} winning movies", winners.size());

        // Step 2: Build producer → years map
        Map<String, List<Integer>> producerYears = buildProducerYearsMap(winners);
        logger.debug("Found {} unique producers", producerYears.size());

        // Step 3: Calculate intervals for each producer
        List<ProducerIntervalDTO> allIntervals = calculateAllIntervals(producerYears);
        logger.debug("Calculated {} total intervals", allIntervals.size());

        // Step 4: Find min and max
        List<ProducerIntervalDTO> minIntervals = findMinIntervals(allIntervals);
        List<ProducerIntervalDTO> maxIntervals = findMaxIntervals(allIntervals);

        return new ProducerIntervalResponseDTO(minIntervals, maxIntervals);
    }

    /**
     * Builds a map of producer name to list of win years.
     *
     * @param winners List of winning movies
     * @return Map of producer → years
     */
    private Map<String, List<Integer>> buildProducerYearsMap(List<Movie> winners) {
        Map<String, List<Integer>> producerYears = winners.stream()
            .flatMap(movie -> parseProducers(movie.getProducers()).stream()
                .map(producer -> new AbstractMap.SimpleEntry<>(producer, movie.getYear())))
            .collect(Collectors.groupingBy(
                Map.Entry::getKey,
                Collectors.mapping(Map.Entry::getValue, Collectors.toList())
            ));

        // Sort years for each producer
        producerYears.values().forEach(Collections::sort);

        return producerYears;
    }

    /**
     * Parses producer names from a comma/and-separated string.
     * <p>
     * Examples:
     * - "Producer A" → ["Producer A"]
     * - "Producer A and Producer B" → ["Producer A", "Producer B"]
     * - "Producer A, Producer B and Producer C" → ["Producer A", "Producer B", "Producer C"]
     * </p>
     *
     * @param producersString Raw producers string from CSV
     * @return List of individual producer names
     */
    private List<String> parseProducers(String producersString) {
        if (producersString == null || producersString.trim().isEmpty()) {
            return Collections.emptyList();
        }

        // Replace ", " with " and " to normalize separators
        String normalized = producersString.replace(", ", " and ");

        // Split on " and " and trim each name
        return Arrays.stream(normalized.split(" and "))
            .map(String::trim)
            .filter(name -> !name.isEmpty())
            .collect(Collectors.toList());
    }

    /**
     * Calculates all intervals for all producers with multiple wins.
     *
     * @param producerYears Map of producer → win years
     * @return List of all intervals
     */
    private List<ProducerIntervalDTO> calculateAllIntervals(Map<String, List<Integer>> producerYears) {
        return producerYears.entrySet().stream()
            .filter(entry -> entry.getValue().size() >= 2)
            .flatMap(entry -> calculateIntervalsForProducer(entry.getKey(), entry.getValue()).stream())
            .collect(Collectors.toList());
    }

    /**
     * Calculates intervals for a single producer.
     *
     * @param producer Producer name
     * @param years    Sorted list of win years
     * @return List of intervals for this producer
     */
    private List<ProducerIntervalDTO> calculateIntervalsForProducer(String producer, List<Integer> years) {
        List<ProducerIntervalDTO> intervals = new ArrayList<>();

        for (int i = 0; i < years.size() - 1; i++) {
            int previousWin = years.get(i);
            int followingWin = years.get(i + 1);
            int interval = followingWin - previousWin;

            intervals.add(new ProducerIntervalDTO(producer, interval, previousWin, followingWin));
        }

        return intervals;
    }

    /**
     * Finds all intervals matching the minimum value.
     *
     * @param allIntervals List of all intervals
     * @return List of intervals with minimum value (handles ties)
     */
    private List<ProducerIntervalDTO> findMinIntervals(List<ProducerIntervalDTO> allIntervals) {
        if (allIntervals.isEmpty()) {
            return Collections.emptyList();
        }

        int minInterval = allIntervals.stream()
            .mapToInt(ProducerIntervalDTO::getInterval)
            .min()
            .orElse(0);

        return allIntervals.stream()
            .filter(dto -> dto.getInterval() == minInterval)
            .collect(Collectors.toList());
    }

    /**
     * Finds all intervals matching the maximum value.
     *
     * @param allIntervals List of all intervals
     * @return List of intervals with maximum value (handles ties)
     */
    private List<ProducerIntervalDTO> findMaxIntervals(List<ProducerIntervalDTO> allIntervals) {
        if (allIntervals.isEmpty()) {
            return Collections.emptyList();
        }

        int maxInterval = allIntervals.stream()
            .mapToInt(ProducerIntervalDTO::getInterval)
            .max()
            .orElse(0);

        return allIntervals.stream()
            .filter(dto -> dto.getInterval() == maxInterval)
            .collect(Collectors.toList());
    }
}
