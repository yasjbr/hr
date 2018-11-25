<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities"
           value="${message(code: 'employeeViolation.entities', default: 'employeeViolation List')}"/>
    <g:set var="entity" value="${message(code: 'employeeViolation.entity', default: 'employeeViolation')}"/>
    <g:set var="title" value="${message(code: 'violationList.label', args: [entities])}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'violationList', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>
<el:row/>
<br/>
<lay:showWidget size="6">
    <lay:showElement value="${violationList?.code}" type="String"
                     label="${message(code: 'violationList.code.label', default: 'code')}"/>
    <lay:showElement value="${violationList?.name}" type="String"
                     label="${message(code: 'violationList.name.label', default: 'name')}"/>
</lay:showWidget>
<lay:showWidget size="6">
    <lay:showElement value="${violationList?.currentStatus?.correspondenceListStatus}" type="enum"
                     label="${message(code: 'violationList.currentStatus.correspondenceListStatus.label', default: 'correspondenceListStatus')}"/>
    <lay:showElement value="${violationList?.receivingParty}" type="enum"
                     label="${message(code: 'violationList.receivingParty.label', default: 'receivingParty')}"/>
</lay:showWidget>
<el:row/>
<br/>
<lay:collapseWidget id="dispatchCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetBody>
        <el:form action="#" name="employeeViolationSearchForm"
                 id="employeeViolationSearchForm">
            <el:hiddenField id="violationList.id" name="violationList.id" value="${violationList.id}"/>
            <g:render template="/violationListEmployee/search"  />
            <el:formButton functionName="search"
                           onClick="_dataTables['employeeViolationTableInViolationList'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('employeeViolationSearchForm');_dataTables['employeeViolationTableInViolationList'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:row/>
<el:dataTable id="employeeViolationTableInViolationList" searchFormName="employeeViolationSearchForm"
              dataTableTitle="${message(code: 'default.list.label', args: [entities])}"
              hasCheckbox="true"
              widthClass="col-sm-12"
              controller="violationListEmployee"
              spaceBefore="true"
              hasRow="true"
              action="filter"
              serviceName="violationListEmployee"
              domainColumns="DOMAIN_COLUMNS">
    <el:dataTableAction controller="employeeViolation" action="show" class="green icon-eye"
                        actionParams="employeeViolationEncodedId"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show employeeViolation')}"/>
    <el:dataTableAction controller="violationListEmployee" action="delete"
                        showFunction="manageExecuteActions" class="red icon-cancel" type="confirm-delete"
                        actionParams="encodedId"
                        message="${message(code: 'list.removeRecord.label', args: [entity], default: 'delete employeeViolation record')}"/>
</el:dataTable>

<br/><br/>

<div class="clearfix form-actions text-center" style="background:gainsboro;">

    <g:if test="${violationList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <el:modalLink
                link="${createLink(controller: 'violationList', action: 'addViolationModal', id: violationList?.encodedId)}"
                preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                label="">
            <i class="ace-icon icon-plus"></i>${message(code: 'list.addRequest.label')}
        </el:modalLink>
    </g:if>

    <g:if test="${violationList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                      link="${createLink(controller: 'violationList', action: 'sendListModal', id: violationList?.encodedId)}"
                      label="">
            <i class="ace-icon icon-paper-plane"></i>${message(code: 'list.sendList.label')}
        </el:modalLink>
    </g:if>
    <btn:attachmentButton onClick="openAttachmentModal('${violationList?.id}')" />

    <report:staticViewList fileName="correspondenceTemplate"  withDataTable="employeeViolationTableInViolationList"
                           searchFromName="employeeViolationSearchForm"
                           domain="violationListEmployee" method="getReportData"
                           columns="DOMAIN_COLUMNS"  format="pdf" title="${message(code: 'violationList.entity')}"  />

</div>

<g:render template="/attachment/attachmentCreateModal"/>
<g:render template="/attachment/attachmentListModal"/>
<g:render template="/attachment/attachmentPreviewModal"/>
<g:render template="/attachment/attachmentViewModal"/>
<g:render template="/attachment/attachmentEditModal"/>

<script>
    function openAttachmentModal(row) {
        $("#attachmentListModal").attr('parentId', row);
        $("#attachmentListModal").attr('referenceObject',"${referenceObject}" );
        $("#attachmentListModal").attr('parentType', 'normal');
        $("#attachmentListModal").attr('operationType', "${operationType}");
        $("#attachmentListModal").attr('attachmentTypeList',  "${(attachmentTypeList?.encodeAsJSON())}");
        $("#attachmentListModal").attr('callBackFunction', '');
        $("#attachmentListModal").modal('show');
    }

    /*allow delete when recordStatus is NEW  */
    function manageExecuteActions(row) {
        if (${violationList?.currentStatus?.correspondenceListStatus==ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}) {
            return true;
        }
        return false;
    }
</script>
</body>
</html>


