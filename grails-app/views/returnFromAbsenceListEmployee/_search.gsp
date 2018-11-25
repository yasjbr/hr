<el:formGroup>
    <el:textField name="returnFromAbsenceRequest.id" size="6" class=" "
                  label="${message(code: 'returnFromAbsenceRequest.id.label', default: 'id')}"
                  value=""/>
    <g:render template="/employee/wrapper" model="[isSearch     : true,
                                                   withOutForm  : true,
                                                   size         : 6]"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=" "
            controller="militaryRank"
            action="autocomplete"
            name="militaryRank.id"
            label="${message(code: 'militaryRank.label', default: 'militaryRank')}"/>

    <el:select valueMessagePrefix="EnumAbsenceReason" from="${ps.gov.epsilon.hr.enums.absence.v1.EnumAbsenceReason.values()}" name="actualAbsenceReason" size="6"  class="" label="${message(code:'returnFromAbsenceRequest.actualAbsenceReason.label',default:'actualAbsenceReason')}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="actualReturnDate"  size="6" class="" label="${message(code:'returnFromAbsenceRequest.actualReturnDate.label',default:'actualReturnDate')}" />
    <el:select valueMessagePrefix="EnumListRecordStatus" from="${ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.values()}" name="recordStatus" size="6"  class="" label="${message(code:'returnFromAbsenceListEmployee.recordStatus.label',default:'recordStatus')}" />
</el:formGroup>