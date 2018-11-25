<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity"
           value="${message(code: 'internalTransferRequest.entity', default: 'InternalTransferRequest List')}"/>
    <g:set var="title"
           value="${message(code: 'default.show.label', args: [entity], default: 'InternalTransferRequest List')}"/>
    <title>${title}</title>
</head>

<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'internalTransferRequest', action: 'list')}'"/>
    </div></div>
</div>

<g:render template="/internalTransferRequest/show"
          model="[internalTransferRequest: internalTransferRequest, title: title]"/>

<div class="clearfix form-actions text-center">
    <g:if test="${internalTransferRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.CREATED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'internalTransferRequest', action: 'edit', params: [encodedId: internalTransferRequest?.encodedId])}'"/>
    </g:if>
    %{--<g:if test="${internalTransferRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED_BY_WORKFLOW}">
        <btn:button color="approve" messageCode="internalTransferRequest.closeRequest.label" icon="icon-ok"
                    size="bigger" class="width-135"
                    onClick="window.location.href='${createLink(controller: 'internalTransferRequest', action: 'close',
                            params: [encodedId: internalTransferRequest?.encodedId])}'"/>
    </g:if>--}%
    <btn:backButton/>
</div>

</body>
</html>