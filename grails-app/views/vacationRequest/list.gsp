<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities" value="${message(code: 'vacationRequest.entities', default: 'vacationRequest List')}"/>
    <g:set var="entity" value="${message(code: 'vacationRequest.entity', default: 'vacationRequest')}"/>
    <g:set var="title"
           value="${message(code: 'default.list.label', args: [entities], default: 'vacationRequest List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
<lay:collapseWidget id="vacationRequestCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton
                    onClick="window.location.href='${createLink(controller: 'vacationRequest', action: 'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="vacationRequestSearchForm">
            <g:render template="/vacationRequest/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['vacationRequestTable'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('vacationRequestSearchForm');_dataTables['vacationRequestTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>

<g:render template="/vacationRequest/dataTable" model="[title: title]"/>

<g:render template="/request/script"/>

</body>
</html>