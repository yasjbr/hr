<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'personMaritalStatus.entity', default: 'PersonMaritalStatus List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'PersonMaritalStatus List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'personMaritalStatus',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="personMaritalStatusForm" controller="personMaritalStatus" action="update">
                <el:hiddenField name="id" value="${personMaritalStatus?.id}" />
                <g:render template="/pcore/person/personMaritalStatus/form" model="[personMaritalStatus:personMaritalStatus]"/>
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>