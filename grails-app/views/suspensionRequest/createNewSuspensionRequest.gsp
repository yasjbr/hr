<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'suspensionRequest.entity', default: 'suspensionRequest List')}"/>
    <g:set var="title"
           value="${message(code: 'default.create.label', args: [entity], default: 'suspensionRequest List')}"/>
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
            <el:validatableResetForm name="suspensionRequestForm" callLoadingFunction="performPostActionWithEncodedId"
                                     controller="suspensionRequest" action="save">
                <g:render template="/suspensionRequest/form" model="[suspensionRequest: suspensionRequest]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel"
                               onClick="window.location.href='${createLink(controller: 'suspensionRequest', action: 'list')}'"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
