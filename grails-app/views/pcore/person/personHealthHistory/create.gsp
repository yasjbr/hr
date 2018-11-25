<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'personHealthHistory.entity', default: 'PersonHealthHistory List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'PersonHealthHistory List')}" />
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
            <el:validatableResetForm name="personHealthHistoryForm" controller="personHealthHistory" action="save">
                <g:render template="/pcore/person/personHealthHistory/form" model="[personHealthHistory:personHealthHistory]"/>
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
