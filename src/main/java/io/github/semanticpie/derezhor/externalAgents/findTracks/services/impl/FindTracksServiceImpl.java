package io.github.semanticpie.derezhor.externalAgents.findTracks.services.impl;

import io.github.semanticpie.derezhor.externalAgents.findTracks.models.TrackDTO;
import io.github.semanticpie.derezhor.externalAgents.findTracks.services.FindTracksService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ostis.api.context.DefaultScContext;
import org.ostis.scmemory.model.element.ScElement;
import org.ostis.scmemory.model.element.edge.EdgeType;
import org.ostis.scmemory.model.element.link.LinkType;
import org.ostis.scmemory.model.element.link.ScLinkString;
import org.ostis.scmemory.model.element.node.NodeType;
import org.ostis.scmemory.model.exception.ScMemoryException;
import org.ostis.scmemory.model.pattern.ScPattern;
import org.ostis.scmemory.websocketmemory.memory.pattern.DefaultWebsocketScPattern;
import org.ostis.scmemory.websocketmemory.memory.pattern.SearchingPatternTriple;
import org.ostis.scmemory.websocketmemory.memory.pattern.element.AliasPatternElement;
import org.ostis.scmemory.websocketmemory.memory.pattern.element.FixedPatternElement;
import org.ostis.scmemory.websocketmemory.memory.pattern.element.TypePatternElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
@Slf4j
public class FindTracksServiceImpl implements FindTracksService {

    private final DefaultScContext context;

    @Override
    public List<TrackDTO> findAll(Integer page, Integer limit) {
        try {
            long time = System.currentTimeMillis();
            var conceptTrack = context.findKeynode("concept_track").orElseThrow();
            var sysIdtf = context.findKeynode("nrel_system_identifier").orElseThrow();
            var mainIdtf = context.findKeynode("nrel_main_idtf").orElseThrow();
            var nrelArtis = context.findKeynode("nrel_artis").orElseThrow();

            ScPattern p = new DefaultWebsocketScPattern();
            // track
            p.addElement(new SearchingPatternTriple(
                    new FixedPatternElement(conceptTrack),
                    new TypePatternElement<>(EdgeType.ACCESS_VAR_POS_PERM, new AliasPatternElement("edge_1")),
                    new TypePatternElement<>(NodeType.VAR, new AliasPatternElement("track"))
            ));

            // hash
            p.addElement(new SearchingPatternTriple(
                    new AliasPatternElement("track"),
                    new TypePatternElement<>(EdgeType.D_COMMON_VAR, new AliasPatternElement("track=>hash")),
                    new TypePatternElement<>(LinkType.LINK_VAR, new AliasPatternElement("hash"))
            ));

            p.addElement(new SearchingPatternTriple(
                    new FixedPatternElement(sysIdtf),
                    new TypePatternElement<>(EdgeType.ACCESS_VAR_POS_PERM, new AliasPatternElement("sys_idtf->(track=>hash)")),
                    new AliasPatternElement("track=>hash")
            ));

            // title
            p.addElement(new SearchingPatternTriple(
                    new AliasPatternElement("track"),
                    new TypePatternElement<>(EdgeType.D_COMMON_VAR, new AliasPatternElement("track=>title")),
                    new TypePatternElement<>(LinkType.LINK_VAR, new AliasPatternElement("title"))
            ));

            p.addElement(new SearchingPatternTriple(
                    new FixedPatternElement(mainIdtf),
                    new TypePatternElement<>(EdgeType.ACCESS_VAR_POS_PERM, new AliasPatternElement("main_idtf->(track=>title)")),
                    new AliasPatternElement("track=>title")
            ));

            // artist
            p.addElement(new SearchingPatternTriple(
                    new AliasPatternElement("track"),
                    new TypePatternElement<>(EdgeType.D_COMMON_VAR, new AliasPatternElement("track=>artist")),
                    new TypePatternElement<>(NodeType.VAR, new AliasPatternElement("artist"))
            ));

            p.addElement(new SearchingPatternTriple(
                    new FixedPatternElement(nrelArtis),
                    new TypePatternElement<>(EdgeType.ACCESS_VAR_POS_PERM, new AliasPatternElement("nrel_artist->(track=>artist)")),
                    new AliasPatternElement("track=>artist")
            ));

            // artist name
            p.addElement(new SearchingPatternTriple(
                    new AliasPatternElement("artist"),
                    new TypePatternElement<>(EdgeType.D_COMMON_VAR, new AliasPatternElement("artist=>name")),
                    new TypePatternElement<>(LinkType.LINK_VAR, new AliasPatternElement("artist_name"))
            ));

            p.addElement(new SearchingPatternTriple(
                    new FixedPatternElement(mainIdtf),
                    new TypePatternElement<>(EdgeType.ACCESS_VAR_POS_PERM, new AliasPatternElement("main_idtf->(artist=>name)")),
                    new AliasPatternElement("artist=>name")
            ));

            List<TrackDTO> result = context.find(p).limit(limit).map(this::toTrack).toList();

            log.info("SEARCH TIME: {}", System.currentTimeMillis() - time );
            return result;
        } catch (ScMemoryException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<TrackDTO> findByName(String name) {
        return List.of();
    }

    private TrackDTO toTrack(Stream<? extends ScElement> pattern) {
        try {
            var searchResult = pattern.toList();
            return TrackDTO.builder()
                    .hash(context.getStringLinkContent((ScLinkString) searchResult.get(5)))
                    .title(context.getStringLinkContent((ScLinkString) searchResult.get(11)))
                    .author(context.getStringLinkContent((ScLinkString)  searchResult.get(23)))
                    .scAddr(searchResult.get(2).getAddress())
                    .build();
        } catch (ScMemoryException | RuntimeException e) {
            return null;
        }
    }
}
