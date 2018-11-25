<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'jobRequisition.entity', default: 'JobRequisition List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'JobRequisition List')}" />
    <title>${title}</title>
    <g:render template="script"/>


</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'jobRequisition',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="jobRequisitionForm" controller="jobRequisition" action="update">
                <el:hiddenField name="id" value="${jobRequisition?.id}" />
                <g:render template="/jobRequisition/form" model="[jobRequisition:jobRequisition, type:type]"/>
                <el:formButton isSubmit="true" functionName="save" withPreviousLink="true"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>