package org.ieee.sa.x1ng.webscripts.auth;import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.xxxxxxxxx.webscripts.bean.PermissionsBean;
import org.xxxxxxxxx.webscripts.util.NodeUtil;
import org.xxxxxxxxx.webscripts.util.WebScriptUtil;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.web.scripts.AbstractWebScript;
import org.alfresco.web.scripts.WebScriptException;
import org.alfresco.web.scripts.WebScriptRequest;
import org.alfresco.web.scripts.WebScriptResponse;


public class PermissionsSvc extends AbstractWebScript {

    private final static Logger LOG = Logger.getLogger(PermissionsSvc.class);

    protected Repository m_repository = null;
    protected ServiceRegistry m_serviceRegistry = null;

    @Override
    public void execute( final WebScriptRequest request, final WebScriptResponse response) throws IOException {
        LOG.debug("Start executeImpl()");

        NodeService nodeService = m_serviceRegistry.getNodeService();
        PermissionService permService = m_serviceRegistry.getPermissionService();
        AuthorityService authService = m_serviceRegistry.getAuthorityService();;
        PersonService personService = m_serviceRegistry.getPersonService();

        ObjectMapper mapper = new ObjectMapper();

        String nodeRefStr = WebScriptUtil.getNodeRef(request);
        String userName = WebScriptUtil.getUserName(request);
        NodeRef nodeRef = new NodeRef(nodeRefStr);

        try {

            PermissionsBean permBean = new PermissionsBean();
            NodeRef personRef = personService.getPerson(userName);
            String firstName = (String) nodeService.getProperty(personRef, ContentModel.PROP_FIRSTNAME);
            String lastName = (String) nodeService.getProperty(personRef, ContentModel.PROP_LASTNAME);
            String ParentNodeRefId = nodeService.getPrimaryParent(nodeRef).getParentRef().toString();

            permBean.setNodeRef(personRef.toString());
            permBean.setUserName(userName);
            permBean.setFirstName(firstName);
            permBean.setLastName(lastName);
            permBean.setDisplayName(authService.getAuthorityDisplayName(userName));
            permBean.setParentNodeRef(ParentNodeRefId);

            userAuthority(permService, authService, userName, nodeRef);

            permBean.setRole(permissionsForUser);

            response.getWriter().write(mapper.writeValueAsString(permBean));
            response.setContentType(MimetypeMap.MIMETYPE_JSON);
            response.setContentEncoding(StandardCharsets.UTF_8.name());

        } catch (Throwable e) {
            String errorMsg = "Unable to retrieve properties for node " + nodeRefStr.toString();
            throw new WebScriptException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMsg, e);
        }

    }

    public static String userAuthority(final PermissionService permService, final AuthorityService authService, final String userName,
            final NodeRef nodeRef) {

        Set<AccessPermission> perms = permService.getAllSetPermissions(nodeRef);
        StringBuilder permissionsForUser = new StringBuilder();
        for (AccessPermission perm : perms) {
            if (hasUserAuthority(authService, userName, perm.getAuthority())) {
                permissionsForUser.append(perm.getAuthority());
                permissionsForUser.append(": ");
                permissionsForUser.append(perm.getPermission());
                permissionsForUser.append(", ");
            }
        }
        return permissionsForUser.toString();
    }

    public static boolean hasUserAuthority(final AuthorityService authService, final String userName, final String authority) {

        Set<String> auth = authService.getAuthoritiesForUser(userName);
        Iterator<String> authorities = auth.iterator();
        while (authorities.hasNext()) {
            if (authorities.next().equalsIgnoreCase(authority)) {
                return true;
            }
        }
        return false;
    }


    public void setRepository(final Repository repository) {
        m_repository = repository;
    }

    public void setServiceRegistry(final ServiceRegistry registry) {
        m_serviceRegistry = registry;
    }
}
