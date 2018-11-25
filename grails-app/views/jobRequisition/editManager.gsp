<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'jobRequisition.entity', default: 'JobRequisition ListManager')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'JobRequisition ListManager')}" />
    <title>${title}</title>
    <g:render template="script"/>


</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'jobRequisition',action:'listManager')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="jobRequisitionForm" controller="jobRequisition" action="update">
                <el:hiddenField name="id" value="${jobRequisition?.id}" />
                <g:render template="/jobRequisition/form" model="[jobRequisition:jobRequisition]"/>
                <el:formButton isSubmit="true" functionName="save" withPreviousLink="true" />
                <el:formButton functionName="cancel" withPreviousLink="true" />
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>