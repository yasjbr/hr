<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'returnFromAbsenceList.entity', default: 'ReturnFromAbsenceList List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'ReturnFromAbsenceList List')}" />
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
            <el:validatableResetForm name="returnFromAbsenceListForm" callLoadingFunction="performPostActionWithEncodedId" controller="returnFromAbsenceList" action="save">
                <g:render template="/returnFromAbsenceList/form" model="[returnFromAbsenceList:returnFromAbsenceList]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
