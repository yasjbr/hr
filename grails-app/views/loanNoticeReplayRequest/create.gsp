<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'loanNoticeReplayRequest.entity', default: 'LoanNoticeReplayRequest List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'LoanNoticeReplayRequest List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'loanNoticeReplayRequest',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="loanNoticeReplayRequestForm" callLoadingFunction="performPostActionWithEncodedId" controller="loanNoticeReplayRequest" action="save">
                <g:render template="/loanNoticeReplayRequest/form" model="[loanNoticeReplayRequest:loanNoticeReplayRequest]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>

            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
