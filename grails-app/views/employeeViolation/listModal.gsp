<g:set var="entities" value="${message(code: 'employeeViolation.entities', default: 'EmployeeViolation List')}"/>
<g:set var="entity" value="${message(code: 'employeeViolation.entity', default: 'EmployeeViolation')}"/>
<g:set var="title" value="${message(code: 'default.list.label', args: [entities], default: 'EmployeeViolation List')}"/>
<el:modal isModalWithDiv="true" id="employeeViolationModal" title="${message(code: 'employeeViolation.label')}"
          preventCloseOutSide="true" width="90%">
    <msg:warning label="${message(code: 'disciplinaryRequest.employeeViolationInfo.label')}"/>
    <div class="clearfix form-actions text-left">
        <btn:buttonGroup>
            <el:modalLink preventCloseOutSide="true" class="btn btn-sm btn-info2 width-135"
                          link="${createLink(controller: 'employeeViolation', action: 'createNewEmployeeViolationModal', params:[employeeId: employeeId])}"
                          label="${message(code: 'default.button.create.label')}">
                <i class="icon-plus"></i>
            </el:modalLink>
        </btn:buttonGroup>
    </div>
    <el:form action="#" name="employeeViolationSearchForm">
        <el:hiddenField name="employee.id" value="${employeeId ?: -1L}"/>
        <el:hiddenField name="disciplinaryCategoryId" value="${disciplinaryCategoryId ?: -1L}"/>
        <el:hiddenField name="excludedIds" value="${params.excludedIds ?: []}"/>
    </el:form>
    <g:render template="/employeeViolation/dataTable"
              model="[title: title, domainColumns: 'DOMAIN_TAB_COLUMNS', viewExtendButtons: 'false', hideTools: true]"/>
    <el:modalButton class="btn  btn-bigger  btn-sm  btn-primary  btn-round"
                    icon="ace-icon icon-plus"
                    onClick="addEmployeeViolations()"
                    message="${g.message(code: "default.button.add.label")}"/>
</el:modal>
<script type="text/javascript">
    gui.dataTable.initialize($('#application-modal-main-content'));
    gui.modal.initialize($('#application-modal-main-content'));
    function addEmployeeViolations() {
        $("#employeeViolationIds").val(_dataTablesCheckBoxValues['employeeViolationTable']);
        getViolationsWithJudgments();
        $('#application-modal-main-content').modal("hide");
    }
</script>