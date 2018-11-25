<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'inspectionCategory.entity', default: 'InspectionCategory List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'InspectionCategory List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'inspectionCategory',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="inspectionCategoryForm" controller="inspectionCategory" action="update">
                <el:hiddenField name="id" value="${inspectionCategory?.id}" />
                <g:render template="/inspectionCategory/form" model="[inspectionCategory:inspectionCategory]"/>
                <el:formButton isSubmit="true" withPreviousLink="true"  functionName="save"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>