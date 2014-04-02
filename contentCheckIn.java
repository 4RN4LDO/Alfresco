package org.ieee.sa.x1ng.webscripts.node;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.extensions.webscripts.WebScriptException;

import com.componize.alfresco.repo.node.NodePathResolver;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.version.VersionModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionType;
import org.alfresco.web.scripts.AbstractWebScript;
import org.alfresco.web.scripts.WebScriptRequest;
import org.alfresco.web.scripts.WebScriptResponse;

public class ContentCheckIn extends AbstractWebScript {
    private static Logger LOG = Logger.getLogger(ContentCheckIn.class);

    protected Repository m_repository = null;
    protected ServiceRegistry m_serviceRegistry = null;
    protected NodePathResolver m_nodePathResolver = null;

    @Override
    public void execute(final WebScriptRequest request, final WebScriptResponse response) throws IOException {

        LOG.debug("Start executeImpl()");

        CheckOutCheckInService checkOutCheckInService = m_serviceRegistry.getCheckOutCheckInService();
        ContentService contService = m_serviceRegistry.getContentService();

        ObjectMapper mapper = new ObjectMapper();

        String nodeRefStr = request.getParameter("NodeRef");
        NodeRef nodeRef = new NodeRef(nodeRefStr);

        try {
            NodeRef workingCopy = checkOutCheckInService.getWorkingCopy(nodeRef);
            ContentReader tempWriter = contService.getReader(workingCopy, ContentModel.PROP_CONTENT);
            String contentUrl = tempWriter.getContentUrl();
            Map<String, Serializable> versionProperties3 = new HashMap<String, Serializable>();
            versionProperties3.put(Version.PROP_DESCRIPTION, "description");
            versionProperties3.put(VersionModel.PROP_VERSION_TYPE, VersionType.MAJOR);
            checkOutCheckInService.checkin(workingCopy, versionProperties3, contentUrl);

            response.getWriter().write(mapper.writeValueAsString(nodeRef));
            response.setContentType(MimetypeMap.MIMETYPE_JSON);
            response.setContentEncoding(StandardCharsets.UTF_8.name());
        }catch (Throwable e) {
            String errorMsg = "Unable to check out node ";
            throw new WebScriptException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMsg, e);
        }

    }

    public void setRepository(final Repository repository) {

        m_repository = repository;
    }

    public void setServiceRegistry(final ServiceRegistry registry) {

        m_serviceRegistry = registry;
    }

    public final void setNodePathResolver(final NodePathResolver nodePathResolver) {

        m_nodePathResolver = nodePathResolver;
    }
}
