<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'petitionRequest.entity', default: 'PetitionRequest List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'PetitionRequest List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'petitionRequest',action:'list')}'"/>
    </div></div>
</div>

<g:render template="show" model="[petitionRequest:petitionRequest]" />


<div class="clearfix form-actions text-center">
    <g:if test="${petitionRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.CREATED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'petitionRequest', action: 'edit', params: [encodedId: petitionRequest?.encodedId])}'"/>
    </g:if>
    <g:if test="${petitionRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.ADD_TO_LIST || petitionRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.SENT_BY_LIST || petitionRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED || petitionRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.REJECTED}">
        <btn:button messageCode="petitionList.entities" color="pink"
                    icon="fa fa-bars" size="bigger" class="width-135"
                    onClick="window.location.href='${createLink(controller: 'petitionRequest', action: 'goToList',
                            params: [encodedId: petitionRequest?.encodedId])}'"/>
    </g:if>
    <btn:backButton goToPreviousLink="true"/>
</div>
</body>
</html>