<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'allowanceType.entity', default: 'AllowanceType List')}"/>
    <g:set var="title" value="${message(code: 'default.edit.label', args: [entity], default: 'AllowanceType List')}"/>
    <title>${title}</title>

</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller: 'allowanceType', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableForm name="allowanceTypeForm" controller="allowanceType" action="update">
                <g:render template="/allowanceType/form" model="[allowanceType: allowanceType]"/>
                <el:hiddenField name="id" value="${allowanceType?.id}"/>
                <el:formButton isSubmit="true" withPreviousLink="true" functionName="save"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>