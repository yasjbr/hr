<div id="listAlert">
    <msg:page/>
</div>
<g:set var="entities"
       value="${message(code: 'allowanceListEmployeeNote.entities', default: 'allowanceListEmployeeNote List')}"/>
<g:set var="dataTableTitle"
       value="${message(code: 'default.list.label', args: [entities], default: 'allowanceListEmployeeNote List')}"/>


<lay:collapseWidget id="allowanceListEmployeeNoteCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="openEmployeeNoteCreateModal()"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="allowanceListEmployeeNoteSearchForm">
            <g:render template="/allowanceListEmployeeNote/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['employeeNoteTable'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('allowanceListEmployeeNoteSearchForm');_dataTables['employeeNoteTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>

<el:dataTable id="employeeNoteTable"
              searchFormName="allowanceListEmployeeNoteSearchForm"
              dataTableTitle="${dataTableTitle}"
              hasCheckbox="true"
              widthClass="col-sm-12"
              controller="allowanceListEmployeeNote"
              spaceBefore="true"
              hasRow="true"
              action="filter"
              serviceName="allowanceListEmployeeNote">
    <el:dataTableAction controller="allowanceListEmployeeNote" action="delete" actionParams="encodedId"
                        class="red icon-trash" type="confirm-delete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete allowanceListEmployeeNote')}"/>
</el:dataTable>

<script type="text/javascript">
    var allowanceListEmployeeId = '';

    function initListFormVariables(allowanceListEmployeeId_p) {
        allowanceListEmployeeId = allowanceListEmployeeId_p;
        $("#allowanceListEmployeeId").val(allowanceListEmployeeId);
        $("#save_allowanceListEmployeeId").val(allowanceListEmployeeId);
        $("#listAlert .alert").html('');
    }

    function openEmployeeNoteCreateModal() {
        _dataTables['employeeNoteTable'].draw();
        $("#noteListFormDiv").toggle();
        $("#employeeNoteCreateFormDiv").toggle();
        $("#listAlert .alert").html('');
    }

</script>