<div id="listAlert">
    <msg:page/>
</div>
<g:set var="entities"
       value="${message(code: 'suspensionListEmployeeNote.entities', default: 'suspensionListEmployeeNote List')}"/>
<g:set var="dataTableTitle"
       value="${message(code: 'default.list.label', args: [entities], default: 'suspensionListEmployeeNote List')}"/>


<lay:collapseWidget id="suspensionListEmployeeNoteCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="openEmployeeNoteCreateModal()"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="suspensionListEmployeeNoteSearchForm">
            <g:render template="/suspensionListEmployeeNote/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['employeeNoteTable'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('suspensionListEmployeeNoteSearchForm');_dataTables['employeeNoteTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>

<el:dataTable id="employeeNoteTable"
              searchFormName="suspensionListEmployeeNoteSearchForm"
              dataTableTitle="${dataTableTitle}"
              hasCheckbox="true"
              widthClass="col-sm-12"
              controller="suspensionListEmployeeNote"
              spaceBefore="true"
              hasRow="true"
              action="filter"
              serviceName="suspensionListEmployeeNote">
    <el:dataTableAction controller="suspensionListEmployeeNote" action="delete" actionParams="encodedId"
                        class="red icon-trash" type="confirm-delete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete suspensionListEmployeeNote')}"/>
</el:dataTable>

<script type="text/javascript">
    var suspensionListEmployeeId = '';

    function initListFormVariables(suspensionListEmployeeId_p) {
        suspensionListEmployeeId = suspensionListEmployeeId_p;
        $("#suspensionListEmployeeId").val(suspensionListEmployeeId);
        $("#save_suspensionListEmployeeId").val(suspensionListEmployeeId);
        $("#listAlert .alert").html('');
    }

    function openEmployeeNoteCreateModal() {
        _dataTables['employeeNoteTable'].draw();
        $("#noteListFormDiv").toggle();
        $("#employeeNoteCreateFormDiv").toggle();
        $("#listAlert .alert").html('');
    }

</script>