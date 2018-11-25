<g:render template="/employee/wrapper" model="[id:(employeeCallBackId?:'employeeId'),
                                               name:'employee.id',
                                               isHiddenInfo:params.isHiddenPersonInfo,
                                               bean:joinedEmployeeOperationalTasks?.employee,
                                               isDisabled:isEmployeeDisabled]" />
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired"
                     paramsGenerateFunction="operationalTaskParams"
                     controller="operationalTask" action="autocomplete" name="operationalTask.id"
                     label="${message(code:'joinedEmployeeOperationalTasks.operationalTask.label',default:'operationalTask')}"
                     values="${[[joinedEmployeeOperationalTasks?.operationalTask?.id,
                                 joinedEmployeeOperationalTasks?.operationalTask?.descriptionInfo?.localName]]}" />
</el:formGroup>

<el:formGroup>
    <el:dateField name="fromDate"  size="8" class=" isRequired"
                  label="${message(code:'joinedEmployeeOperationalTasks.fromDate.label',default:'fromDate')}"
                  value="${joinedEmployeeOperationalTasks?.fromDate}" />
</el:formGroup>

<el:formGroup>
    <el:dateField name="toDate"  size="8" class=" "
                  label="${message(code:'joinedEmployeeOperationalTasks.toDate.label',default:'toDate')}"
                  value="${joinedEmployeeOperationalTasks?.toDate}" />
</el:formGroup>

<script>
    function operationalTaskParams() {
        var employeeId = $('#employeeId').val();
        return {
            "excludeCurrentOperationTask":true,
            "currentOperationTaskId":"${joinedEmployeeOperationalTasks?.operationalTask?.id}",
            "isEdit":"${joinedEmployeeOperationalTasks?.id != null}",
            'employee.id':employeeId
        }
    }
</script>