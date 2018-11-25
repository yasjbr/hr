<%@ page import="ps.gov.epsilon.hr.enums.disciplinary.v1.EnumViolationStatus" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'absence.entity', default: 'Absence List')}"/>
    <g:set var="title" value="${message(code: 'default.show.label', args: [entity], default: 'Absence List')}"/>
    <title>${title}</title>
</head>

<body>

<msg:page/>


<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller: 'absence', action: 'list')}'"/>
    </div></div>
</div>

<el:row/><br/>
<g:render template="/employee/employeeShowWrapper" model="[employee: absence?.employee]"/>

<el:row/>
<el:row/>
<el:row/>
<g:render template="/absence/show" model="[absence: absence]"/>
<el:row/>
<el:row/>

<div class="clearfix form-actions text-center">
    <g:if test="${absence?.violationStatus == ps.gov.epsilon.hr.enums.disciplinary.v1.EnumViolationStatus.NEW}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'absence', action: 'edit', params: [encodedId: absence?.encodedId])}'"/>
    </g:if>
    <btn:backButton goToPreviousLink="true" withPreviousLink="true"/>
</div>

</body>
</html>

