<g:set var="entities"
       value="${message(code: 'suspensionExtensionRequest.entities', default: 'suspensionExtensionRequest List')}"/>

<g:set var="entity"
       value="${message(code: 'suspensionExtensionRequest.entity', default: 'suspensionExtensionRequest List')}"/>


<g:set var="dataTableTitle"
       value="${message(code: 'default.list.label', args: [entities], default: 'suspensionExtensionRequest List')}"/>


<el:modal isModalWithDiv="true" title="${dataTableTitle}" width="90%">
    <msg:page/>


    <div class="clearfix form-actions text-left">
        <btn:buttonGroup>
            <el:modalLink preventCloseOutSide="true" class="btn btn-sm btn-info2 width-135"
                          link="${createLink(controller: 'suspensionRequest', action: 'extensionRequestCreate', id: id)}"
                          label="${message(code: 'default.button.create.label')}">
                <i class="icon-plus"></i>
            </el:modalLink>
        </btn:buttonGroup>
    </div>

    <el:form action="#" style="display: none;" name="suspensionExtensionRequestSearchForm">
        <el:hiddenField name="suspensionRequest.id" value="${id}"/>
    </el:form>


    <el:dataTable id="employeeNoteTable"
                  searchFormName="suspensionExtensionRequestSearchForm"
                  dataTableTitle="${dataTableTitle}"
                  hasCheckbox="false"
                  widthClass="col-sm-12"
                  controller="suspensionExtensionRequest"
                  spaceBefore="true"
                  hasRow="true"
                  action="filter"
                  serviceName="suspensionExtensionRequest">

        <el:dataTableAction
                preventCloseOutSide="true"
                actionParams="encodedId" controller="suspensionRequest" action="extensionRequestShow"
                class="green icon-eye"
                message="${message(code: 'default.edit.label', args: [entity], default: 'Show suspensionRequest')}"
                type="modal-ajax"/>

        <el:dataTableAction
                preventCloseOutSide="true"
                actionParams="encodedId" controller="suspensionRequest" action="extensionRequestEdit"
                class="blue icon-pencil"  showFunction="manageExecuteDelete"
                message="${message(code: 'default.edit.label', args: [entity], default: 'edit suspensionRequest')}"
                type="modal-ajax"/>





        <el:dataTableAction controller="suspensionExtensionRequest" action="delete" actionParams="encodedId"
                            class="red icon-trash" type="confirm-delete" showFunction="manageExecuteDelete"
                            message="${message(code: 'default.delete.label', args: [entity], default: 'delete suspensionExtensionRequest')}"/>
    </el:dataTable>

</el:modal>
<script type="text/javascript">
    gui.dataTable.initialize($('#application-modal-main-content'));
    gui.modal.initialize($('#application-modal-main-content'));

    /**
     * manage delete request
     * @param row
     * @returns {boolean}
     */
    function manageExecuteDelete(row) {
        return row.requestStatus == "${g.message(code: 'EnumRequestStatus.CREATED')}";
    }


    /**
     * manage edit request
     * @param row
     * @returns {boolean}
     */
    function manageExecuteEdit(row) {
        if (row.requestStatus == "${g.message(code: 'EnumRequestStatus.CREATED')}") {
            return true;
        }
        return false;
    }


</script>