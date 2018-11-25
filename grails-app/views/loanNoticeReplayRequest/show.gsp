<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'loanNoticeReplayRequest.entity', default: 'LoanNoticeReplayRequest List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'LoanNoticeReplayRequest List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'loanNoticeReplayRequest',action:'list')}'"/>
    </div></div>
</div>

<g:render template="/loanNoticeReplayRequest/show" model="[loanNoticeReplayRequest:loanNoticeReplayRequest,title:title]" />
<div class="clearfix form-actions text-center">
    <g:if test="${loanNoticeReplayRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.CREATED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'loanNoticeReplayRequest', action: 'edit', params: [encodedId: loanNoticeReplayRequest?.encodedId])}'"/>
    </g:if>
    <g:if test="${loanNoticeReplayRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.ADD_TO_LIST || loanNoticeReplayRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.SENT_BY_LIST || loanNoticeReplayRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED || loanNoticeReplayRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.REJECTED}">
        <btn:button messageCode="loanNoticeReplayList.entities" color="pink"
                    icon="fa fa-bars" size="bigger" class="width-135"
                    onClick="window.location.href='${createLink(controller: 'loanNoticeReplayRequest', action: 'goToList',
                            params: [encodedId: loanNoticeReplayRequest?.encodedId])}'"/>
    </g:if>
    <btn:backButton />
</div>
</body>
</html>