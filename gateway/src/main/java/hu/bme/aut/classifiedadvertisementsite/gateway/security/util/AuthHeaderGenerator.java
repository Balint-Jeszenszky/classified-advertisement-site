package hu.bme.aut.classifiedadvertisementsite.gateway.security.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import hu.bme.aut.classifiedadvertisementsite.gateway.security.model.User;

public class AuthHeaderGenerator {

    public static String createAuthHeader(User user) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        node.put("id", user.getId());
        node.put("username", user.getUsername());
        node.put("email", user.getEmail());
        ArrayNode arrayNode = mapper.createArrayNode();
        user.getRoles().forEach(arrayNode::add);
        node.set("roles", arrayNode);

        return node.toString();
    }
}
