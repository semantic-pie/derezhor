package io.github.semanticpie.derezhor.externalAgents.findTracks.api;

import io.github.semanticpie.derezhor.externalAgents.findTracks.models.TrackDTO;
import io.github.semanticpie.derezhor.externalAgents.findTracks.services.FindTracksService;
import io.github.semanticpie.derezhor.externalAgents.findTracks.services.LikeService;
import io.github.semanticpie.derezhor.externalAgents.users.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/derezhor/tracks")
public class TrackController {

    private final FindTracksService findTracksService;
    private final LikeService likeService;

    @CrossOrigin("*")
    @GetMapping()
    public List<TrackDTO> findAll(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit) {
        return findTracksService.findAll(page, limit);
    }

    @CrossOrigin("*")
    @PatchMapping("/{hash}/like")
    public ResponseEntity<?> likeTrack(@PathVariable String hash, HttpServletRequest request) {
        return likeService.likeTrack(hash, request);
    }

}
