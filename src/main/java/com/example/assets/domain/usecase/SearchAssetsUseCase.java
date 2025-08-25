package com.example.assets.domain.usecase;

import com.example.assets.domain.model.Asset;

import java.time.Instant;
import java.util.List;

/**
 * Use case interface for searching assets.
 * Defines the contract that application services must implement.
 */
public interface SearchAssetsUseCase {

    /**
     * Executes the search for assets based on filters.
     *
     * @param start         optional start date filter
     * @param end           optional end date filter
     * @param filenameRegex optional filename regex filter
     * @param filetype      optional file type filter
     * @param asc           true for ascending sort by uploadDate, false for descending
     * @return list of matching assets
     */
    List<Asset> execute(
            Instant start,
            Instant end,
            String filenameRegex,
            String filetype,
            boolean asc
    );
}
