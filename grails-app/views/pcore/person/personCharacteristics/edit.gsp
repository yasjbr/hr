<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'personCharacteristics.entity', default: 'PersonCharacteristics List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'PersonCharacteristics List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'personCharacteristics',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="personCharacteristicsForm" controller="personCharacteristics" action="update">
                <el:hiddenField name="id" value="${personCharacteristics?.id}" />
                <g:render template="/pcore/person/personCharacteristics/form" model="[personCharacteristics:personCharacteristics]"/>
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>