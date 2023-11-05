package io.github.semanticpie.derezhor.externalAgents.findTracks.models;

import lombok.Data;

@Data
public class TrackDTO {
    private String hash;
    private String title;
    private String author;
    private String genre;
}
