<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'militaryRankType.entity', default: 'MilitaryRankType List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'MilitaryRankType List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'militaryRankType',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="militaryRankTypeForm" controller="militaryRankType" action="update">
                <g:render template="/militaryRankType/form" model="[militaryRankType:militaryRankType]"/>
                <el:hiddenField name="id" value="${militaryRankType?.id}" />
                <el:formButton isSubmit="true" withPreviousLink="true"  functionName="save"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>