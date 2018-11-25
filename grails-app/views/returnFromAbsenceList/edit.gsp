<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'returnFromAbsenceList.entity', default: 'ReturnFromAbsenceList List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'ReturnFromAbsenceList List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'returnFromAbsenceList',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="returnFromAbsenceListForm" controller="returnFromAbsenceList" action="update">
                <g:render template="/returnFromAbsenceList/form" model="[returnFromAbsenceList:returnFromAbsenceList]"/>
                <el:hiddenField name="encodedId" value="${returnFromAbsenceList?.encodedId}" />
                <el:formButton isSubmit="true" functionName="save" withPreviousLink="true" />
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>