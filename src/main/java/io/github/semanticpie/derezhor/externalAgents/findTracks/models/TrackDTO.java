package io.github.semanticpie.derezhor.externalAgents.findTracks.models;

import lombok.Data;

@Data
public class TrackDTO {
    private Long scAddr;
    private String hash;
    private String title;
    private String author;
    private String genre;
}
