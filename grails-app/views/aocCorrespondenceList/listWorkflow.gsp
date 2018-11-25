<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'aocCorrespondenceList.entities', default: 'AocCorrespondenceList List')}" />
    <g:set var="entity" value="${message(code: 'aocCorrespondenceList.label', default: 'Correspondence List')}" />
    <g:set var="title" value="${message(code: 'aocCorrespondenceList.listWorkflow.label')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="aocCorrespondenceListCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetBody>
        <el:form action="#" name="aocCorrespondenceListSearchForm">
            <g:render template="/aocCorrespondenceList/search" model="[workflowSearch:true]"/>
            <el:formButton functionName="search" onClick="_dataTables['aocCorrespondenceListTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('aocCorrespondenceListSearchForm');_dataTables['aocCorrespondenceListTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="aocCorrespondenceListTable" searchFormName="aocCorrespondenceListSearchForm" domainColumns="DOMAIN_COLUMNS_WORKFLOW"
              dataTableTitle="${title}" hasCheckbox="true" widthClass="col-sm-12" controller="aocCorrespondenceList"
              spaceBefore="true" hasRow="true" action="filterWorkflow" serviceName="aocCorrespondenceList">
     <el:dataTableAction controller="aocCorrespondenceList" action="manageListWorkflow" class="icon-cog"
                         message="${message(code: 'allowanceList.manage.label')}" actionParams="['id']" showFunction="showManageWorkflow"/>
</el:dataTable>



</body>
</html>