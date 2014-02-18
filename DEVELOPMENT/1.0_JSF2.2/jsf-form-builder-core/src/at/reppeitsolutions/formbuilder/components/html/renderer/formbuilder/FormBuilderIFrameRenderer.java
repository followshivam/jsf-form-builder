/* 
 * Copyright (C) 2014 Mathias Reppe <mathias.reppe@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.reppeitsolutions.formbuilder.components.html.renderer.formbuilder;

import at.reppeitsolutions.formbuilder.components.Constants;
import at.reppeitsolutions.formbuilder.components.FormBuilderIFrame;
import at.reppeitsolutions.formbuilder.components.ModelApplicationBean;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Mathias Reppe <mathias.reppe@gmail.com>
 */
@FacesRenderer(componentFamily = FormBuilderIFrameRenderer.FAMILY, rendererType = FormBuilderIFrameRenderer.RENDERTYPE)
public class FormBuilderIFrameRenderer extends Renderer {

    public static final String RENDERTYPE = "FormBuilderIframeRenderer";
    public static final String FAMILY = "at.rits.formbuilder";

    public FormBuilderIFrameRenderer() {
    }

    @Override
    public void encodeBegin(FacesContext ctx,
            UIComponent component) throws IOException {
        FormBuilderIFrame formBuilderIFrame = (FormBuilderIFrame) component;
        String uuid = UUID.randomUUID().toString();
        ModelApplicationBean.getInstance().putModel(uuid, formBuilderIFrame.getModel());
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        formBuilderIFrame.getIFrame().setSrc(request.getContextPath() + "/pages/formbuilder.xhtml?uuid=" + uuid);
        
        formBuilderIFrame.getCallbackButton().setAction(formBuilderIFrame.getAction());
        FormBuilderRenderer.getHtmlForm(component).getChildren().add(formBuilderIFrame.getCallbackButton());
    }

    @Override
    public void encodeEnd(FacesContext ctx,
            UIComponent component) throws IOException {
        FormBuilderIFrame formBuilderIFrame = (FormBuilderIFrame) component;
        ResponseWriter writer = ctx.getResponseWriter();
        writer.write("<script type=\"text/javascript\">"
                + "function buffer() {"
                + "document.getElementById(\"" + FormBuilderRenderer.getHtmlForm(component).getId() + Constants.sep + formBuilderIFrame.getCallbackButton().getId() + "\").click();"
                + "}"
                + "</script>");
    }
}
