package org.ieee.sa.x1ng.webscripts.node;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.extensions.webscripts.WebScriptException;

import org.alfresco.web.scripts.AbstractWebScript;
import org.alfresco.web.scripts.WebScriptRequest;
import org.alfresco.web.scripts.WebScriptResponse;


public class createFolder extends AbstractWebScript {


    @Override
    public void execute(final WebScriptRequest request, final WebScriptResponse response) throws IOException {

        try {

        }catch (Throwable e) {
            String errorMsg = "Unable to retrieve properties for node ";
            throw new WebScriptException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMsg, e);
        }
    }

}