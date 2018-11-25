<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'serviceActionReasonType.entity', default: 'ServiceActionReasonType List')}"/>
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'ServiceActionReasonType List')}"/>
    <title>${title}</title>

</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller:'serviceActionReasonType',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableForm name="serviceActionReasonTypeForm" controller="serviceActionReasonType" action="update">
                <el:hiddenField name="id" value="${serviceActionReasonType?.id}"/>
                <g:render template="form" model="[serviceActionReasonType:serviceActionReasonType]" />
                <el:formButton isSubmit="true" withPreviousLink="true" functionName="save"/>
                <el:formButton functionName="cancel" goToPreviousLink="true" withPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>