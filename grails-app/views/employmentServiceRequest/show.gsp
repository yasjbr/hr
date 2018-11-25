<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:if test="${employmentServiceRequest.requestType == ps.gov.epsilon.hr.enums.v1.EnumRequestType.RETURN_TO_SERVICE}" >
        <g:set var="messageCode" value="recallToServiceList.entities" />
        <g:set var="entity" value="${message(code: 'recallToService.entity', default: 'recallToServiceList List')}" />
        <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'recallToServiceList List')}" />
    </g:if>
    <g:else>
        <g:set var="messageCode" value="endOfServiceList.entities" />
        <g:set var="entity" value="${message(code: 'endOfService.entity', default: 'endOfService List')}" />
        <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'endOfService List')}" />
    </g:else>
    <title>${title}</title>
</head>
<body>

<msg:page/>

<g:if test="${employmentServiceRequest.requestType == ps.gov.epsilon.hr.enums.v1.EnumRequestType.RETURN_TO_SERVICE}" >
    <div style="margin-top: -46px">
        <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
            <btn:listButton onClick="window.location.href='${createLink(controller:'employmentServiceRequest',action:'listReturnToService')}'"/>
        </div></div>
    </div>
</g:if>
<g:else>
    <div style="margin-top: -46px">
        <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
            <btn:listButton onClick="window.location.href='${createLink(controller:'employmentServiceRequest',action:'listEndOfService')}'"/>
        </div></div>
    </div>
</g:else>
<g:render template="/employmentServiceRequest/show" model="[employmentServiceRequest:employmentServiceRequest]"/>
<el:row />

<div class="clearfix form-actions text-center">
    <g:if test="${employmentServiceRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.CREATED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'employmentServiceRequest', action: 'edit', params: [encodedId: employmentServiceRequest?.encodedId])}'"/>
    </g:if>
    <g:if test="${employmentServiceRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.ADD_TO_LIST || employmentServiceRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.SENT_BY_LIST || employmentServiceRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED || employmentServiceRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.REJECTED}">
        <btn:button messageCode="${messageCode}" color="pink"
                    icon="fa fa-bars" size="bigger" class="width-135"
                    onClick="window.location.href='${createLink(controller: 'employmentServiceRequest', action: 'goToList',
                            params: [encodedId: employmentServiceRequest?.encodedId])}'"/>
    </g:if>
    <btn:backButton goToPreviousLink="true" withPreviousLink="true"/>
</div>

</body>
</html>