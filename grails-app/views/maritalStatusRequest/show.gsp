<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'maritalStatusRequest.entity', default: 'MaritalStatusRequest List')}"/>
    <g:set var="title"
           value="${message(code: 'default.show.label', args: [entity], default: 'MaritalStatusRequest List')}"/>
    <title>${title}</title>
</head>

<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'maritalStatusRequest', action: 'list')}'"/>
    </div></div>
</div>

<el:row/><br/>

<g:render template="show" model="[maritalStatusRequest: maritalStatusRequest]" />
<div class="clearfix form-actions text-center">
    <g:if test="${maritalStatusRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.CREATED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'maritalStatusRequest', action: 'edit', params: [encodedId: maritalStatusRequest?.encodedId])}'"/>
    </g:if>
    <g:if test="${maritalStatusRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.ADD_TO_LIST || maritalStatusRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.SENT_BY_LIST || maritalStatusRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED || maritalStatusRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.REJECTED}">
        <btn:button messageCode="maritalStatusList.entities" color="pink"
                    icon="fa fa-bars" size="bigger" class="width-135"
                    onClick="window.location.href='${createLink(controller: 'maritalStatusRequest', action: 'goToList',
                            params: [encodedId: maritalStatusRequest?.encodedId])}'"/>
    </g:if>

    <btn:backButton goToPreviousLink="true" withPreviousLink="true"/>
</div>

</body>
</html>