package io.github.semanticpie.derezhor.externalAgents.findTracks.services.impl;

import io.github.semanticpie.derezhor.externalAgents.findTracks.models.TrackDTO;
import io.github.semanticpie.derezhor.externalAgents.findTracks.services.FindTracksService;
import lombok.extern.slf4j.Slf4j;
import org.ostis.api.context.DefaultScContext;
import org.ostis.scmemory.model.element.ScElement;
import org.ostis.scmemory.model.element.edge.EdgeType;
import org.ostis.scmemory.model.element.link.LinkType;
import org.ostis.scmemory.model.element.link.ScLinkString;
import org.ostis.scmemory.model.element.node.NodeType;
import org.ostis.scmemory.model.exception.ScMemoryException;
import org.ostis.scmemory.model.pattern.pattern3.ScConstruction3;
import org.ostis.scmemory.model.pattern.pattern3.ScPattern3Impl;
import org.ostis.scmemory.model.pattern.pattern5.ScPattern5Impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class FindTracksServiceImpl implements FindTracksService {

    private final DefaultScContext context;

    @Autowired
    public FindTracksServiceImpl(DefaultScContext context) {
        this.context = context;
    }

    @Override
    public List<TrackDTO> findAll(Integer page, Integer limit) {
        try {
            var conceptTrack = context.findKeynode("concept_track").orElseThrow();
            log.info("1");

            return context.find(new ScPattern3Impl<>(conceptTrack, EdgeType.ACCESS_VAR_POS_PERM, NodeType.VAR))
                    .map(this::toTrack)
                    .filter(Objects::nonNull).toList();
        } catch (ScMemoryException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<TrackDTO> findByName(String name) {
        return null;
    }


    private ScElement findSystemIdtf(ScElement element) {
        try {
            log.info("findSystemIdtf");
            var sysIdtf = context.findKeynode("nrel_system_identifier").orElseThrow();
            return context.find(new ScPattern5Impl<>(
                    element, EdgeType.D_COMMON_VAR, LinkType.LINK_VAR, EdgeType.ACCESS_VAR_POS_PERM, sysIdtf
            )).findFirst().orElseThrow().get3();
        } catch (ScMemoryException | RuntimeException ignored) {
            return null;
        }
    }

    private ScElement findMainIdtf(ScElement element) {
        try {
            log.info("nrel_main_idtf");
            var mainIdtf = context.findKeynode("nrel_main_idtf").orElseThrow();
            return context.find(new ScPattern5Impl<>(
                    element, EdgeType.D_COMMON_VAR, LinkType.LINK_VAR, EdgeType.ACCESS_VAR_POS_PERM, mainIdtf
            )).findFirst().orElseThrow().get3();
        } catch (ScMemoryException | RuntimeException ignored) {
            return null;
        }

    }

    private ScElement findArtist(ScElement element) {
        try {
            log.info("nrel_artis");
            var nrelArtis = context.findKeynode("nrel_artis").orElseThrow();
            return context.find(new ScPattern5Impl<>(
                    element, EdgeType.D_COMMON_VAR, NodeType.VAR, EdgeType.ACCESS_VAR_POS_PERM, nrelArtis
            )).findFirst().orElseThrow().get3();
        } catch (ScMemoryException | RuntimeException ignored) {
            return null;
        }

    }

    private ScElement findGenre(ScElement element) {
        try {
            log.info("nrel_genre");
            var nrelGenre= context.findKeynode("nrel_genre").orElseThrow();
            return context.find(new ScPattern5Impl<>(
                    element, EdgeType.D_COMMON_VAR, NodeType.VAR, EdgeType.ACCESS_VAR_POS_PERM, nrelGenre
            )).findFirst().orElseThrow().get3();
        } catch (ScMemoryException | RuntimeException ignored) {
            return null;
        }

    }

    private TrackDTO toTrack(ScConstruction3 construction3) {
        log.info("toTrack");
        try {
            String hash = context.getStringLinkContent((ScLinkString)  findSystemIdtf(construction3.get3()));
            log.info("hash: {}", hash);

            String title = context.getStringLinkContent((ScLinkString)  findMainIdtf(construction3.get3()));
            log.info("title: {}", title);

            var artist = findArtist(construction3.get3());
            String artistName = context.getStringLinkContent((ScLinkString)  findMainIdtf(artist));

//            var genre = findGenre(construction3.get3());
//            String genreName = context.getStringLinkContent((ScLinkString)  findMainIdtf(genre));

            var track = new TrackDTO();
            track.setHash(hash);
            track.setTitle(title);
            track.setAuthor(artistName);
//            track.setGenre(genreName);
            return track;
        } catch (ScMemoryException | RuntimeException e) {
            return null;
        }
    }
}
