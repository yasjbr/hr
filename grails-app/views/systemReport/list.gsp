<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities" value="${message(code: 'systemReport.entities', default: 'SystemReport List')}"/>
    <g:set var="entity" value="${message(code: 'systemReport.entity', default: 'SystemReport')}"/>
    <g:set var="title"
           value="${message(code: 'default.list.label', args: [entities], default: 'SystemReport List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>



<div id="sidebar2" style="width: 30%" class="sidebar responsive">
    <g:sideMenu navListClass="nav nav-list"/>
    <ul class="nav nav-list">

    <li> <a href="${createLink(controller: 'systemReport',action: 'employeeRank')}">
        <i class="menu-icon icon-chart-bar-1 orange"></i>
        <span class="menu-text">
            <g:message code="systemReport.show.label" args="[message(code:'employeeRank.label')]" default="employeeRank" />
        </span></a><b class="arrow"></b>
    </li>



</ul>
</div>


</body>
</html>