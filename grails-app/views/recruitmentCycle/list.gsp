<%@ page import="grails.converters.JSON" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'recruitmentCycle.entities', default: 'RecruitmentCycle List')}" />
    <g:set var="entity" value="${message(code: 'recruitmentCycle.entity', default: 'RecruitmentCycle')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'RecruitmentCycle List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="recruitmentCycleCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'recruitmentCycle',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="recruitmentCycleSearchForm">
            <g:render template="/recruitmentCycle/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['recruitmentCycleTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('recruitmentCycleSearchForm');_dataTables['recruitmentCycleTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable  id="recruitmentCycleTable" searchFormName="recruitmentCycleSearchForm" dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="recruitmentCycle" spaceBefore="true" hasRow="true" action="filter" serviceName="recruitmentCycle">
    <el:dataTableAction controller="recruitmentCycle" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show recruitmentCycle')}" />

    <el:dataTableAction controller="recruitmentCycle" action="edit" actionParams="encodedId" showFunction="manageExecuteEdit" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit recruitmentCycle')}" />
    <el:dataTableAction controller="recruitmentCycle" action="delete" actionParams="encodedId" showFunction="manageExecuteDelete" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete recruitmentCycle')}" />

    <el:dataTableAction controller="recruitmentCycle" action="changePhase"
                        actionParams="encodedId" showFunction="manageChangePhase"
                        class=" icon-forward-3"
                        message="${message(code:'recruitmentCycle.nextPhase.label',default:'nextPhase recruitmentCycle')}" />


    <el:dataTableAction showFunction="viewJobRequisitionReport"
            functionName="openReport"
            class="red icon-file-pdf"
            type="function"
            message="${message(code:'report.reportFormatPDF.label')} ${message(code:'jobRequisition.civil.label')}" />

    <el:dataTableAction showFunction="viewJobRequisitionReport"
            functionName="openSoldierReport" title="sdfoignsd;fg"
            class="red icon-file-pdf"
            type="function"
            message="${message(code:'report.reportFormatPDF.label')} ${message(code:'jobRequisition.soldier.label')}" />
    <el:dataTableAction
            functionName="openAttachmentModal" accessUrl="${createLink(controller: 'attachment',action: 'filterAttachment')}"
            actionParams="id"
            class="blue icon-attach"
            type="function"
            message="${message(code: 'default.attachment.label')}" />
</el:dataTable>

<g:render template="/attachment/attachmentCreateModal"/>
<g:render template="/attachment/attachmentListModal"/>
<g:render template="/attachment/attachmentPreviewModal"/>
<g:render template="/attachment/attachmentViewModal"/>
<g:render template="/attachment/attachmentEditModal"/>

<script>

    function viewJobRequisitionReport(row) {
        return row.currentPhase == "REVIEWED"
    }
    function openReport(id) {
        window.location.href = "${createLink(controller: 'systemReport',action: 'renderReport')}?reportType=showStatic&recruitmentCycle.id="+id+"&isSoldier=false&_domain=jobRequisition&_format=PDF&_method=getJobRequisitionReportData&_reportName=jobRequisitionReport&_title=${message(code:'jobRequisition.entities')}"
    }
    function openSoldierReport(id) {
        window.location.href = "${createLink(controller: 'systemReport',action: 'renderReport')}?reportType=showStatic&recruitmentCycle.id="+id+"&isSoldier=true&_domain=jobRequisition&_format=PDF&_method=getJobRequisitionReportData&_reportName=jobRequisitionSoldierReport&_title=${message(code:'jobRequisition.entities')}"
    }

    function openAttachmentModal(row) {
        $("#attachmentListModal").attr('parentId', row);
        $("#attachmentListModal").attr('referenceObject',"${referenceObject}" );
        $("#attachmentListModal").attr('parentType', 'normal');
        $("#attachmentListModal").attr('operationType', "${operationType}");
        $("#attachmentListModal").attr('attachmentTypeList',  "${(attachmentTypeList?.encodeAsJSON())}");
        $("#attachmentListModal").attr('callBackFunction', '');
        $("#attachmentListModal").modal('show');
    }

    function manageExecuteDelete(row){
        if(row.currentPhase == "${ps.gov.epsilon.hr.enums.v1.EnumRequisitionAnnouncementStatus.NEW}"){
            return true;
        }
        return false;
    }
    function manageExecuteEdit(row){
        if(row.currentPhase == "${ps.gov.epsilon.hr.enums.v1.EnumRequisitionAnnouncementStatus.NEW}" || row.currentPhase == "${ps.gov.epsilon.hr.enums.v1.EnumRequisitionAnnouncementStatus.OPEN}"){
            return true;
        }
        return false;
    }
    function manageChangePhase(row) {
        if(row.currentPhase == "${ps.gov.epsilon.hr.enums.v1.EnumRequisitionAnnouncementStatus.CLOSED}"){
            return false;
        }
        return true;
    }

</script>

</body>
</html>