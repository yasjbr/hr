<el:modal isModalWithDiv="true" id="eligibleEmployeeModal"
          title="${message(code: 'promotionList.label')}"
          hideCancel="true"
          preventCloseOutSide="true" width="60%">
    <msg:modal/>
    <g:set var="employeeEntities" value="${message(code: 'employee.entities', default: 'Employee List')}"/>

    <lay:collapseWidget id="employeeCollapseWidget" icon="icon-search"
                        title="${message(code: 'default.search.label', args: [employeeEntities])}"
                        size="12" collapsed="true" data-toggle="collapse">
        <lay:widgetBody>
            <el:form action="#" name="eligibleEmployeeSearchForm">
                <el:hiddenField name="eligibleEmployeeList" value="true"/>
                <el:hiddenField name="employeePromotionList" value="true"/>
                <g:render template="/employee/searchInModal" model="[:]"/>
                <el:formButton functionName="search" onClick="_dataTables['eligibleEmployeeTable'].draw()"/>
                <el:formButton functionName="clear"
                               onClick="gui.formValidatable.resetForm('eligibleEmployeeSearchForm');_dataTables['eligibleEmployeeTable'].draw();"/>
            </el:form>
        </lay:widgetBody>
    </lay:collapseWidget>

    <el:dataTable id="eligibleEmployeeTable" searchFormName="eligibleEmployeeSearchForm"
                  hasCheckbox="true" widthClass="col-sm-12" controller="employee"
                  spaceBefore="true" hasRow="true" action="filterEmployeeForModal"
                  serviceName="employee" viewExtendButtons="false" domainColumns="DOMAIN_COLUMNS_MODAL">

    </el:dataTable>

    <el:validatableForm
            callBackFunction="callBackFunction"
            name="addPromotionToListForm"
            controller="promotionList"
            action="addEmployeeToList">

        <el:hiddenField name="checked_employeeIdsList" value=""/>
        <el:hiddenField name="eligible" value="true"/>
        <el:hiddenField name="promotionListId" value="${promotionList.id}"/>
        <el:formButton functionName="addButton" isSubmit="true" withClose="true" class="center" onclick="callBackBeforeSendFunction()"/>
        <el:formButton functionName="close" onClick="callBackFunction()"/>
    </el:validatableForm>
</el:modal>

<script type="text/javascript">
    gui.dataTable.initialize($('#application-modal-main-content'));

    function callBackBeforeSendFunction() {
        $("#checked_employeeIdsList").val(_dataTablesCheckBoxValues['eligibleEmployeeTable']);
        return true;
    }

    function callBackFunction(json) {
        _dataTables['promotionListEmployeeTableInPromotionList'].draw();
        $('#application-modal-main-content').modal("hide");
    }
</script>
