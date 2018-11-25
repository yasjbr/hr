<g:set var="entities"
       value="${message(code: 'generalListEmployeeNote.entities', default: 'generalListEmployeeNote List')}"/>
<g:set var="entity"
       value="${message(code: 'generalListEmployeeNote.entity', default: 'generalListEmployeeNote List')}"/>
<g:set var="dataTableTitle"
       value="${message(code: 'default.list.label', args: [entities], default: 'generalListEmployeeNote List')}"/>


<el:modal isModalWithDiv="true" title="${dataTableTitle}" width="90%">
    <msg:page/>


    <div class="clearfix form-actions text-left">
        <btn:buttonGroup>
            <el:modalLink preventCloseOutSide="true" class="btn btn-sm btn-info2 width-135"
                          link="${createLink(controller: 'generalList', action: 'noteCreate', id: id)}"
                          label="${message(code: 'default.button.create.label')}">
                <i class="icon-plus"></i>
            </el:modalLink>
        </btn:buttonGroup>
    </div>

    <el:form action="#" style="display: none;" name="generalListEmployeeNoteSearchForm">
        <el:hiddenField name="generalListEmployee.id" value="${id}"/>
    </el:form>

    <el:dataTable id="employeeNoteTable"
                  searchFormName="generalListEmployeeNoteSearchForm"
                  dataTableTitle="${dataTableTitle}"
                  hasCheckbox="true"
                  widthClass="col-sm-12"
                  controller="generalListEmployeeNote"
                  spaceBefore="true"
                  hasRow="true"
                  action="filter"
                  serviceName="generalListEmployeeNote">
        <el:dataTableAction controller="generalListEmployeeNote" action="delete" actionParams="encodedId"
                            class="red icon-trash" type="confirm-delete"
                            message="${message(code: 'default.delete.label', args: [entity], default: 'delete generalListEmployeeNote')}"/>
    </el:dataTable>

</el:modal>
<script type="text/javascript">
    gui.dataTable.initialize($('#application-modal-main-content'));
    gui.modal.initialize($('#application-modal-main-content'));
</script>