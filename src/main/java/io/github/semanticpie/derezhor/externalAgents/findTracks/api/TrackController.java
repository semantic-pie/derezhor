package io.github.semanticpie.derezhor.externalAgents.findTracks.api;

import io.github.semanticpie.derezhor.externalAgents.findTracks.models.TrackDTO;
import io.github.semanticpie.derezhor.externalAgents.findTracks.services.FindTracksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/derezhor")
public class TrackController {

    private final FindTracksService findTracksService;

    @Autowired
    public TrackController(FindTracksService findTracksService) {
        this.findTracksService = findTracksService;
    }

    @CrossOrigin("*")
    @GetMapping("/tracks")
    public List<TrackDTO> findAll(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit) {
        return findTracksService.findAll(page, limit);
    }

}
