<el:formGroup>

    <el:textField name="id" size="6" class=" "
                     label="${message(code: 'bordersSecurityCoordination.id.label', default: 'id')}"/>

    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="employee" action="autocomplete"
                     name="employee.id" isReadOnly="${isReadOnly}"
                     label="${message(code: 'bordersSecurityCoordination.employee.label', default: 'employee')}"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="militaryRank" action="autocomplete"
                     name="militaryRank.id"
                     label="${message(code: 'militaryRank.label', default: 'militaryRank')}"/>

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

<el:formGroup>

    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="documentType"
                     action="autocomplete" name="legalIdentifierId"
                     label="${message(code: 'bordersSecurityCoordination.legalIdentifierId.label', default: 'legalIdentifierId')}"
                     values=""/>

    <el:select valueMessagePrefix="EnumRequestStatus" from="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.values()}"
               name="requestStatus" size="6" class=""
               label="${message(code: 'bordersSecurityCoordination.requestStatus.label', default: 'requestStatus')}"/>
</el:formGroup>
