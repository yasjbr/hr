<el:hiddenField name="dispatchListIdReject" value="${dispatchListId}"/>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="employee" action="autocomplete"
                     name="employee.idReject"
                     label="${message(code: 'dispatchRequest.employee.label', default: 'employee')}"/>

    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="dispatchType"
                     action="autocomplete" name="dispatchType.idReject"
                     label="${message(code: 'dispatchRequest.dispatchType.label', default: 'dispatchType')}"/>
</el:formGroup>


<el:formGroup>
    <el:dateField name="fromEffectiveDateList" size="6" class="" setMinDateFor="toEffectiveDateList"
                  label="${message(code: 'dispatchRequest.fromEffectiveDate.label', default: 'fromDate')}"/>
    <el:dateField name="toEffectiveDateList" size="6" class=""
                  label="${message(code: 'dispatchRequest.toEffectiveDate.label', default: 'fromDate')}"/>
</el:formGroup>
<el:hiddenField name="requestStatusReject"
                value="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED_BY_WORKFLOW}"/>