<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'externalTransferRequest.entity', default: 'ExternalTransferRequest List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'ExternalTransferRequest List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'externalTransferRequest',action:'list')}'"/>
    </div></div>
</div>


<g:render template="/externalTransferRequest/show" model="[externalTransferRequest:externalTransferRequest,title:title]" />


<div class="clearfix form-actions text-center">
    <g:if test="${externalTransferRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.CREATED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'externalTransferRequest', action: 'edit', params: [encodedId: externalTransferRequest?.encodedId])}'"/>
    </g:if>
    <g:if test="${externalTransferRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.ADD_TO_LIST || externalTransferRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.SENT_BY_LIST || externalTransferRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED || externalTransferRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.REJECTED}">
        <btn:button messageCode="externalTransferList.entities" color="pink"
                    icon="fa fa-bars" size="bigger" class="width-135"
                    onClick="window.location.href='${createLink(controller: 'externalTransferRequest', action: 'goToList',
                            params: [encodedId: externalTransferRequest?.encodedId])}'"/>
    </g:if>

    <g:if test="${externalTransferRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED && externalTransferRequest?.hasClearance && externalTransferRequest?.hasTransferPermission}">
        <btn:button color="approve" messageCode="externalTransferRequest.closeRequest.label" icon="icon-ok"
                    size="bigger" class="width-135"
                    onClick="window.location.href='${createLink(controller: 'externalTransferRequest', action: 'closeRequest',
                            params: [encodedId: externalTransferRequest?.encodedId])}'"/>
    </g:if>
    <btn:backButton />
</div>

</body>
</html>