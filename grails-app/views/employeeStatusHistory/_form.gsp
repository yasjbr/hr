<g:render template="/employee/wrapper" model="[id:(employeeCallBackId?:'employeeId'),
                                               name:'employee.id',
                                               isHiddenInfo:params.isHiddenPersonInfo,
                                               bean:employeeStatusHistory?.employee,
                                               isDisabled:isEmployeeDisabled]" />


<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired"
                     controller="employeeStatus" action="autocomplete" name="employeeStatus.id"
                     label="${message(code:'employeeStatusHistory.employeeStatus.label',default:'employeeStatus')}"
                     values="${[[employeeStatusHistory?.employeeStatus?.id,
                                 employeeStatusHistory?.employeeStatus?.descriptionInfo?.localName]]}" />
</el:formGroup>

<el:formGroup>
    <el:dateField name="fromDate"  size="8" class=" isRequired"
                  label="${message(code:'employeeStatusHistory.fromDate.label',default:'fromDate')}"
                  value="${employeeStatusHistory?.fromDate}" />
</el:formGroup>

<el:formGroup>
    <el:dateField name="toDate"  size="8" class=" "
                  label="${message(code:'employeeStatusHistory.toDate.label',default:'toDate')}"
                  value="${employeeStatusHistory?.toDate}" />
</el:formGroup>
