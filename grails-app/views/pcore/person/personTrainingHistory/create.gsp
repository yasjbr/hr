<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'personTrainingHistory.entity', default: 'PersonTrainingHistory List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'PersonTrainingHistory List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'personTrainingHistory',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="personTrainingHistoryForm" controller="personTrainingHistory" action="save">
                <g:render template="/pcore/person/personTrainingHistory/form" model="[personTrainingHistory:personTrainingHistory]"/>
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
