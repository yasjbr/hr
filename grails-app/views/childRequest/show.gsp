<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'childRequest.entity', default: 'ChildRequest List')}"/>
    <g:set var="title" value="${message(code: 'default.show.label', args: [entity], default: 'ChildRequest List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller: 'childRequest', action: 'list')}'"/>
    </div></div>
</div>
<el:row/><br/>

<g:render template="show" model="[childRequest:childRequest]" />
<div class="clearfix form-actions text-center">
    <g:if test="${childRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.CREATED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'childRequest', action: 'edit', params: [encodedId: childRequest?.encodedId])}'"/>
    </g:if>
    <g:if test="${childRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.ADD_TO_LIST || childRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.SENT_BY_LIST || childRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED || childRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.REJECTED}">
        <btn:button messageCode="childList.entities" color="pink"
                    icon="fa fa-bars" size="bigger" class="width-135"
                    onClick="window.location.href='${createLink(controller: 'childRequest', action: 'goToList',
                            params: [encodedId: childRequest?.encodedId])}'"/>
    </g:if>
    <btn:backButton goToPreviousLink="true"/>
</div>

</body>
</html>