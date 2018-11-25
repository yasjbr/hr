<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'personEducation.entity', default: 'PersonEducation List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'PersonEducation List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'personEducation',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="personEducationForm" controller="personEducation" action="update">
                <el:hiddenField name="id" value="${personEducation?.id}" />
                <g:render template="/pcore/person/personEducation/form" model="[personEducation:personEducation]"/>
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>