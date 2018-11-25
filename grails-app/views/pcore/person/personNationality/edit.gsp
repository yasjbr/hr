<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'personNationality.entity', default: 'PersonNationality List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'PersonNationality List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'personNationality',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="personNationalityForm" controller="personNationality" action="update">
                <el:hiddenField name="id" value="${personNationality?.id}" />
                <g:render template="/pcore/person/personNationality/form" model="[personNationality:personNationality]"/>
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>