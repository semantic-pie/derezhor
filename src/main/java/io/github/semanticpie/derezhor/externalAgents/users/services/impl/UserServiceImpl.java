package io.github.semanticpie.derezhor.externalAgents.users.services.impl;

import io.github.semanticpie.derezhor.externalAgents.users.models.UserDTO;
import io.github.semanticpie.derezhor.externalAgents.users.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ostis.api.context.DefaultScContext;
import org.ostis.scmemory.model.element.edge.EdgeType;
import org.ostis.scmemory.model.element.edge.ScEdge;
import org.ostis.scmemory.model.element.link.LinkType;
import org.ostis.scmemory.model.element.link.ScLinkString;
import org.ostis.scmemory.model.element.node.NodeType;
import org.ostis.scmemory.model.element.node.ScNode;
import org.ostis.scmemory.model.exception.ScMemoryException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private DefaultScContext context;
    @Override
    public Optional<UserDTO> createUser(String username) {
        UUID uuid = UUID.randomUUID();
        String strUUID = uuid.toString().replace('-','_');

        try {
            ScNode conceptUser = context.resolveKeynode("concept_user", NodeType.CONST_CLASS);
            ScNode userStruct = context.resolveKeynode(strUUID, NodeType.CONST_STRUCT);
            ScNode noRole = context.resolveKeynode("nrel_username", NodeType.CONST_NO_ROLE);

            ScLinkString userNickname = context.createStringLink(LinkType.LINK, username);

            ScEdge commonEdge = context.createEdge(EdgeType.D_COMMON_CONST, userStruct, userNickname);
            context.createEdge(EdgeType.ACCESS_CONST_POS_PERM, noRole, commonEdge);
            context.createEdge(EdgeType.ACCESS_CONST_POS_PERM, conceptUser, userStruct);

            log.info("User service create user: [{}:{}]", strUUID, username);
            return Optional.of(UserDTO.builder()
                    .uuid(strUUID)
                    .username(username)
                    .build());

        } catch (ScMemoryException e) {
            log.error("Can't create user struct", e);
            return Optional.empty();
        }
    }
}
