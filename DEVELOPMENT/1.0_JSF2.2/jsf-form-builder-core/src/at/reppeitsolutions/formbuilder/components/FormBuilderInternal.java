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
package at.reppeitsolutions.formbuilder.components;

import at.reppeitsolutions.formbuilder.components.formbuilderitem.FormBuilderItemCheckbox;
import at.reppeitsolutions.formbuilder.components.formbuilderitem.FormBuilderItemConstraint;
import at.reppeitsolutions.formbuilder.components.formbuilderitem.FormBuilderItemDate;
import at.reppeitsolutions.formbuilder.components.formbuilderitem.FormBuilderItemDownload;
import at.reppeitsolutions.formbuilder.components.formbuilderitem.FormBuilderItemFormatArea;
import at.reppeitsolutions.formbuilder.components.formbuilderitem.FormBuilderItemHeading;
import at.reppeitsolutions.formbuilder.components.formbuilderitem.FormBuilderItemHorizonalRule;
import at.reppeitsolutions.formbuilder.components.formbuilderitem.FormBuilderItemImage;
import at.reppeitsolutions.formbuilder.components.formbuilderitem.FormBuilderItemInput;
import at.reppeitsolutions.formbuilder.components.formbuilderitem.FormBuilderItemLabel;
import at.reppeitsolutions.formbuilder.components.formbuilderitem.FormBuilderItemListbox;
import at.reppeitsolutions.formbuilder.components.formbuilderitem.FormBuilderItemMetaData;
import at.reppeitsolutions.formbuilder.components.formbuilderitem.FormBuilderItemNumber;
import at.reppeitsolutions.formbuilder.components.formbuilderitem.FormBuilderItemPagebreak;
import at.reppeitsolutions.formbuilder.components.formbuilderitem.FormBuilderItemRadio;
import at.reppeitsolutions.formbuilder.components.formbuilderitem.FormBuilderItemSelect;
import at.reppeitsolutions.formbuilder.components.formbuilderitem.FormBuilderItemSpace;
import at.reppeitsolutions.formbuilder.components.formbuilderitem.FormBuilderItemTextarea;
import at.reppeitsolutions.formbuilder.components.formbuilderitem.FormBuilderItemTime;
import at.reppeitsolutions.formbuilder.components.formbuilderitem.FormBuilderItemUpload;
import at.reppeitsolutions.formbuilder.components.helper.FormBuilderItemFactory;
import at.reppeitsolutions.formbuilder.components.helper.JQueryHelper;
import at.reppeitsolutions.formbuilder.components.helper.MetaDataDescription;
import at.reppeitsolutions.formbuilder.components.html.HtmlDiv;
import at.reppeitsolutions.formbuilder.components.html.HtmlHeading;
import at.reppeitsolutions.formbuilder.components.html.formbuilder.HtmlFormBuilderItem;
import at.reppeitsolutions.formbuilder.components.html.HtmlUnorderedList;
import at.reppeitsolutions.formbuilder.components.html.renderer.formbuilder.FormBuilderInternalRenderer;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.html.HtmlInputHidden;
import at.reppeitsolutions.formbuilder.messages.Messages;
import java.util.Locale;

/**
 *
 * @author Mathias Reppe <mathias.reppe@gmail.com>
 */
@FacesComponent(createTag = true, namespace = Constants.NAMESPACE, tagName = "formBuilderInternal")
@ResourceDependencies(value = {
    @ResourceDependency(library = "javax.faces", name = "jsf.js"),
    @ResourceDependency(library = "formbuilder", name = "js/jquery-1.9.1.js"),
    @ResourceDependency(library = "formbuilder", name = "js/jquery-ui-1.10.3.custom.js"),
    @ResourceDependency(library = "formbuilder", name = "js/jquery-ui-1.10.3.custom.min.js"),
    @ResourceDependency(library = "formbuilder", name = "js/jquery.timepicker.min.js"),
    @ResourceDependency(library = "formbuilder", name = "css/smoothness/jquery-ui-1.10.3.custom.css"),
    @ResourceDependency(library = "formbuilder", name = "css/smoothness/jquery-ui-1.10.3.custom.min.css"),
    @ResourceDependency(library = "formbuilder", name = "jquery.listAttributes.js"),
    @ResourceDependency(library = "formbuilder", name = "jquery.timepicker.css"),
    @ResourceDependency(library = "formbuilder", name = "formbuilder.js"),
    @ResourceDependency(library = "formbuilder", name = "formbuilder.css")})
public class FormBuilderInternal extends FormBuilderComponentBase {

    public static final String FORMACTIONSTRING = "formActionString";
    public static final String FORMCONTENTSTRING = "formContentString";
    public static final String FORMACTIVETABSTRING = "formActiveTabString";
    private List<HtmlUnorderedList> palettes = new ArrayList<>();

    private enum COMPONENT_CATEGORY {

        INPUT, OUTPUT, FORMAT, WORKFLOW, METADATA
    }

    public FormBuilderInternal() {
        setRendererType(FormBuilderInternalRenderer.RENDERTYPE);
    }

