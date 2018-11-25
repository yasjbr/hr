<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'personArrestHistory.entity', default: 'PersonArrestHistory List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'PersonArrestHistory List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'personArrestHistory',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
            <msg:page />
            <el:validatableForm name="personArrestHistoryForm" controller="personArrestHistory" action="update">
                <el:hiddenField name="id" value="${personArrestHistory?.id}" />
                <g:render template="/pcore/person/personArrestHistory/form" model="[personArrestHistory:personArrestHistory]"/>
                <el:formButton functionName="save" isSubmit="true"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
    </lay:widgetBody>
</lay:widget>
</body>
</html>