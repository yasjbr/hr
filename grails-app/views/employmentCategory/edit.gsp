<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'employmentCategory.entity', default: 'EmploymentCategory List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'EmploymentCategory List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'employmentCategory',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="employmentCategoryForm" controller="employmentCategory" action="update">
                <el:hiddenField name="id" value="${employmentCategory?.id}" />
                <g:render template="/employmentCategory/form" model="[employmentCategory:employmentCategory]"/>
                <el:formButton isSubmit="true" withPreviousLink="true"  functionName="save"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>