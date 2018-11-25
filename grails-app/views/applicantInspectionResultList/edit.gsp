<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'applicantInspectionResultList.entity', default: 'SuspensionExtensionList List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'SuspensionExtensionList List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'applicantInspectionResultList',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="applicantInspectionResultListForm" controller="applicantInspectionResultList" action="update">
                <g:render template="/applicantInspectionResultList/form" model="[applicantInspectionResultList:applicantInspectionResultList]"/>
                <el:hiddenField name="encodedId" value="${applicantInspectionResultList?.encodedId}" />
                <el:formButton isSubmit="true" functionName="save" withPreviousLink="true" />
                <el:formButton functionName="back"  goToPreviousLink="true" withPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>