<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'disciplinaryRequest.entities', default: 'DisciplinaryRequest List')}" />
    <g:set var="entity" value="${message(code: 'disciplinaryRequest.entity', default: 'DisciplinaryRequest')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'DisciplinaryRequest List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="disciplinaryRequestCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'disciplinaryRequest',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="disciplinaryRequestSearchForm">
            <g:render template="/disciplinaryRequest/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['disciplinaryRequestTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('disciplinaryRequestSearchForm');_dataTables['disciplinaryRequestTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>

<g:render template="/disciplinaryRequest/dataTable" model="[title:title]"/>

<g:render template="/request/script"/>

<script>
    function createPetitionAction(row) {
        if(row.status == "${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED}"){
            return true
        }else{
            false
        }
    }
    function viewPetitionAction(row) {
        if((row.status == "${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.CANCELED}") || (row.status == "${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.ADD_PETITION_REQUEST}")){
            return true
        }else{
            false
        }
    }
</script>

</body>
</html>