    public void initFormBuilder() {
        formContent = new HtmlUnorderedList();
        formContent.setClassString("connectedSortable sortable2 box-runde-ecken");

        HtmlInputHidden formActionString = new HtmlInputHidden();
        formActionString.setValue("update");
        formActionString.setId(FORMACTIONSTRING);

        HtmlInputHidden formContentString = new HtmlInputHidden();
        formContentString.setValue("");
        formContentString.setId(FORMCONTENTSTRING);

        HtmlInputHidden formActiveTabString = new HtmlInputHidden();
        if(getActiveTab() != null) {
            formActiveTabString.setValue(getActiveTab());
        } else {
            formActiveTabString.setValue("0");
        }
        formActiveTabString.setId(FORMACTIVETABSTRING);

        HtmlInputHidden propDialogHeader = new HtmlInputHidden();
        propDialogHeader.setValue(Messages.getStringJSF("dialog.header"));
        propDialogHeader.setId("prop-dialog-header");

        HtmlDiv holder = new HtmlDiv();
        holder.setId("holder");

        holder.getChildren().add(formActionString);
        holder.getChildren().add(formContentString);
        holder.getChildren().add(formActiveTabString);
        holder.getChildren().add(propDialogHeader);

        HtmlDiv accordion = new HtmlDiv();
        accordion.setId("accordion");

        for (COMPONENT_CATEGORY category : COMPONENT_CATEGORY.values()) {
            if(category != COMPONENT_CATEGORY.METADATA ||
               (category == COMPONENT_CATEGORY.METADATA && getMetaDataDescriptions() != null && getMetaDataObject() != null)) {
                HtmlHeading heading = new HtmlHeading();
                heading.setSize(3);
                heading.setValue(Messages.getStringJSF("menu.palette." + category.name()));
                HtmlDiv paletteDiv = new HtmlDiv();
                HtmlUnorderedList palette = getPalette(category);
                palettes.add(palette);
                paletteDiv.getChildren().add(palette);
                accordion.getChildren().add(heading);
                accordion.getChildren().add(paletteDiv);
            }
        }

        HtmlDiv accordionHolder = new HtmlDiv();
        accordionHolder.setId("accordionHolder");

        HtmlDiv ajaxReload = new HtmlDiv();
        ajaxReload.setId("ajaxReload");
        accordionHolder.getChildren().add(ajaxReload);
        accordionHolder.getChildren().add(accordion);

        holder.getChildren().add(accordionHolder);

        HtmlDiv contentHolder = new HtmlDiv();
        contentHolder.setId("contentHolder");
        contentHolder.getChildren().add(formContent);

        holder.getChildren().add(contentHolder);

        getChildren().add(holder);

        HtmlDiv div = new HtmlDiv();
        div.setStyle("clear:both;");
        getChildren().add(div);
    }

    @Override
    public String getFamily() {
        return FormBuilderInternalRenderer.FAMILY;
    }

    public List<HtmlUnorderedList> getPalettes() {
        return palettes;
    }

    private HtmlUnorderedList getPalette(COMPONENT_CATEGORY category) {
        List<HtmlFormBuilderItem> components = new ArrayList<>();

        if (category == COMPONENT_CATEGORY.OUTPUT) {
            components.add(FormBuilderItemFactory.getUIComponent(new FormBuilderItemHeading(true)));
            components.add(FormBuilderItemFactory.getUIComponent(new FormBuilderItemLabel(true)));
            components.add(FormBuilderItemFactory.getUIComponent(new FormBuilderItemImage(true)));
            components.add(FormBuilderItemFactory.getUIComponent(new FormBuilderItemDownload(true)));
        } else if (category == COMPONENT_CATEGORY.INPUT) {
            components.add(FormBuilderItemFactory.getUIComponent(new FormBuilderItemInput(true)));
            components.add(FormBuilderItemFactory.getUIComponent(new FormBuilderItemTextarea(true)));
            components.add(FormBuilderItemFactory.getUIComponent(new FormBuilderItemNumber(true)));
            components.add(FormBuilderItemFactory.getUIComponent(new FormBuilderItemDate(true)));
            components.add(FormBuilderItemFactory.getUIComponent(new FormBuilderItemTime(true)));
            components.add(FormBuilderItemFactory.getUIComponent(new FormBuilderItemUpload(true)));
            components.add(FormBuilderItemFactory.getUIComponent(new FormBuilderItemSelect(true)));
            components.add(FormBuilderItemFactory.getUIComponent(new FormBuilderItemRadio(true)));
            components.add(FormBuilderItemFactory.getUIComponent(new FormBuilderItemCheckbox(true)));
            components.add(FormBuilderItemFactory.getUIComponent(new FormBuilderItemListbox(true)));
        } else if (category == COMPONENT_CATEGORY.FORMAT) {
            components.add(FormBuilderItemFactory.getUIComponent(new FormBuilderItemHorizonalRule(true)));
            components.add(FormBuilderItemFactory.getUIComponent(new FormBuilderItemSpace(true)));
            components.add(FormBuilderItemFactory.getUIComponent(new FormBuilderItemFormatArea(true)));
            components.add(FormBuilderItemFactory.getUIComponent(new FormBuilderItemPagebreak(true)));
        } else if (category == COMPONENT_CATEGORY.WORKFLOW) {
            components.add(FormBuilderItemFactory.getUIComponent(new FormBuilderItemConstraint(true)));
        } else if (category == COMPONENT_CATEGORY.METADATA &&
                getMetaDataObject() != null &&
                getMetaDataDescriptions() != null) {
            for(MetaDataDescription description : getMetaDataDescriptions()) {
                components.add(FormBuilderItemFactory.getUIComponent(new FormBuilderItemMetaData(true, description)));
            }
        }

        HtmlUnorderedList palette = new HtmlUnorderedList();
        palette.setClassString("connectedSortable sortable1");

        JQueryHelper.encasulateForJQuery(palette, components);

        return palette;
    }
    
    public String getActiveTab() {
        return (String) getStateHelper().eval("activeTab");
    }

    public void setActiveTab(String activeTab) {
        getStateHelper().put("activeTab", activeTab);
    }
    
}
