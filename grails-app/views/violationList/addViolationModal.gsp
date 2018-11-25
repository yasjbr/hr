<el:modal isModalWithDiv="true"  id="employeeViolationModal"
          title="${message(code:'employeeViolation.label')}"
          hideCancel="true"
          preventCloseOutSide="true" width="80%">
    <msg:modal/>
    <msg:page/>
    <g:set var="entities" value="${message(code: 'employeeViolation.entities', default: 'Violation List')}" />

    <lay:collapseWidget id="employeeViolationCollapseWidget" icon="icon-search"
                        title="${message(code:'default.search.label',args:[entities])}"
                        size="12" collapsed="true" data-toggle="collapse" >
        <lay:widgetBody>
            <el:form action="#" name="employeeViolationTableSearchForm">
                <g:render template="/employeeViolation/search" model="[hideStatusSearch:true]"/>
                <el:formButton functionName="search" onClick="_dataTables['employeeViolationTable'].draw()"/>
                <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('employeeViolationTableSearchForm');_dataTables['employeeViolationTable'].draw();"/>
            </el:form>
        </lay:widgetBody>
    </lay:collapseWidget>


    <el:dataTable id="employeeViolationTable" searchFormName="employeeViolationTableSearchForm"
                  hasCheckbox="true" widthClass="col-sm-12" controller="employeeViolation"
                  spaceBefore="true" hasRow="true" action="filterViolationForList"
                  serviceName="employeeViolation" viewExtendButtons="false" domainColumns="DOMAIN_COLUMNS">
    </el:dataTable>

    <el:validatableForm callBackFunction="callBackFunction" name="addViolationToListForm" controller="violationList" action="addViolationToList" >
        <el:hiddenField name="violationTableCheckBoxValues" value=""/>
        <el:hiddenField name="violationListId" value="${violationList.id}" />
        <el:formButton functionName="addButton" isSubmit="true" withClose="true" class="center" onclick="callBackBeforeSendFunction()"/>
        <el:formButton functionName="close" onClick="callBackFunction()"/>
    </el:validatableForm>
</el:modal>

<script type="text/javascript">
    gui.dataTable.initialize($('#application-modal-main-content'));
    function callBackBeforeSendFunction() {
        $("#violationTableCheckBoxValues").val(_dataTablesCheckBoxValues['employeeViolationTable']);
    }
    function callBackFunction(json) {
            _dataTables['employeeViolationTableInViolationList'].draw();
            $('#application-modal-main-content').modal("hide");
    }
</script>
