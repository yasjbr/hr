<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'loanNoticeReplayList.entity', default: 'LoanNoticeReplayList List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'LoanNoticeReplayList List')}" />
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
            <el:validatableForm name="loanNoticeReplayListForm" controller="loanNoticeReplayList" action="update">
                <g:render template="/loanNoticeReplayList/form" model="[loanNoticeReplayList:loanNoticeReplayList]"/>
                <el:hiddenField name="encodedId" value="${loanNoticeReplayList?.encodedId}" />
                <el:formButton isSubmit="true" functionName="save" withPreviousLink="true" goToPreviousLink="true"/>
                <el:formButton functionName="cancel" withPreviousLink="true" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>