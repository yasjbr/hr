<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'dispatchRequest.entity', default: 'DispatchRequest List')}"/>
    <g:set var="title" value="${message(code: 'default.show.label', args: [entity], default: 'DispatchRequest List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller: 'dispatchRequest', action: 'list')}'"/>
    </div></div>
</div>
<br/><br/><br/>
<g:render template="/employee/wrapperForm" model="[employee: dispatchRequest?.employee]"/>

<el:row/>


<g:render template="/dispatchRequest/show" model="[dispatchRequest: dispatchRequest]"/>



<div class="clearfix form-actions text-center">
    <g:if test="${dispatchRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.CREATED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'dispatchRequest', action: 'edit', params: [encodedId: dispatchRequest?.encodedId])}'"/>
    </g:if>
    <g:if test="${dispatchRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.ADD_TO_LIST || dispatchRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.SENT_BY_LIST || dispatchRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED || dispatchRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.REJECTED}">
        <btn:button messageCode="dispatchList.entities" color="pink"
                    icon="fa fa-bars" size="bigger" class="width-135"
                    onClick="window.location.href='${createLink(controller: 'dispatchRequest', action: 'goToList',
                            params: [encodedId: dispatchRequest?.encodedId])}'"/>
    </g:if>
    <btn:backButton goToPreviousLink="true" withPreviousLink="true"/>
</div>

</body>
</html>
