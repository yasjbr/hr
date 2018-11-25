<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'allowanceStopReason.entity', default: 'AllowanceStopReason List')}"/>
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'AllowanceStopReason List')}"/>
    <title>${title}</title>

</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller:'allowanceStopReason',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableForm name="allowanceStopReasonForm" controller="allowanceStopReason" action="update">
                <el:hiddenField name="id" value="${allowanceStopReason?.id}"/>
                <g:render template="form"/>
                <el:formButton isSubmit="true" withPreviousLink="true" functionName="save"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>