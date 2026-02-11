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
     *
     * @return Response DTO with min and max interval lists
     */
    public ProducerIntervalResponseDTO getProducerIntervals() {
        logger.debug("Calculating producer intervals");

        List<Movie> winners = movieRepository.findByWinnerTrue();
        logger.debug("Found {} winning movies", winners.size());

        Map<String, TreeSet<Integer>> producerYears = buildProducerYearsMap(winners);
        logger.debug("Found {} unique producers", producerYears.size());

        return findMinMaxIntervals(producerYears);
    }

    private Map<String, TreeSet<Integer>> buildProducerYearsMap(List<Movie> winners) {
        Map<String, TreeSet<Integer>> producerYears = new HashMap<>();
        for (Movie movie : winners) {
            for (String producer : parseProducers(movie.getProducers())) {
                producerYears.computeIfAbsent(producer, k -> new TreeSet<>()).add(movie.getYear());
            }
        }
        return producerYears;
    }

    private ProducerIntervalResponseDTO findMinMaxIntervals(Map<String, TreeSet<Integer>> producerYears) {
        int[] minValue = {Integer.MAX_VALUE};
        int[] maxValue = {Integer.MIN_VALUE};
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
                ProducerIntervalDTO dto = new ProducerIntervalDTO(producer, year - previousWin, previousWin, year);

                trackExtreme(dto, minValue, minIntervals, true);
                trackExtreme(dto, maxValue, maxIntervals, false);

                previousWin = year;
            }
        }

        logger.debug("Result: min interval={}, max interval={}", minValue[0], maxValue[0]);
        return new ProducerIntervalResponseDTO(minIntervals, maxIntervals);
    }

    private void trackExtreme(ProducerIntervalDTO dto, int[] currentExtreme,
                              List<ProducerIntervalDTO> extremeList, boolean seekingMin) {
        int interval = dto.getInterval();
        boolean isBetter = seekingMin ? interval < currentExtreme[0] : interval > currentExtreme[0];

        if (isBetter) {
            currentExtreme[0] = interval;
            extremeList.clear();
            extremeList.add(dto);
        } else if (interval == currentExtreme[0]) {
            extremeList.add(dto);
        }
    }

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
