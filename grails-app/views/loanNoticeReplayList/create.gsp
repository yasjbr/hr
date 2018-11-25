<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'loanNoticeReplayList.entity', default: 'Loan List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'Loan List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'loanNoticeReplayList',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="loanNoticeReplayListForm" callLoadingFunction="performPostActionWithEncodedId" controller="loanNoticeReplayList" action="save">
                <g:render template="/loanNoticeReplayList/form" model="[loanNoticeReplayList:loanNoticeReplayList]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" withPreviousLink="true" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
