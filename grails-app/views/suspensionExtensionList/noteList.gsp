<g:set var="entities"
       value="${message(code: 'suspensionExtensionListEmployeeNote.entities', default: 'suspensionExtensionListEmployeeNote List')}"/>
<g:set var="entity"
       value="${message(code: 'suspensionExtensionListEmployeeNote.entity', default: 'suspensionExtensionListEmployeeNote List')}"/>
<g:set var="dataTableTitle"
       value="${message(code: 'default.list.label', args: [entities], default: 'suspensionExtensionListEmployeeNote List')}"/>


<el:modal isModalWithDiv="true" title="${dataTableTitle}" width="90%">
    <msg:page/>


    <div class="clearfix form-actions text-left">
        <btn:buttonGroup>
            <el:modalLink preventCloseOutSide="true" class="btn btn-sm btn-info2 width-135"
                          link="${createLink(controller: 'suspensionExtensionList', action: 'noteCreate', id: id)}"
                          label="${message(code: 'default.button.create.label')}">
                <i class="icon-plus"></i>
            </el:modalLink>
        </btn:buttonGroup>
    </div>

    <el:form action="#" style="display: none;" name="suspensionExtensionListEmployeeNoteSearchForm">
        <el:hiddenField name="suspensionExtensionListEmployee.id" value="${id}"/>
    </el:form>

    <el:dataTable id="employeeNoteTable"
                  searchFormName="suspensionExtensionListEmployeeNoteSearchForm"
                  dataTableTitle="${dataTableTitle}"
                  hasCheckbox="true"
                  widthClass="col-sm-12"
                  controller="suspensionExtensionListEmployeeNote"
                  spaceBefore="true"
                  hasRow="true"
                  action="filter"
                  serviceName="suspensionExtensionListEmployeeNote">
        <el:dataTableAction controller="suspensionExtensionListEmployeeNote" action="delete" actionParams="encodedId"
                            class="red icon-trash" type="confirm-delete"
                            message="${message(code: 'default.delete.label', args: [entity], default: 'delete suspensionExtensionListEmployeeNote')}"/>
    </el:dataTable>

</el:modal>
<script type="text/javascript">
    gui.dataTable.initialize($('#application-modal-main-content'));
    gui.modal.initialize($('#application-modal-main-content'));
</script>