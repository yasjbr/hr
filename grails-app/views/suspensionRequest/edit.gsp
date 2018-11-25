<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'suspensionRequest.entity', default: 'SuspensionRequest List')}"/>
    <g:set var="title"
           value="${message(code: 'default.edit.label', args: [entity], default: 'SuspensionRequest List')}"/>
    <title>${title}</title>
    <g:render template="scripts"/>
</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <el:modalLink link="${createLink(controller: 'suspensionRequest',action: 'previousSuspensionsModal',id: suspensionRequest?.employee?.id)}"
                      preventCloseOutSide="true" class="btn btn-sm btn-info2 width-135"
                      label="${message(code: 'suspensionRequest.previous.suspension.request.label')}">
            <i class="icon-list"></i>
        </el:modalLink>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller: 'suspensionRequest', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableForm name="suspensionRequestForm" controller="suspensionRequest" action="update">
                <g:render template="/suspensionRequest/form" model="[suspensionRequest: suspensionRequest]"/>
                <el:hiddenField name="id" value="${suspensionRequest?.id}"/>
                <el:formButton isSubmit="true" functionName="save" withPreviousLink="true"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>