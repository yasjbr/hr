<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity"
           value="${message(code: 'promotionRequest.entity', default: 'SituationSettlementRequest List')}"/>
    <g:set var="title"
           value="${message(code: 'default.show.label', args: [entity], default: 'SituationSettlementRequest List')}"/>
    <title>${title}</title>
</head>

<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'promotionRequest', action: 'list')}'"/>
    </div></div>
</div>

<g:render template="show" model="[promotionRequest: promotionRequest]" />

<div class="clearfix form-actions text-center">
    <g:if test="${promotionRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.CREATED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'promotionRequest', action: 'edit', params: [encodedId: promotionRequest?.encodedId])}'"/>
    </g:if>

    <g:if test="${promotionRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.ADD_TO_LIST || promotionRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.SENT_BY_LIST || promotionRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED || promotionRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.REJECTED}">
        <btn:button messageCode="promotionList.entities" color="pink"
                    icon="fa fa-bars" size="bigger" class="width-135"
                    onClick="window.location.href='${createLink(controller: 'promotionRequest', action: 'goToList',
                            params: [encodedId: promotionRequest?.encodedId])}'"/>
    </g:if>

    <btn:backButton goToPreviousLink="true"/>
</div>

</body>
</html>