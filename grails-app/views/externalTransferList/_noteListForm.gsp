<div id="listAlert">
    <msg:page/>
</div>
<g:set var="entities"
       value="${message(code: 'externalTransferListEmployeeNote.entities', default: 'externalTransferListEmployeeNote List')}"/>
<g:set var="dataTableTitle"
       value="${message(code: 'default.list.label', args: [entities], default: 'externalTransferListEmployeeNote List')}"/>


<lay:collapseWidget id="externalTransferListEmployeeNoteCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="openEmployeeNoteCreateModal()"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="externalTransferListEmployeeNoteSearchForm">
            <g:render template="/externalTransferListEmployeeNote/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['employeeNoteTable'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('externalTransferListEmployeeNoteSearchForm');_dataTables['employeeNoteTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>

<el:dataTable id="employeeNoteTable"
              searchFormName="externalTransferListEmployeeNoteSearchForm"
              dataTableTitle="${dataTableTitle}"
              hasCheckbox="true"
              widthClass="col-sm-12"
              controller="externalTransferListEmployeeNote"
              spaceBefore="true"
              hasRow="true"
              action="filter"
              serviceName="externalTransferListEmployeeNote">
    <el:dataTableAction controller="externalTransferListEmployeeNote" action="delete" actionParams="encodedId"
                        class="red icon-trash" type="confirm-delete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete externalTransferListEmployeeNote')}"/>
</el:dataTable>

<script type="text/javascript">
    var externalTransferListEmployeeId = '';

    function initListFormVariables(externalTransferListEmployeeId_p) {
        externalTransferListEmployeeId = externalTransferListEmployeeId_p;
        $("#externalTransferListEmployeeId").val(externalTransferListEmployeeId);
        $("#save_externalTransferListEmployeeId").val(externalTransferListEmployeeId);
        $("#listAlert .alert").html('');
    }

    function openEmployeeNoteCreateModal() {
        _dataTables['employeeNoteTable'].draw();
        $("#noteListFormDiv").toggle();
        $("#employeeNoteCreateFormDiv").toggle();
        $("#listAlert .alert").html('');
    }

</script>