<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'personTrainingHistory.entity', default: 'PersonTrainingHistory List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'PersonTrainingHistory List')}" />
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
            <el:validatableForm name="personTrainingHistoryForm" controller="personTrainingHistory" action="update">
                <el:hiddenField name="id" value="${personTrainingHistory?.id}" />
                <g:render template="/pcore/person/personTrainingHistory/form" model="[personTrainingHistory:personTrainingHistory]"/>
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>