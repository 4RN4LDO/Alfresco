package org.ieee.sa.x1ng.webscripts.node;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.ieee.sa.x1ng.webscripts.util.WebScriptUtil;
import org.springframework.extensions.webscripts.WebScriptException;

import com.componize.alfresco.repo.node.NodePathResolver;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.web.scripts.AbstractWebScript;
import org.alfresco.web.scripts.WebScriptRequest;
import org.alfresco.web.scripts.WebScriptResponse;

public class ContentCheckOut extends AbstractWebScript {
    private static Logger LOG = Logger.getLogger(ContentCheckOut.class);

    protected Repository m_repository = null;
    protected ServiceRegistry m_serviceRegistry = null;
    protected NodePathResolver m_nodePathResolver = null;

    @Override
    public void execute(final WebScriptRequest request, final WebScriptResponse response) throws IOException {

        LOG.debug("Start executeImpl()");

        FileFolderService fileService = m_serviceRegistry.getFileFolderService();
        NodeService nodeService = m_serviceRegistry.getNodeService();
        CheckOutCheckInService checkOutCheckInService = m_serviceRegistry.getCheckOutCheckInService();
        ContentService contService = m_serviceRegistry.getContentService();

        ObjectMapper mapper = new ObjectMapper();

        String nodeRefStr = WebScriptUtil.getNodeRef(request);
        NodeRef nodeRef = new NodeRef(nodeRefStr);

        try {
            FileInfo info;
            info = fileService.getFileInfo(nodeRef);
            String contentUrl = "";
            String contentUrl1 = "";
            if (nodeRef != null) {
                NodeRef checkedOutCopy = checkOutCheckInService.checkout(nodeRef);
                ContentWriter writer = contService.getWriter(checkedOutCopy, ContentModel.PROP_CONTENT, false);//fileService.getWriter(checkedOutCopy);
                contentUrl = writer.getContentUrl();
                ContentData contData = (ContentData) nodeService.getProperty(checkedOutCopy, ContentModel.PROP_CONTENT);
                contentUrl1 = contData.getContentUrl();
                ContentReader reader = contService.getReader(nodeRef, ContentModel.PROP_CONTENT);
                File file = new File("C:/Users/Administrator/Downloads/" + info.getName());
                reader.getContent(file);
            }

            response.getWriter().write(mapper.writeValueAsString(contentUrl)+ "\n");
            response.getWriter().write(mapper.writeValueAsString(contentUrl1));
            response.setContentType(MimetypeMap.MIMETYPE_JSON);
            response.setContentEncoding(StandardCharsets.UTF_8.name());
        }catch (Throwable e) {
            String errorMsg = "Unable to retrieve properties for node ";
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
