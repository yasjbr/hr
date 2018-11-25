<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'applicantStatusHistory.entity', default: 'ApplicantStatusHistory List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'ApplicantStatusHistory List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'applicantStatusHistory',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="applicantStatusHistoryForm" controller="applicantStatusHistory" action="update">
                <g:render template="/applicantStatusHistory/form" model="[applicantStatusHistory:applicantStatusHistory]"/>
                <el:hiddenField name="id" value="${applicantStatusHistory?.id}" />
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>