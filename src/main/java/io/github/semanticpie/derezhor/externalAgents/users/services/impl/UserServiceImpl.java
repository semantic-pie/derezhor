package io.github.semanticpie.derezhor.externalAgents.users.services.impl;

import io.github.semanticpie.derezhor.externalAgents.users.models.ScUser;
import io.github.semanticpie.derezhor.externalAgents.users.models.enums.UserRole;
import io.github.semanticpie.derezhor.externalAgents.users.services.UserService;
import io.github.semanticpie.derezhor.externalAgents.users.services.encrypt.PasswordEncryptor;
import io.github.semanticpie.derezhor.externalAgents.users.services.exceptions.UserAlreadyExistsException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.ostis.api.context.DefaultScContext;
import org.ostis.scmemory.model.element.ScElement;
import org.ostis.scmemory.model.element.edge.EdgeType;
import org.ostis.scmemory.model.element.edge.ScEdge;
import org.ostis.scmemory.model.element.link.LinkType;
import org.ostis.scmemory.model.element.link.ScLinkString;
import org.ostis.scmemory.model.element.node.NodeType;
import org.ostis.scmemory.model.element.node.ScNode;
import org.ostis.scmemory.model.exception.ScMemoryException;
import org.ostis.scmemory.model.pattern.ScPattern;
import org.ostis.scmemory.websocketmemory.memory.pattern.DefaultWebsocketScPattern;
import org.ostis.scmemory.websocketmemory.memory.pattern.SearchingPatternTriple;
import org.ostis.scmemory.websocketmemory.memory.pattern.element.AliasPatternElement;
import org.ostis.scmemory.websocketmemory.memory.pattern.element.FixedPatternElement;
import org.ostis.scmemory.websocketmemory.memory.pattern.element.TypePatternElement;

