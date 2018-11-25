<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'vacancy.entity', default: 'Vacancy List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'Vacancy List')}" />
    <title>${title}</title>
    <g:render template="script"/>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'vacancy',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="vacancyForm" controller="vacancy" action="update">
                <el:hiddenField name="encodedId" value="${vacancy?.encodedId}"/>
                <g:render template="/vacancy/form" model="[vacancy:vacancy, type:type]"/>
                <el:formButton isSubmit="true" functionName="save" withPreviousLink="true" />
                <el:formButton functionName="cancel" withPreviousLink="true" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>