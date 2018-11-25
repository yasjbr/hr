<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'vacancy.entities', default: 'Vacancy List')}" />
    <g:set var="entity" value="${message(code: 'vacancy.entity', default: 'Vacancy')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'Vacancy List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="vacancyCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'vacancy',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="vacancySearchForm">
            <g:render template="/vacancy/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['vacancyTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('vacancySearchForm');_dataTables['vacancyTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="vacancyTable" searchFormName="vacancySearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="vacancy" spaceBefore="true" hasRow="true" action="filter" serviceName="vacancy">
    <el:dataTableAction controller="vacancy" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show vacancy')}" />
    <el:dataTableAction controller="vacancy" action="edit" showFunction="manageExecuteEditActions" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit vacancy')}" />
    <el:dataTableAction controller="vacancy" action="delete"  showFunction="manageExecuteActions" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete vacancy')}" />
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
    function openAttachmentModal(row) {
        $("#attachmentListModal").attr('parentId', row);
        $("#attachmentListModal").attr('referenceObject',"${referenceObject}" );
        $("#attachmentListModal").attr('parentType', 'normal');
        $("#attachmentListModal").attr('operationType', "${operationType}");
        $("#attachmentListModal").attr('attachmentTypeList',  "${(attachmentTypeList?.encodeAsJSON())}");
        $("#attachmentListModal").attr('callBackFunction', '');
        $("#attachmentListModal").modal('show');
    }


    //to not allow edit the vacancy when state is POSTED
    function manageExecuteEditActions(row) {
        if(row.vacancyStatusValue == "${ps.gov.epsilon.hr.enums.v1.EnumVacancyStatus.POSTED}")
        {
            return false;
        }
        return true;
    }

    //to allow delete for jobRequisitionInstance that requisitionStatusValue is CREATED
    function manageExecuteActions(row) {
        if(row.vacancyStatusValue == "${ps.gov.epsilon.hr.enums.v1.EnumVacancyStatus.NEW}")
        {
            return true;
        }
        return false;
    }
</script>
</body>
</html>