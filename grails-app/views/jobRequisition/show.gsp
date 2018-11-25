<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumRequestStatus" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'jobRequisition.entity', default: 'JobRequisition List')}"/>
    <g:set var="title" value="${message(code: 'default.show.label', args: [entity], default: 'JobRequisition List')}"/>
    <title>${title}</title>
</head>

<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller: 'jobRequisition', action: 'list')}'"/>
    </div></div>
</div>

<el:row/> <br/> <el:row/>

<g:render template="/jobRequisition/show" model="[jobRequisition:jobRequisition]"/>

<div class="clearfix form-actions text-center">
    <g:if test="${jobRequisition?.requisitionStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.CREATED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'jobRequisition', action: 'edit', params: [encodedId: jobRequisition?.encodedId])}'"/>
    </g:if>
    <btn:backButton goToPreviousLink="true"/>
</div>

</body>
</html>








