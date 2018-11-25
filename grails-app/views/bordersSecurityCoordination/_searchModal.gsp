<el:hiddenField name="requestStatus" value="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED}"/>
<el:hiddenField name="employee.id" value="${id}"/>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="documentType"
                     action="autocomplete" name="legalIdentifierId"
                     label="${message(code: 'bordersSecurityCoordination.legalIdentifierId.label', default: 'legalIdentifierId')}"
                     values=""/>
    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="borderCrossingPoint"
                     action="autocomplete" name="borderLocationId"
                     label="${message(code: 'bordersSecurityCoordination.borderLocationId.label', default: 'borderLocationId')}"
                     values=""/>

</el:formGroup>


<el:formGroup>

    <el:range type="date" size="6" name="fromDate"
              label="${message(code: 'bordersSecurityCoordination.fromDate.label')}"/>

    <el:range type="date" size="6" name="toDate"
              label="${message(code: 'bordersSecurityCoordination.toDate.label')}"/>

</el:formGroup>

