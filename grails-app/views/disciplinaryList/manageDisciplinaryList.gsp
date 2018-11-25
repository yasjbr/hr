<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities"
           value="${message(code: 'disciplinaryList.entities', default: 'disciplinaryRecordJudgment List')}"/>
    <g:set var="entity" value="${message(code: 'disciplinaryList.entity', default: 'disciplinaryRequest')}"/>
    <g:set var="entityRequest" value="${message(code: 'disciplinaryList.disciplinaryRequest.label', default: 'disciplinaryRequest')}"/>
    <g:set var="title" value="${message(code: 'disciplinaryList.manage.label')}"/>
    <title>${title}</title>
</head>

<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar">
        <div data-toggle="" class="btn-group btn-overlap btn-corner">
            <btn:listButton
                    onClick="window.location.href='${createLink(controller: 'disciplinaryList', action: 'list')}'"/>
        </div>
    </div>
</div>
<el:row/>
<el:row/>
<el:row/>
<br/>
<lay:showWidget size="6">
    <lay:showElement value="${disciplinaryList?.code}" type="String"
                     label="${message(code: 'disciplinaryList.code.label', default: 'code')}"/>
    <lay:showElement value="${disciplinaryList?.name}" type="String"
                     label="${message(code: 'disciplinaryList.name.label', default: 'name')}"/>
</lay:showWidget>
<lay:showWidget size="6">
    <lay:showElement value="${disciplinaryList?.currentStatus?.correspondenceListStatus}" type="enum"
                     label="${message(code: 'disciplinaryList.currentStatus.correspondenceListStatus.label', default: 'correspondenceListStatus')}"/>
    <lay:showElement value="${disciplinaryList?.receivingParty}" type="enum"
                     label="${message(code: 'disciplinaryList.receivingParty.label', default: 'receivingParty')}"/>
</lay:showWidget>
<el:row/>
<br/>



<lay:collapseWidget id="disciplinaryRecordJudgmentCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entityRequest])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetBody>
        <el:form action="#" name="disciplinaryRecordJudgmentSearchForm">
            <el:hiddenField name="disciplinaryRecordsList.id" value="${disciplinaryList.id}"/>
            <el:hiddenField name="domainColumns" value="DOMAIN_LIST_COLUMNS"/>
            <g:render template="/disciplinaryList/searchList" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['disciplinaryRecordJudgmentTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('disciplinaryRecordJudgmentSearchForm');_dataTables['disciplinaryRecordJudgmentTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>



<el:dataTable id="disciplinaryRecordJudgmentTable" searchFormName="disciplinaryRecordJudgmentSearchForm"
              dataTableTitle="${message(code: 'default.list.label', args: [entityRequest])}" domainColumns="DOMAIN_LIST_COLUMNS"
              hasCheckbox="true" widthClass="col-sm-12" controller="disciplinaryRecordJudgment"
              spaceBefore="true" hasRow="true" action="filter" serviceName="disciplinaryRecordJudgment">


    <el:dataTableAction controller="disciplinaryRecordJudgment" action="show" actionParams="encodedId" class="green icon-eye"
                        message="${message(code:'default.show.label',args:[entity],default:'show disciplinaryRecordJudgment')}" />


    <el:dataTableAction controller="disciplinaryRecordJudgment" action="delete" showFunction="manageDeleteActions" actionParams="encodedId" class="red icon-cancel"
                        type="confirm-delete" message="${message(code:'default.delete.label',
            args:[entity],default:'delete disciplinaryRecordJudgment')}" />



</el:dataTable>




<el:row />

<div class="clearfix form-actions text-center" style="background:gainsboro;">


    <g:if test="${disciplinaryList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <el:modalLink id="addDisciplinaryRecordJudgmentButton"
                link="${createLink(controller: 'disciplinaryList', action: 'addDisciplinaryRecordJudgmentModal', id: disciplinaryList?.encodedId)}"
                preventCloseOutSide="true" class=" btn btn-sm btn-primary" label="">
            <i class="ace-icon icon-plus"></i>
            ${message(code: 'disciplinaryList.addDisciplinaryRecordJudgment.label')}
        </el:modalLink>
    </g:if>

    <g:if test="${disciplinaryList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <el:modalLink id="sendListButton" preventCloseOutSide="true" class="btn btn-sm btn-primary"
                      link="${createLink(controller: 'disciplinaryList', action: 'sendListModal', id: disciplinaryList?.encodedId)}"
                      label="">
            <i class="ace-icon icon-paper-plane"></i>${message(code: 'list.sendList.label')}
        </el:modalLink>
    </g:if>

    <btn:attachmentButton onClick="openAttachmentModal('${disciplinaryList?.id}')" />


    <report:staticViewList fileName="correspondenceTemplate"  withDataTable="disciplinaryRecordJudgmentTable"
                           searchFromName="disciplinaryRecordJudgmentSearchForm"
                     domain="disciplinaryRecordJudgment" method="getReportData"
                     columns="DOMAIN_LIST_COLUMNS"  format="pdf" title="${message(code: 'disciplinaryList.entity')}"  />

</div>

<g:render template="/attachment/attachmentCreateModal"/>
<g:render template="/attachment/attachmentListModal"/>
<g:render template="/attachment/attachmentPreviewModal"/>
<g:render template="/attachment/attachmentViewModal"/>
<g:render template="/attachment/attachmentEditModal"/>



<script>

    function openAttachmentModal(row) {
        $("#attachmentListModal").attr('parentId', row);
        $("#attachmentListModal").attr('referenceObject', "${referenceObject}");
        $("#attachmentListModal").attr('parentType', 'normal');
        $("#attachmentListModal").attr('operationType', "${operationType}");
        $("#attachmentListModal").attr('attachmentTypeList', "${(attachmentTypeList?.encodeAsJSON())}");
        $("#attachmentListModal").attr('callBackFunction', '');
        $("#attachmentListModal").modal('show');
    }

    function manageDeleteActions(row) {
        if (row.status == "${ps.gov.epsilon.hr.enums.disciplinary.v1.EnumJudgmentStatus.ADD_TO_LIST}") {
            return true;
        }
        return false;
    }

</script>

</body>
</html>