import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private DefaultScContext context;

    @Override
    public Optional<ScUser> createUser(final String username,
                                       final String password,
                                       final UserRole userRole){

        if (getUsernameScLink(username).isPresent()) {
            var msg = "Username '" + username + "' is already taken";
            log.info(msg);
            throw new UserAlreadyExistsException(msg);
        }

        try {
            UUID uuid = UUID.randomUUID();
            var strUUID = uuid.toString().replace('-', '_');

            // user's role
            ScNode conceptUserRole;
            if (userRole.equals(UserRole.ADMIN)) {
                conceptUserRole = context.resolveKeynode("concept_admin_role", NodeType.CONST_CLASS);
            } else {
                conceptUserRole = context.resolveKeynode("concept_user_role", NodeType.CONST_CLASS);
            }

            // concept_user -> userStruct
            // concept_role -> userStruct
            ScNode conceptUser = context.resolveKeynode("concept_user", NodeType.CONST_CLASS);
            ScNode userStruct = context.resolveKeynode(strUUID, NodeType.CONST_STRUCT);
            context.createEdge(EdgeType.ACCESS_CONST_POS_PERM, conceptUser, userStruct);
            context.createEdge(EdgeType.ACCESS_CONST_POS_PERM, conceptUserRole, userStruct);

            // lang_en -> username
            ScLinkString scLinkUsername = context.createStringLink(LinkType.LINK_CONST, username);
            ScNode langEn = context.findKeynode("lang_en").orElseThrow();
            context.createEdge(EdgeType.ACCESS_CONST_POS_PERM, langEn, scLinkUsername);

            // userStruct => username + nrel_username
            ScNode noRoleUsername = context.resolveKeynode("nrel_username", NodeType.CONST_NO_ROLE);
            ScEdge userStructUsernameEdge = context.createEdge(EdgeType.D_COMMON_CONST, userStruct, scLinkUsername);
            context.createEdge(EdgeType.ACCESS_CONST_POS_PERM, noRoleUsername, userStructUsernameEdge);

            savePassword(userStruct, password);

            log.info("Created user: [{}:{}:{}]", strUUID, username, password);
            return Optional.of(ScUser.builder()
                    .uuid(strUUID)
                    .username(username)
                    .password(password)
                    .userRole(userRole)
                    .build());
        } catch (Exception ex) {
            log.error("Can't create user", ex);
            return Optional.empty();
        }
    }

    private void savePassword(ScNode userStruct, String password) throws ScMemoryException {
        byte[] salt = PasswordEncryptor.generateSalt(16);
        String hashedPassword = Hex.encodeHexString(PasswordEncryptor.encryptPassword(password, salt));
        String saltHex = Hex.encodeHexString(salt);

        ScNode noRolePasswordSalt = context.resolveKeynode("nrel_password_salt", NodeType.CONST_NO_ROLE);
        ScNode noRolePassword = context.resolveKeynode("nrel_password", NodeType.CONST_NO_ROLE);

        // userStruct => scLinkSalt + nrel_password_salt
        ScLinkString scLinkSalt = context.createStringLink(LinkType.LINK_CONST, saltHex);
        ScEdge userStructSaltEdge = context.createEdge(EdgeType.D_COMMON_CONST, userStruct, scLinkSalt);
        context.createEdge(EdgeType.ACCESS_CONST_POS_PERM, noRolePasswordSalt, userStructSaltEdge);

        // userStruct => scLinkHashPassword + nrel_password
        ScLinkString scLinkHashPassword = context.createStringLink(LinkType.LINK_CONST, hashedPassword);
        ScEdge userStructHashPassEdge = context.createEdge(EdgeType.D_COMMON_CONST, userStruct, scLinkHashPassword);
        context.createEdge(EdgeType.ACCESS_CONST_POS_PERM, noRolePassword, userStructHashPassEdge);
    }

    @Override
    public Optional<String> getUserUUID(String username) {
        try {
            Optional<ScLinkString> userScLinkString = getUsernameScLink(username);

            if (userScLinkString.isEmpty()) {
                return Optional.empty();
            }

            ScPattern p = new DefaultWebsocketScPattern();
            var conceptUser = context.findKeynode("concept_user").orElseThrow();
            var sysIdtf = context.findKeynode("nrel_system_identifier").orElseThrow();

            p.addElement(new SearchingPatternTriple(
                    new FixedPatternElement(conceptUser),
                    new TypePatternElement<>(EdgeType.ACCESS_VAR_POS_PERM, new AliasPatternElement("edge_1")),
                    new TypePatternElement<>(NodeType.VAR, new AliasPatternElement("var_user_struct"))
            ));

            p.addElement(new SearchingPatternTriple(
                    new AliasPatternElement("var_user_struct"),
                    new TypePatternElement<>(EdgeType.D_COMMON_VAR, new AliasPatternElement("var_user_struct==>username")),
                    new FixedPatternElement(userScLinkString.get())
            ));

            p.addElement(new SearchingPatternTriple(
                    new AliasPatternElement("var_user_struct"),
                    new TypePatternElement<>(EdgeType.D_COMMON_VAR, new AliasPatternElement("var_user_struct==>uuid_link")),
                    new TypePatternElement<>(LinkType.LINK_VAR, new AliasPatternElement("uuid_link"))
            ));

            p.addElement(new SearchingPatternTriple(
                    new FixedPatternElement(sysIdtf),
                    new TypePatternElement<>(EdgeType.ACCESS_VAR_POS_PERM, new AliasPatternElement("edge_2")),
                    new AliasPatternElement("var_user_struct==>uuid_link")
            ));

            Stream<Stream<? extends ScElement>> result = context.find(p);

            return result
                    .flatMap(innerStream -> innerStream.filter(link -> link instanceof ScLinkString)
                            .map(link -> (ScLinkString) link))
                    .filter(link -> !link.getContent().equals(username))
                    .findFirst()
                    .map(ScLinkString::getContent);

        } catch (ScMemoryException ex) {
            return Optional.empty();
        }
    }

    private boolean isAnyUserExists() {
        try {
            context
                    .findKeynode("concept_user")
                    .orElseThrow(() -> new ScMemoryException("concept_user is not found in memory"));
            return true;
        } catch (ScMemoryException ex) {
            log.info("No users in memory");
            return false;
        }
    }

    public Optional<ScLinkString> getUsernameScLink(String username) {
        if (!isAnyUserExists()) {
            return Optional.empty();
        }

        try {
            var nrelUsername = context.findKeynode("nrel_username").orElseThrow();
            ScPattern p = new DefaultWebsocketScPattern();

            // see edge alias
            p.addElement(new SearchingPatternTriple(
                    new TypePatternElement<>(NodeType.VAR, new AliasPatternElement("var_user_struct")),
                    new TypePatternElement<>(EdgeType.D_COMMON_VAR, new AliasPatternElement("var_user_struct==>username")),
                    new TypePatternElement<>(LinkType.LINK_VAR, new AliasPatternElement("username"))
            ));

            // link nrel_username with previous edge
            p.addElement(new SearchingPatternTriple(
                    new FixedPatternElement(nrelUsername),
                    new TypePatternElement<>(EdgeType.ACCESS_VAR_POS_PERM, new AliasPatternElement("nrel_username-->(track=>hash)")),
                    new AliasPatternElement("var_user_struct==>username")
            ));

            Stream<Stream<? extends ScElement>> result = context.find(p);

            return result
                    .flatMap(innerStream -> innerStream.filter(link -> link instanceof ScLinkString)
                            .map(link -> (ScLinkString) link)
                            .filter(link -> link.getContent().equals(username)))
                    .findFirst();

        } catch (ScMemoryException ex) {
            return Optional.empty();
        }
    }

}
