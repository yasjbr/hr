<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'personEmploymentHistory.entity', default: 'PersonEmploymentHistory List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'PersonEmploymentHistory List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'personEmploymentHistory',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="personEmploymentHistoryForm" controller="personEmploymentHistory" action="update">
                <el:hiddenField name="id" value="${personEmploymentHistory?.id}" />
                <g:render template="/pcore/person/personEmploymentHistory/form" model="[personEmploymentHistory:personEmploymentHistory]"/>
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>