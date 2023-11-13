package io.github.semanticpie.derezhor.externalAgents.findTracks.api;

import io.github.semanticpie.derezhor.externalAgents.findTracks.models.TrackDTO;
import io.github.semanticpie.derezhor.externalAgents.findTracks.services.FindTracksService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/derezhor")
public class TrackController {

    private final FindTracksService findTracksService;

    @CrossOrigin("*")
    @GetMapping("/tracks")
    public List<TrackDTO> findAll(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit) {
        return findTracksService.findAll(page, limit);
    }


    @CrossOrigin("*")
    @GetMapping("/{playlist}")
    public List<TrackDTO> findByPlaylist(@RequestParam("user") String user, @PathVariable("playlist") String playlist) {
        return findTracksService.findByPlaylist(user, playlist);
    }

}
