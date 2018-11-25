<el:hiddenField name="allowanceList.id" value="${allowanceListId}"/>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="employee" action="autocomplete"
                     name="employee.idReject"
                     label="${message(code: 'allowanceRequest.employee.label', default: 'employee')}"/>

    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="allowanceType"
                     action="autocomplete" name="allowanceType.idReject"
                     label="${message(code: 'allowanceRequest.allowanceType.label', default: 'allowanceType')}"/>
</el:formGroup>


<el:formGroup>
    <el:dateField name="fromEffectiveDateList" size="6" class="" setMinDateFor="toEffectiveDateList"
                  label="${message(code: 'allowanceRequest.fromEffectiveDate.label', default: 'fromDate')}"/>
    <el:dateField name="toEffectiveDateList" size="6" class=""
                  label="${message(code: 'allowanceRequest.toEffectiveDate.label', default: 'fromDate')}"/>
</el:formGroup>
<el:hiddenField name="requestStatusReject"
                value="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED_BY_WORKFLOW}"/>