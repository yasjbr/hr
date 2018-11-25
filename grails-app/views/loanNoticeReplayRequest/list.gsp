<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'loanNoticeReplayRequest.entities', default: 'LoanNoticeReplayRequest List')}" />
    <g:set var="entity" value="${message(code: 'loanNoticeReplayRequest.entity', default: 'LoanNoticeReplayRequest')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'LoanNoticeReplayRequest List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="loanNoticeReplayRequestCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'loanNoticeReplayRequest',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="loanNoticeReplayRequestSearchForm">
            <g:render template="/loanNoticeReplayRequest/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['loanNoticeReplayRequestTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('loanNoticeReplayRequestSearchForm');_dataTables['loanNoticeReplayRequestTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<g:render template="/loanNoticeReplayRequest/dataTable" model="[hasAttachment:true]"/>

<g:render template="/request/script"/>

</body>
</html>