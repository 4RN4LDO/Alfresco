package org.ieee.sa.x1ng.webscripts.node;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.ieee.sa.x1ng.webscripts.util.WebScriptUtil;
import org.springframework.extensions.webscripts.WebScriptException;

import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.web.scripts.AbstractWebScript;
import org.alfresco.web.scripts.WebScriptRequest;
import org.alfresco.web.scripts.WebScriptResponse;

public class ChangeProperties extends AbstractWebScript {
    private static Logger LOG = Logger.getLogger(ChangeProperties.class);

    protected ServiceRegistry m_serviceRegistry = null;

    @Override
    public void execute(final WebScriptRequest request, final WebScriptResponse response) throws IOException {
        LOG.debug("Start executeImpl()");

        VersionService versionService = m_serviceRegistry.getVersionService();
        NodeService nodeService = m_serviceRegistry.getNodeService();

        ObjectMapper mapper = new ObjectMapper();

        String nodeRefStr = WebScriptUtil.getNodeRef(request);
        NodeRef nodeRef = new NodeRef(nodeRefStr);

        try {

            response.getWriter().write(mapper.writeValueAsString(nodeRef));
            response.setContentType(MimetypeMap.MIMETYPE_JSON);
            response.setContentEncoding(StandardCharsets.UTF_8.name());
        } catch (Throwable e) {
            String errorMsg = "Unable to retrieve properties for node ";
            throw new WebScriptException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMsg, e);
        }
    }

    public void setServiceRegistry(final ServiceRegistry registry) {

        m_serviceRegistry = registry;
    }

}
