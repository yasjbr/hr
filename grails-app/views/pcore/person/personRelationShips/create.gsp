<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'personRelationShips.entity', default: 'PersonRelationShips List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'PersonRelationShips List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'personRelationShips',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="personRelationShipsForm" controller="personRelationShips" action="save">
                <g:render template="/pcore/person/personRelationShips/form" model="[personRelationShips:personRelationShips]"/>
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
