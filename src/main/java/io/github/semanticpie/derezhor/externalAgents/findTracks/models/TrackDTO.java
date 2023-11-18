package io.github.semanticpie.derezhor.externalAgents.findTracks.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrackDTO {
    private Long scAddr;
    private String hash;
    private String title;
    private String author;
    private String genre;
}
