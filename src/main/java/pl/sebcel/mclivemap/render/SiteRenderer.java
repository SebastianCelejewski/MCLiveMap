package pl.sebcel.mclivemap.render;

import java.io.StringWriter;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import pl.sebcel.mclivemap.domain.PlayerData;

public class SiteRenderer {

    public String renderSite(List<PlayerData> playerData, String templateName) {
        try {
            VelocityEngine velocityEngine = new VelocityEngine();
            velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
            velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
            Template template = velocityEngine.getTemplate(templateName);

            VelocityContext context = new VelocityContext();
            context.put("players", playerData);
            StringWriter out = new StringWriter();
            template.merge(context, out);

            return out.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to render site: " + ex.getMessage(), ex);
        }
    }

}