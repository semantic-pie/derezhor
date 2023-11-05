package io.github.semanticpie.derezhor.externalAgents.findTracks.services;

import io.github.semanticpie.derezhor.externalAgents.findTracks.models.TrackDTO;

import java.util.List;

public interface FindTracksService {
    List<TrackDTO> findAll(Integer page, Integer limit);
    List<TrackDTO> findByName(String name);
}
