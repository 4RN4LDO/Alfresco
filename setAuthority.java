
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.xxxxxxxxx.webscripts.bean.SetAuthoBean;
import org.xxxxxxxxx.webscripts.util.NodeUtil;
import org.xxxxxxxxx.webscripts.util.WebScriptUtil;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.web.scripts.AbstractWebScript;
import org.alfresco.web.scripts.WebScriptException;
import org.alfresco.web.scripts.WebScriptRequest;
import org.alfresco.web.scripts.WebScriptResponse;

public class SetAuthority extends AbstractWebScript {

    private final static Logger LOG = Logger.getLogger(SetAuthority.class);

    protected Repository m_repository = null;

    protected ServiceRegistry m_serviceRegistry = null;

    @Override
    public void execute(final WebScriptRequest request, final WebScriptResponse response) throws IOException {

        LOG.debug("Start executeImpl()");

        NodeService nodeService = m_serviceRegistry.getNodeService();
        AuthorityService authService = m_serviceRegistry.getAuthorityService();

        PersonService personService = m_serviceRegistry.getPersonService();

        ObjectMapper mapper = new ObjectMapper();

        String userName = WebScriptUtil.getUserName(request);
        String userAuthority = WebScriptUtil.getAuthorization(request);

        try {

            SetAuthoBean authoBean = new SetAuthoBean();
            NodeRef personRef = personService.getPerson(userName);
            String firstName = (String) nodeService.getProperty(personRef, ContentModel.PROP_FIRSTNAME);
            String lastName = (String) nodeService.getProperty(personRef, ContentModel.PROP_LASTNAME);

            authoBean.setUserName(userName);
            authoBean.setFirstName(firstName);
            authoBean.setLastName(lastName);
            authoBean.setDisplayName(authService.getAuthorityDisplayName(userName));

            setUserAuthority(authService, userName, userAuthority, authoBean);

            response.getWriter().write(mapper.writeValueAsString(authoBean));
            response.setContentType(MimetypeMap.MIMETYPE_JSON);
            response.setContentEncoding(StandardCharsets.UTF_8.name());

        } catch (Throwable e) {
            String errorMsg = "Unable to retrieve properties for node ";
            throw new WebScriptException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMsg, e);
        }

    }

    public void setUserAuthority(final AuthorityService authService, final String userName, final String userAuthority, final SetAuthoBean authoBean) {

        if (NodeUtil.hasUserAuthority(authService, userName, "GROUP_" + userAuthority)) {
            String test = "Already has it";
            authoBean.setAuthority(test);
        } else {
            final String adminGroup = authService.getName(AuthorityType.GROUP, userAuthority);
            authService.addAuthority(adminGroup, userName);
            authoBean.setAuthority(userAuthority);
        }
    }

    public void setRepository(final Repository repository) {

        m_repository = repository;
    }

    public void setServiceRegistry(final ServiceRegistry registry) {

        m_serviceRegistry = registry;
    }

}
