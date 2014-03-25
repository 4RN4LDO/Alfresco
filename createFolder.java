package org.ieee.sa.x1ng.webscripts.node;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.ieee.sa.x1ng.webscripts.bean.CreateFolderBean;
import org.ieee.sa.x1ng.webscripts.util.WebScriptUtil;
import org.springframework.extensions.webscripts.WebScriptException;

import com.componize.alfresco.repo.node.NodePathResolver;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.web.scripts.AbstractWebScript;
import org.alfresco.web.scripts.WebScriptRequest;
import org.alfresco.web.scripts.WebScriptResponse;

public class CreateFolder extends AbstractWebScript {
    private static Logger LOG = Logger.getLogger(NodePropertySvc.class);

    protected Repository m_repository = null;
    protected ServiceRegistry m_serviceRegistry = null;
    protected NodePathResolver m_nodePathResolver = null;

    @Override
    public void execute(final WebScriptRequest request, final WebScriptResponse response) throws IOException {

        LOG.debug("Start executeImpl()");

        FileFolderService fileService = m_serviceRegistry.getFileFolderService();

        ObjectMapper mapper = new ObjectMapper();

        String nodeRefStr = WebScriptUtil.getNodeRef(request);
        String folderNameStr = WebScriptUtil.getFolderName(request);
        NodeRef nodeRef = new NodeRef(nodeRefStr);

        try {
            CreateFolderBean createBean = createFolder(fileService, folderNameStr, nodeRef);

            response.getWriter().write(mapper.writeValueAsString(createBean));
            response.setContentType(MimetypeMap.MIMETYPE_JSON);
            response.setContentEncoding(StandardCharsets.UTF_8.name());
        } catch (Throwable e) {
            String errorMsg = "Unable to retrieve properties for node ";
            throw new WebScriptException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMsg, e);
        }
    }


    /**
     * @param fileService
     * @param folderNameStr
     * @param nodeRef
     * @return
     * @throws Exception
     */
    public CreateFolderBean createFolder(final FileFolderService fileService, final String folderNameStr, final NodeRef nodeRef) throws Exception {
        CreateFolderBean createBean = new CreateFolderBean();
        try {
            if (folderNameStr != null && !folderNameStr.isEmpty()) {
                fileService.create(nodeRef, folderNameStr, ContentModel.TYPE_FOLDER);
                createBean.setFolderName(folderNameStr + " folder was created");
            }else {
                createBean.setFolderName("Can't create folder: "+ folderNameStr);
            }
        }catch (FileExistsException  exception) {
            String errorMsg = "Folder already exists, unable to create. Try Other name.";
            createBean.setErrorStack(errorMsg);
            //throw new Exception(errorMsg,exception);
        }
        return createBean;
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
