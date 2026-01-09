package com.hunesion.gucamole;

import com.hunesion.gucamole.service.AccessPolicyService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.SneakyThrows;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.net.GuacamoleSocket;
import org.apache.guacamole.net.GuacamoleTunnel;
import org.apache.guacamole.net.InetGuacamoleSocket;
import org.apache.guacamole.net.SimpleGuacamoleTunnel;
import org.apache.guacamole.protocol.ConfiguredGuacamoleSocket;
import org.apache.guacamole.protocol.GuacamoleConfiguration;
import org.apache.guacamole.servlet.GuacamoleHTTPTunnelServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
public class GuacamoleController extends GuacamoleHTTPTunnelServlet {

    @Autowired
    private AccessPolicyService accessPolicyService;

    @RequestMapping(path = "/tunnel", method = RequestMethod.OPTIONS)
    public void tunnel(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        super.handleTunnelRequest(request, response);
    }

    private static final Logger logger = LoggerFactory.getLogger(GuacamoleController.class);

    @SneakyThrows
    @Override
    protected GuacamoleTunnel doConnect(HttpServletRequest request) throws GuacamoleException {

        logger.info("Tunnel connection requested!");
        logger.info("Remote address: {}", request.getRemoteAddr());

        // Extract parameters
        String username = extractUsername(request);
        List<String> userRoles = extractUserRoles(request);
        String targetHost = extractTargetHost(request);
        String targetPort = extractTargetPort(request);
        String sshUsername = extractSshUsername(request);
        String password = extractPassword(request);

        // ============ POLICY CHECK ============
        if (!accessPolicyService.checkAccess(username, userRoles, targetHost, targetPort, sshUsername)) {
            logger.warn("Access DENIED for user: {} to: {}:{}", username, targetHost, targetPort);
            throw new AccessDeniedException("Access denied by policy for user: " + username);
        }
        logger.info("Access ALLOWED for user: {}", username);

        // ======================================
        // ... rest of connection logic
        try {
            GuacamoleConfiguration config = new GuacamoleConfiguration();
            config.setProtocol("ssh");
            config.setParameter("hostname", "192.168.0.211");
            config.setParameter("port", "22");
            config.setParameter("username", "narin");
            config.setParameter("password", "asdqwe");

            logger.info("Connecting to guacd at localhost:4822");

            GuacamoleSocket socket = new ConfiguredGuacamoleSocket(
                    new InetGuacamoleSocket("localhost", 4822),
                    config
            );

            logger.info("Connection successful!");
            return new SimpleGuacamoleTunnel(socket);

        } catch (Exception e) {
            logger.error("Connection failed: ", e);
            throw new GuacamoleException("Failed to connect", e);
        }
    }

    /**
     * Extract username from request
     * TODO: Implement based on your authentication mechanism (JWT, Session, etc.)
     */
    private String extractUsername(HttpServletRequest request) {
        // Option 1: From request parameter
        String username = request.getParameter("username");
        if (username != null) return username;
        // Option 2: From header (e.g., JWT token - you'd need to decode it)
        // String token = request.getHeader("Authorization");

        // Option 3: From session
        // return (String) request.getSession().getAttribute("username");
        // Default fallback (replace with your logic)
        return "narin";
    }

    private List<String> extractUserRoles(HttpServletRequest request) {
        // TODO: Extract from JWT token or session
        // Example: parse from header or session attribute
        String rolesParam = request.getParameter("roles");
        if (rolesParam != null) {
            return List.of(rolesParam.split(","));
        }
        return List.of("SSH_USER"); // Default
    }

    private String extractTargetHost(HttpServletRequest request) {
        String host = request.getParameter("hostname");
        return host != null ? host : "192.168.0.211";
    }


    private String extractTargetPort(HttpServletRequest request) {
        String port = request.getParameter("port");
        return port != null ? port : "22";
    }

    private String extractSshUsername(HttpServletRequest request) {
        String sshUser = request.getParameter("sshUsername");
        return sshUser != null ? sshUser : "narin";
    }

    private String extractPassword(HttpServletRequest request) {
        String password = request.getParameter("password");
        return password != null ? password : "asdqwe";
    }
}
