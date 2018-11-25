<div id="listAlert">
    <msg:page/>
</div>
<g:set var="entities"
       value="${message(code: 'vacationListEmployeeNote.entities', default: 'vacationListEmployeeNote List')}"/>
<g:set var="dataTableTitle"
       value="${message(code: 'default.list.label', args: [entities], default: 'vacationListEmployeeNote List')}"/>


<lay:collapseWidget id="vacationListEmployeeNoteCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="openEmployeeNoteCreateModal()"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="vacationListEmployeeNoteSearchForm">
            <g:render template="/vacationListEmployeeNote/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['employeeNoteTable'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('vacationListEmployeeNoteSearchForm');_dataTables['employeeNoteTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>

<el:dataTable id="employeeNoteTable"
              searchFormName="vacationListEmployeeNoteSearchForm"
              dataTableTitle="${dataTableTitle}"
              hasCheckbox="true"
              widthClass="col-sm-12"
              controller="vacationListEmployeeNote"
              spaceBefore="true"
              hasRow="true"
              action="filter"
              serviceName="vacationListEmployeeNote">
    <el:dataTableAction controller="vacationListEmployeeNote" action="delete" actionParams="encodedId"
                        class="red icon-trash" type="confirm-delete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete vacationListEmployeeNote')}"/>
</el:dataTable>

<script type="text/javascript">
    var vacationListEmployeeId = '';

    function initListFormVariables(vacationListEmployeeId_p) {
        vacationListEmployeeId = vacationListEmployeeId_p;
        $("#vacationListEmployeeId").val(vacationListEmployeeId);
        $("#save_vacationListEmployeeId").val(vacationListEmployeeId);
        $("#listAlert .alert").html('');
    }

    function openEmployeeNoteCreateModal() {
        _dataTables['employeeNoteTable'].draw();
        $("#noteListFormDiv").toggle();
        $("#employeeNoteCreateFormDiv").toggle();
        $("#listAlert .alert").html('');
    }

</script>