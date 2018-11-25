<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'employeeSalaryInfo.entity', default: 'EmployeeSalaryInfo List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'EmployeeSalaryInfo List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'employeeSalaryInfo',action:'list')}'"/>
    </div></div>
</div>

<el:row/><br/>
<g:render template="/employee/employeeShowWrapper" model="[employee: employeeSalaryInfo?.employee]"/>
<el:row/>
<g:render template="show" model="[employeeSalaryInfo:employeeSalaryInfo]" />


<div class="clearfix form-actions text-center">
    <btn:backButton goToPreviousLink="true" withPreviousLink="true"/>
</div>

</body>
</html>