<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'personHealthHistory.entity', default: 'PersonHealthHistory List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'PersonHealthHistory List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'personHealthHistory',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="personHealthHistoryForm" controller="personHealthHistory" action="update">
                <el:hiddenField name="id" value="${personHealthHistory?.id}" />
                <g:render template="/pcore/person/personHealthHistory/form" model="[personHealthHistory:personHealthHistory]"/>
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>