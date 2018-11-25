<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:if test="${updateMilitaryRankRequest?.requestType == ps.gov.epsilon.hr.enums.v1.EnumRequestType.UPDATE_MILITARY_RANK_TYPE}">
        <g:set var="entity" value="${message(code: 'updateMilitaryRankTypeRequest.entity', default: 'updateMilitaryRankTypeRequest List')}" />
    </g:if><g:else>
        <g:set var="entity" value="${message(code: 'updateMilitaryRankClassification.entity', default: 'updateMilitaryRankClassification List')}" />
    </g:else>
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'UpdateMilitaryRankRequest List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'updateMilitaryRankRequest',action:'list')}'"/>
    </div></div>
</div>
<el:row/><br/>
<g:render template="show" model="[updateMilitaryRankRequest:updateMilitaryRankRequest]" />

<div class="clearfix form-actions text-center">
    <g:if test="${updateMilitaryRankRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.CREATED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'updateMilitaryRankRequest', action: 'edit', params: [encodedId: updateMilitaryRankRequest?.encodedId])}'"/>
    </g:if>
    <g:if test="${updateMilitaryRankRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.ADD_TO_LIST || updateMilitaryRankRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.SENT_BY_LIST || updateMilitaryRankRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED || updateMilitaryRankRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.REJECTED}">
        <btn:button messageCode="promotionList.entities" color="pink"
                    icon="fa fa-bars" size="bigger" class="width-135"
                    onClick="window.location.href='${createLink(controller: 'updateMilitaryRankRequest', action: 'goToList',
                            params: [encodedId: updateMilitaryRankRequest?.encodedId])}'"/>
    </g:if>
    <btn:backButton goToPreviousLink="true"/>
</div>

</body>
</html>







