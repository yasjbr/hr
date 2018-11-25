<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'employeeViolation.entity', default: 'Absence List')}"/>
    <g:set var="title" value="${message(code: 'default.show.label', args: [entity], default: 'Absence List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'employeeViolation', action: 'list')}'"/>
    </div></div>
</div>
<el:row/><br/>
<g:render template="/employee/employeeShowWrapper" model="[employee: employeeViolation?.employee]"/>
<el:row/>
<el:row/>
<el:row/>
<g:render template="/employeeViolation/show" model="[employeeViolation: employeeViolation]"/>
<el:row/>

<g:render template="/employeeViolation/trackingInfoWrapper" model="[violation: employeeViolation]"/>


<el:row/>
<div class="clearfix form-actions text-center">
    <g:if test="${employeeViolation?.violationStatus == ps.gov.epsilon.hr.enums.disciplinary.v1.EnumViolationStatus.NEW}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'employeeViolation', action: 'edit', params: [encodedId: employeeViolation?.encodedId])}'"/>
    </g:if>
    <btn:backButton goToPreviousLink="true" withPreviousLink="true"/>
</div>
</body>
</html>

