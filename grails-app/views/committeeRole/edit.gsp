<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'committeeRole.entity', default: 'CommitteeRole List')}"/>
    <g:set var="title" value="${message(code: 'default.edit.label', args: [entity], default: 'CommitteeRole List')}"/>
    <title>${title}</title>

</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller: 'committeeRole', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableForm name="committeeRoleForm" controller="committeeRole" action="update">
                <el:hiddenField name="id" value="${committeeRole?.id}"/>
                <g:render template="/committeeRole/form" model="[committeeRole: committeeRole]"/>
                <el:formButton isSubmit="true" withPreviousLink="true" functionName="save"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>