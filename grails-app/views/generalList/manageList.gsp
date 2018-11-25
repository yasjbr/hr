<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities"
           value="${message(code: 'generalList.entities', default: 'employeeRequest List')}"/>
    <g:set var="entity"
           value="${message(code: 'generalList.entity', default: 'employeeRequest ')}"/>
    <g:set var="title" value="${message(code: 'generalList.label', args: [entities])}"/>
    <title>${title}</title>
</head>

<body>
<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'generalList', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>
<el:row/>
<br/>
<lay:showWidget size="6">
    <lay:showElement value="${generalList?.code}" type="String"
                     label="${message(code: 'generalList.code.label', default: 'code')}"/>
    <lay:showElement value="${generalList?.name}" type="String"
                     label="${message(code: 'generalList.name.label', default: 'name')}"/>
</lay:showWidget>
<lay:showWidget size="6">
    <lay:showElement value="${generalList?.currentStatus?.correspondenceListStatus}" type="enum"
                     label="${message(code: 'generalList.currentStatus.correspondenceListStatus.label', default: 'correspondenceListStatus')}"/>
    <lay:showElement value="${generalList?.transientData?.organizationName}" type="string"
                     label="${message(code: 'generalList.coreOrganizationId.label', default: 'coreOrganizationId')}"/>
</lay:showWidget>
<el:row/>
<br/>
<lay:collapseWidget id="employeeCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetBody>
        <el:form action="#" name="employeeRequestSearchForm"
                 id="employeeRequestSearchForm">
            <el:hiddenField id="generalList.id" name="generalList.id"
                            value="${generalList.id}"/>
            <g:render template="/generalListEmployee/search" model="[:]"/>
            <el:formButton functionName="search"
                           onClick="_dataTables['employeeTableInGeneralList'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('employeeRequestSearchForm');_dataTables['employeeTableInGeneralList'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:row/>

<el:dataTable id="employeeTableInGeneralList"
              searchFormName="employeeRequestSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true"
              widthClass="col-sm-12"
              controller="generalListEmployee"
              spaceBefore="true"
              hasRow="true" rowCallbackFunction="manageRow"
              action="filter"
              serviceName="generalListEmployee"
              domainColumns="DOMAIN_COLUMNS">

    <el:dataTableAction controller="employee" action="show" class="green icon-eye"
                        actionParams="emlpoyeeEncodedId"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show employeeRequest')}"/>


    <el:dataTableAction controller="generalListEmployee" action="delete"
                        showFunction="manageExecuteActions" class="red icon-cancel" type="confirm-delete"
                        actionParams="encodedId"
                        message="${message(code: 'list.removeRecord.label', args: [entity], default: 'delete generalListEmployee record')}"/>


%{--show modal with note details--}%
    <el:dataTableAction controller="generalList" preventCloseOutSide="true"
                        actionParams="id" action="noteList"
                        class="black icon-info-4" type="modal-ajax"
                        message="${message(code: 'generalList.noteList.label')}"/>

</el:dataTable>

<br/><br/>

<div class="clearfix form-actions text-center" style="background:gainsboro;">

    <g:if test="${generalList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <el:modalLink
                link="${createLink(controller: 'generalList', action: 'addRequestModal', params: [encodedId: generalList?.encodedId])}"
                preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                label="">
            <i class="ace-icon icon-plus"></i>${message(code: 'list.addRequest.label')}
        </el:modalLink>
    </g:if>

    <g:if test="${generalList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                      link="${createLink(controller: 'generalList', action: 'sendListModal', params: [encodedId: generalList?.encodedId])}"
                      label="">
            <i class="ace-icon icon-paper-plane"></i>${message(code: 'list.sendList.label')}
        </el:modalLink>
    </g:if>



    <btn:attachmentButton onClick="openAttachmentModal('${generalList?.id}')"/>

    <report:staticViewList fileName="correspondenceTemplate"
                           withDataTable="employeeTableInGeneralList"
                           searchFromName="employeeRequestSearchForm"
                           domain="generalListEmployee" method="getReportData"
                           columns="LIST_DOMAIN_COLUMNS" format="pdf"
                           title="${message(code: 'generalList.entity')}"/>

</div>

<g:render template="/attachment/attachmentCreateModal"/>
<g:render template="/attachment/attachmentListModal"/>
<g:render template="/attachment/attachmentPreviewModal"/>
<g:render template="/attachment/attachmentViewModal"/>
<g:render template="/attachment/attachmentEditModal"/>

<script>


    var testRowList = [];
    var counter = 0;

    function openAttachmentModal(row) {
        $("#attachmentListModal").attr('parentId', row);
        $("#attachmentListModal").attr('referenceObject', "${referenceObject}");
        $("#attachmentListModal").attr('parentType', 'normal');
        $("#attachmentListModal").attr('operationType', "${operationType}");
        $("#attachmentListModal").attr('attachmentTypeList', "${(attachmentTypeList?.encodeAsJSON())}");
        $("#attachmentListModal").attr('callBackFunction', '');
        $("#attachmentListModal").modal('show');
    }

    function showTheRecord(showLink) {
        window.location.href = showLink;

    }

    /*allow delete when list Status is CREATED  */
    function manageExecuteActions(row) {
        testRowList.push(row);
        return (${generalList?.currentStatus?.correspondenceListStatus==ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED});
    }

    function manageRow(row) {
        var testRow = testRowList[counter++];
        var booleanListStatus = "${generalList?.currentStatus?.correspondenceListStatus==ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.PARTIALLY_CLOSED}";
        var booleanRowStatus = testRow.recordStatus != "${g.message(code: 'EnumListRecordStatus.NEW')}";
        if (booleanListStatus && booleanRowStatus) {
            row.deleteCell(0);
            var x = row.insertCell(0);
            x.innerHTML = " ";
        }
    }
</script>
</body>
</html>


