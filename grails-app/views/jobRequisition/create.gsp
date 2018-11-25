<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'jobRequisition.entity', default: 'JobRequisition List')}"/>
    <g:set var="title"
           value="${message(code: 'default.create.label', args: [entity], default: 'JobRequisition List')}"/>
    <title>${title}</title>
    <g:render template="script"/>
</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller: 'jobRequisition', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableResetForm name="jobRequisitionForm" id="jobRequisitionForm"
                                     callLoadingFunction="performPostActionWithEncodedId"
                                     controller="jobRequisition" action="save">
                <g:render template="/jobRequisition/form"
                          model="[jobRequisition: jobRequisition, currentDate: currentDate, type: type]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
