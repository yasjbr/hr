
<el:formGroup>
    <el:textField name="employeeId" size="6" class=""
                  label="${message(code: 'employee.id.label', default: 'employeeId')}"/>


    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="employee" action="autocomplete" name="employee.id" label="${message(code:'generalListEmployee.employee.label',default:'employee')}" />

</el:formGroup>



<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="militaryRank" action="autocomplete" name="militaryRank.id" label="${message(code:'militaryRank.label',default:'militaryRank')}" />


    <el:select valueMessagePrefix="EnumListRecordStatus" from="${ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.values()}" name="recordStatus" size="6"  class="" label="${message(code:'generalListEmployee.recordStatus.label',default:'recordStatus')}" />
</el:formGroup>
